/*
 * Copyright 2014 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.annotations.Wrap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Annotation Processor for processing the {@link com.larswerkman.boxer.annotations.Box} annotation
 */
@SupportedAnnotationTypes("com.larswerkman.boxer.annotations.Box")
public class BoxerProcessor extends AbstractProcessor {

    public static final String ADAPTER_PACKAGE_NAME = "com.larswerkman.boxer";
    public static final String ADAPTER_CLASS_NAME = "Adapters$Box";

    public static final String CLASS_EXTENSION = "$Boxer";
    public static final String METHOD_SERIALIZE = "serialize";
    public static final String METHOD_DESERIALIZE = "deserialize";

    private static TypeMirror TYPE_BOXABLE;
    private static TypeMirror TYPE_STRING;
    private static TypeMirror TYPE_LIST;
    private static TypeMirror TYPE_OBJECT;

    private Elements elementUtils;
    private Types typeUtils;

    private Messager log;
    private Filer filer;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        log = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();

        TYPE_OBJECT = elementUtils.getTypeElement("java.lang.Object").asType();
        TYPE_BOXABLE = elementUtils.getTypeElement("com.larswerkman.boxer.Boxable").asType();
        TYPE_STRING = elementUtils.getTypeElement("java.lang.String").asType();
        TYPE_LIST = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("java.util.List"),
                typeUtils.getWildcardType(TYPE_OBJECT, null));

        Set<? extends Element> boxableElements = env.getElementsAnnotatedWith(Box.class);

        //Process all boxable classes
        for (Element element : boxableElements) {
            TypeElement typeElement = (TypeElement) element;

            List<FieldBinding> bindings = parseBoxableFields(typeElement);
            BoxClass boxClass = new BoxClass(element.getSimpleName().toString() + CLASS_EXTENSION,
                    ClassName.get(typeElement), bindings);

            JavaFile file = JavaFile.builder(getPackage(typeElement), boxClass.build()).build();
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                log.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
            }
        }

        return true;
    }

    private List<FieldBinding> parseBoxableFields(TypeElement typeElement){
        List<FieldBinding> bindings = new ArrayList<FieldBinding>();
        for(Element field : getAllElements(typeElement)){

            //Check if the element isn't a of the type field or has a transient modifier
            if(!field.getKind().isField() || field.getModifiers().contains(Modifier.TRANSIENT)){
                continue;
            }

            //Check for a Final modifier.
            if(field.getModifiers().contains(Modifier.FINAL)){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s field can't be final. " +
                                "Consider making it transient or remove the final modifier",
                                field.getSimpleName()), typeElement);
            }

            //Check for a Private modifier then check if it doesn't contain default getters and setters.
            boolean isPrivate = false;
            if(field.getModifiers().contains(Modifier.PRIVATE) &&
                    !hasGetterAndSetter(getAllElements(typeElement), field)){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s field is private, " +
                                "and doesn't have a suitable getter and setter",
                                field.getSimpleName()), typeElement);
            } else if(field.getModifiers().contains(Modifier.PRIVATE)){
                isPrivate = true;
            }

            //Check if the a wrap annotation exists and is assignable from the class.
            TypeMirror wrapType = getWrapAnnotationType(field.getAnnotation(Wrap.class));
            if (wrapType != null && !typeUtils.isAssignable(wrapType, field.asType())) {
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s field can't be annotated with a @Wrap annotation " +
                                        "of a not assignable class of type %s",
                                field.getSimpleName(), wrapType.toString()), typeElement);
            }

            if(!isAcceptable(getType(field.asType()))){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s field can't be of type %s and should implement a boxable interface",
                                field.getSimpleName().toString(), field.asType().toString()));
            }

            //Check for appropriate field and add the appropriate binding.
            String methodType = getMethodType(getType(field.asType()));
            if(isArray(field.asType())){
                bindings.add(new ArrayFieldBinding(field.getSimpleName().toString(),
                        methodType, getStoreType(getType(field.asType())), isPrivate));
            } else if(isList(field.asType())){
                //Get the type of the list. if it has a Wrap annotation override it.
                String listType = getListType(field.asType(), getType(field.asType()));
                if(wrapType != null){
                    listType = wrapType.toString();
                }

                bindings.add(new ListFieldBinding(field.getSimpleName().toString(),
                        methodType, getStoreType(getType(field.asType())), listType, isPrivate));
            } else {
                bindings.add(new FieldBinding(field.getSimpleName().toString(),
                        methodType, getStoreType(getType(field.asType())), isPrivate));
            }
        }
        return bindings;
    }

    private TypeMirror getWrapAnnotationType(Wrap annotation) {
        if(annotation != null) {
            try {
                annotation.value();
            } catch (MirroredTypeException mte) {
                return mte.getTypeMirror();
            }
        }
        return null;
    }

    /**
     * Check if a Field has a getter and setter method.
     *
     * @param elements all elements to search through
     * @param field the current field element we want to check for getters and setters
     *
     * @return true if it has a correct getter and setter method, else false.
     */
    private boolean hasGetterAndSetter(List<? extends Element> elements, Element field){
        String fieldName = field.getSimpleName().toString();
        boolean getter = false;
        boolean setter = false;

        List<ExecutableElement> methods = ElementFilter.methodsIn(elements);
        for (ExecutableElement method : methods) {
            //Checks if it has a get + fieldname method with 0 paramters which returns the same type as the field.
            if (method.getSimpleName().contentEquals(String.format("get%s", capitalize(fieldName)))
                    && method.getParameters().size() == 0
                    && typeUtils.isAssignable(method.getReturnType(), field.asType())) {
                getter = true;
                continue;
            }

            //Checks if it has a set + fieldname method with 1 parameter which accepts the same type as the field is.
            if (method.getSimpleName().contentEquals(String.format("set%s", capitalize(fieldName)))
                    && method.getParameters().size() == 1
                    && typeUtils.isAssignable(method.getParameters().get(0).asType(), field.asType())) {
                setter = true;
            }
        }
        return getter && setter;
    }

    private String getMethodType(TypeMirror type){
        if(isString(type)){
            return "String";
        } else if(isBoxable(type)){
            return "Boxable";
        } else if(isEnum(type)) {
            return "Enum";
        }

        //Its a primitive or wrapper and we should unbox it.
        return unboxed(type);
    }

    private String getStoreType(TypeMirror type){
        if(isBoxable(type)){
            return type.toString();
        } else if(isEnum(type)){
            return type.toString();
        }
        return null;
    }

    private boolean isAcceptable(TypeMirror type) {
        return (isPrimitiveOrWrapper(type)
                || isString(type)
                || isBoxable(type)
                || isEnum(type));
    }

    private String unboxed(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return capitalize(type.toString());
        }
        return capitalize(typeUtils.unboxedType(type).toString());
    }

    private boolean isPrimitiveOrWrapper(TypeMirror type) {
        return (type.getKind().isPrimitive()
                || type.toString().equals("java.lang.Byte")
                || type.toString().equals("java.lang.Short")
                || type.toString().equals("java.lang.Integer")
                || type.toString().equals("java.lang.Long")
                || type.toString().equals("java.lang.Float")
                || type.toString().equals("java.lang.Double")
                || type.toString().equals("java.lang.Character")
                || type.toString().equals("java.lang.Boolean"));
    }

    private boolean isString(TypeMirror type) {
        return typeUtils.isSameType(type, TYPE_STRING);
    }

    private boolean isBoxable(TypeMirror type){
        return typeUtils.isAssignable(type, TYPE_BOXABLE);
    }

    private boolean isEnum(TypeMirror type) {
        Element element = typeUtils.asElement(type);
        return element != null && element
                .getKind() == ElementKind.ENUM;
    }

    private boolean isArray(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY;
    }

    private boolean isList(TypeMirror type){
        return typeUtils.isAssignable(type, TYPE_LIST);
    }

    private TypeMirror getType(TypeMirror type){
        if(isArray(type)){
            return ((ArrayType) type).getComponentType();
        } else if(isList(type)){
            return ((DeclaredType) type).getTypeArguments().get(0);
        }
        return type;
    }

    private String getListType(TypeMirror type, TypeMirror arrayType){
        TypeMirror declaredListType = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("java.util.List"), arrayType);

        //Check if the list type is the abstract List and make it an default ArrayList
        if (typeUtils.isSameType(declaredListType, type)) {
            return "java.util.ArrayList";
        }
        return ((DeclaredType) type).asElement().toString();
    }

    private String getPackage(TypeElement type) {
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (!pkg.isUnnamed()) {
            return pkg.getQualifiedName().toString();
        } else {
            return "";
        }
    }

    private List<? extends Element> getAllElements(TypeElement element){
        List<Element> fieldElements = new ArrayList<Element>(element.getEnclosedElements());

        TypeMirror superType = element.getSuperclass();
        while(!(superType instanceof NoType)){
            TypeElement typeElement = elementUtils.getTypeElement(superType.toString());
            fieldElements.addAll(typeElement.getEnclosedElements());
            superType = typeElement.getSuperclass();
        }
        return fieldElements;
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
