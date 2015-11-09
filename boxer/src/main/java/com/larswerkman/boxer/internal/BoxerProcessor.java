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

import com.larswerkman.boxer.annotations.*;
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
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.List;

/**
 * Annotation Processor for processing the {@link com.larswerkman.boxer.annotations.Box} annotation
 */
@SupportedAnnotationTypes({
        "com.larswerkman.boxer.annotations.Box",
        "com.larswerkman.boxer.annotations.Adapter"
})
public class BoxerProcessor extends AbstractProcessor {

    public static final String PROCESSOR_NAME = "com.larswerkman.boxer.internal.BoxerProcessor";

    public static final String ADAPTER_PACKAGE_NAME = "com.larswerkman.boxer";
    public static final String ADAPTER_CLASS_NAME = "GeneratedAdapters$$Boxer";
    public static final String ADAPTER_METHOD_GET = "getAdapter";

    public static final String CLASS_EXTENSION = "_TypeAdapter";
    public static final String METHOD_SERIALIZE = "serialize";
    public static final String METHOD_DESERIALIZE = "deserialize";

    private static TypeMirror TYPE_STRING;
    private static TypeMirror TYPE_LIST;
    private static TypeMirror TYPE_OBJECT;
    private static TypeMirror TYPE_ADAPTER;
    private static TypeMirror TYPE_BOXER;
    private static TypeMirror TYPE_BOXER_WILDCARD;

    private List<AdapterBinding> adapters = new ArrayList<AdapterBinding>();

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
        TYPE_STRING = elementUtils.getTypeElement("java.lang.String").asType();
        TYPE_LIST = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("java.util.List"),
                typeUtils.getWildcardType(TYPE_OBJECT, null));
        TYPE_ADAPTER = elementUtils.getTypeElement("com.larswerkman.boxer.TypeAdapter").asType();
        TYPE_BOXER = ((DeclaredType) elementUtils.getTypeElement("com.larswerkman.boxer.Boxer").asType())
                .asElement().asType();
        TYPE_BOXER_WILDCARD = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("com.larswerkman.boxer.Boxer"),
                typeUtils.getWildcardType(TYPE_OBJECT, null)
        );

        Set<? extends Element> boxableElements = env.getElementsAnnotatedWith(Box.class);
        Set<? extends Element> adapterElements = env.getElementsAnnotatedWith(Adapter.class);

        //Process all adapters classes
        if(!adapterElements.isEmpty()) {
            adapters.addAll(parseTypeAdapters(adapterElements));
        }

        //Process all boxable classes
        for (Element element : boxableElements) {
            TypeElement typeElement = (TypeElement) element;
            if(!hasEmptyConstructor(typeElement)){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s class must contain a non-args constructor",
                                typeElement.getSimpleName()), typeElement);
            }

            List<MethodBinding> methodBindings = parseMethodAnnotations(typeElement);
            List<FieldBinding> bindings = parseBoxableFields(typeElement);

            String name = getCanonicalName(typeElement) + CLASS_EXTENSION;
            BoxClass boxClass = new BoxClass(name,
                    ClassName.get(typeElement), bindings, methodBindings);

            JavaFile file = JavaFile.builder(getPackage(typeElement), boxClass.build()).build();
            try {
                file.writeTo(filer);
                adapters.add(new AdapterBinding(ClassName.get(file.packageName, name),typeElement.asType()));
            } catch (IOException e) {
                log.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
            }
        }

        if(!adapters.isEmpty()) {
            JavaFile adaptersClass = JavaFile.builder(ADAPTER_PACKAGE_NAME,
                    new AdaptersClass(adapters).build()).build();
            try {
                adaptersClass.writeTo(filer);
                adapters.clear();
            } catch (IOException e) {
                log.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return true;
    }

    private List<AdapterBinding> parseTypeAdapters(Set<? extends Element> elements){
        List<AdapterBinding> adapters = new ArrayList<AdapterBinding>();
        for(Element element : elements){
            TypeElement typeElement = (TypeElement) element;
            TypeMirror superType = typeElement.getSuperclass();

            if(!hasEmptyConstructor(typeElement)){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s class must contain a non-args constructor",
                                typeElement.getSimpleName()), typeElement);
            }

            //Check for superclass without A WildcardType
            Element superElement = typeUtils.asElement(superType);
            if(typeUtils.isSameType(superElement.asType(), TYPE_ADAPTER)){

                //Returns first TypeArgument which will be the target class.
                TypeMirror targetType = ((DeclaredType) superType).getTypeArguments().get(0);
                adapters.add(new AdapterBinding(ClassName.get(typeElement), targetType));
            } else {
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s class must extend TypeAdapter class",
                                typeElement.getSimpleName()), typeElement);
            }
        }
        return adapters;
    }

    private List<MethodBinding> parseMethodAnnotations(TypeElement typeElement){
        List<MethodBinding> bindings = new ArrayList<MethodBinding>();
        for(Element element : getAllElements(typeElement)){

            if(!element.getKind().equals(ElementKind.METHOD)
                    || !hasAcceptableMethodAnnotation(element)){
                continue;
            }

            if(element.getModifiers().contains(Modifier.PRIVATE)){
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Annotated method %s should be accessible",
                                element.getSimpleName()), typeElement);
            }

            ExecutableElement method = (ExecutableElement) element;
            boolean hasArgument = false;

            List<? extends VariableElement> params = method.getParameters();
            if(params.size() == 1){
                if(isBoxer(params.get(0).asType())){
                    hasArgument = true;
                } else {
                    log.printMessage(Diagnostic.Kind.WARNING, params.get(0).asType().toString());
                    log.printMessage(Diagnostic.Kind.WARNING, TYPE_BOXER.toString());
                    log.printMessage(Diagnostic.Kind.WARNING, TYPE_BOXER_WILDCARD.toString());
                    log.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotated method %s argument must be of type Boxer",
                                    element.getSimpleName()), typeElement);
                }
            } else if(params.size() > 1) {
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Annotated method %s can't have more than 1 argument",
                                element.getSimpleName()), typeElement);
            }

            Serialize serialize = element.getAnnotation(Serialize.class);
            if(serialize != null){
                bindings.add(new MethodBinding(element.getSimpleName().toString(),
                        MethodBinding.Method.SERIALIZE, serialize.value(), hasArgument));
            }

            Deserialize deserialize = element.getAnnotation(Deserialize.class);
            if(deserialize != null){
                bindings.add(new MethodBinding(element.getSimpleName().toString(),
                        MethodBinding.Method.DESERIALIZE, deserialize.value(), hasArgument));
            }
        }
        return bindings;
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
                        String.format("%s field can't be of type %s",
                                field.getSimpleName().toString(), field.asType().toString()));
            }

            //Check for appropriate field and add the appropriate binding.
            String methodType = getMethodType(getType(field.asType()));
            if(isArray(field.asType())){
                bindings.add(new ArrayFieldBinding(field.getSimpleName().toString(),
                        getStoreType(getType(field.asType())), methodType , isPrivate));
            } else if(isList(field.asType())){
                //Get the type of the list. if it has a Wrap annotation override it.
                TypeMirror listType = getListType(field.asType(), getType(field.asType()));
                if(wrapType != null) {
                    listType = wrapType;
                }
                ClassName listTypeName = ClassName.get((TypeElement) ((DeclaredType) listType).asElement());

                bindings.add(new ListFieldBinding(field.getSimpleName().toString(),
                        getStoreType(getType(field.asType())), methodType, listTypeName, isPrivate));
            } else {
                bindings.add(new FieldBinding(field.getSimpleName().toString(),
                        getStoreType(getType(field.asType())), methodType, isPrivate));
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
        } else if(isEnum(type)) {
            return "Enum";
        } else if(isAdapter(type) || isBoxable(type)){
            return "";
        }

        //Its a primitive or wrapper and we should unbox it.
        return unboxed(type);
    }

    private TypeMirror getStoreType(TypeMirror type){
        if(isBoxable(type)){
            return type;
        } else if(isEnum(type)){
            return type;
        } else if(isAdapter(type)){
            return type;
        }
        return null;
    }

    private boolean hasAcceptableMethodAnnotation(Element element){
        return (element.getAnnotation(Serialize.class) != null) ||
                (element.getAnnotation(Deserialize.class) != null);
    }

    private boolean isAcceptable(TypeMirror type) {
        return (isPrimitiveOrWrapper(type)
                || isString(type)
                || isBoxable(type)
                || isEnum(type)
                || isAdapter(type));
    }

    private boolean isAdapter(TypeMirror type){
        for(AdapterBinding adapter : adapters){
            if(typeUtils.isSameType(type, adapter.getType())){
                return true;
            }
        }
        return false;
    }

    private boolean isBoxer(TypeMirror type){
        return typeUtils.isAssignable(type, TYPE_BOXER)
                || typeUtils.isSameType(type, TYPE_BOXER_WILDCARD);
    }

    private boolean hasEmptyConstructor(TypeElement element){
        List<ExecutableElement> constructors = ElementFilter
                .constructorsIn(element.getEnclosedElements());
        if(constructors.isEmpty()){
            return true;
        }
        for(ExecutableElement constructor : constructors){
            if(constructor.getParameters().isEmpty()){
                return true;
            }
        }
        return false;
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
        return typeUtils.asElement(type) != null && findAnnotationMirror(typeUtils.asElement(type), Box.class) != null;
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

    private TypeMirror getListType(TypeMirror type, TypeMirror arrayType){
        TypeMirror declaredListType = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("java.util.List"), arrayType);

        //Check if the list type is the abstract List and make it an default ArrayList
        if (typeUtils.isSameType(declaredListType, type)) {
            return elementUtils.getTypeElement("java.util.ArrayList").asType();
        }
        return type;
    }

    private String getPackage(TypeElement type) {
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (!pkg.isUnnamed()) {
            return pkg.getQualifiedName().toString();
        } else {
            return "";
        }
    }

    private String getCanonicalName(TypeElement type){
        return type.getQualifiedName().toString()
                .replace(getPackage(type) + ".", "")
                .replace(".", "$");
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

    public AnnotationMirror findAnnotationMirror(Element annotatedElement, Class<? extends Annotation> annotationClass) {
        List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();

        for (AnnotationMirror annotationMirror : annotationMirrors) {
            TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if (isAnnotation(annotationElement, annotationClass)) {
                return annotationMirror;
            }
        }
        return null;
    }

    private boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
        return annotation.getQualifiedName().toString().equals(annotationClass.getName());
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
