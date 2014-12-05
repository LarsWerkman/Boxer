package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.annotations.Packet;
import com.larswerkman.boxer.annotations.Wrap;
import com.squareup.javawriter.JavaWriter;

import javax.annotation.Generated;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.*;

/**
 * Created by lars on 13-11-14.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.larswerkman.boxer.annotations.Box")
public class BoxerProcessor extends AbstractProcessor {

    public static final String CLASS_EXTENSION = "Boxer";
    public static final String METHOD_READ = "read";
    public static final String METHOD_WRITE = "write";

    private static TypeMirror TYPE_BOXABLE;
    private static TypeMirror TYPE_STRING;
    private static TypeMirror TYPE_LIST;

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

        TYPE_BOXABLE = elementUtils.getTypeElement("com.larswerkman.boxer.Boxable").asType();
        TYPE_STRING = elementUtils.getTypeElement("java.lang.String").asType();
        TYPE_LIST = typeUtils.getDeclaredType(
                elementUtils.getTypeElement("java.util.List"),
                typeUtils.getWildcardType(TYPE_BOXABLE, null));

        Set<? extends Element> elements = env.getElementsAnnotatedWith(Box.class);
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            //Check if implements a Boxable interface
            if (typeUtils.isAssignable(typeElement.asType(), TYPE_BOXABLE)) {

                //Check if the class has a usable public/no-args constructor
                boolean usableConstructor = false;
                List<ExecutableElement> constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
                for (ExecutableElement constructor : constructors) {
                    if (constructor.getParameters().size() == 0) {
                        usableConstructor = true;
                        break;
                    }
                }
                if (!usableConstructor) {
                    log.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s class should have a public constructor with no-args", typeElement.getSimpleName())
                    );
                    return true;
                }

                List<PackedField> fields = new ArrayList<PackedField>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for (Element child : enclosedElements) {

                    //Check if the child element has a Packet Annotation
                    Packet annotation = child.getAnnotation(Packet.class);
                    if (annotation != null) {
                        String name = child.getSimpleName().toString();

                        //Find out of the child element is accessible
                        Modifier modifier = null;
                        for (Modifier mod : child.getModifiers()) {
                            if (mod == Modifier.PUBLIC) {
                                modifier = mod;
                            } else if (mod == Modifier.PRIVATE || mod == Modifier.PROTECTED) {
                                boolean getter = false;
                                boolean setter = false;
                                List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());
                                for (ExecutableElement method : methods) {
                                    if (method.getSimpleName().contentEquals(String.format("get%s", capitalize(name)))
                                            && method.getParameters().size() == 0
                                            && typeUtils.isAssignable(method.getReturnType(), child.asType())) {
                                        getter = true;
                                        continue;
                                    }

                                    if (method.getSimpleName().contentEquals(String.format("set%s", capitalize(name)))
                                            && method.getParameters().size() == 1
                                            && typeUtils.isAssignable(method.getParameters().get(0).asType(), child.asType())) {
                                        setter = true;
                                    }
                                }
                                if (!getter || !setter) {
                                    log.printMessage(Diagnostic.Kind.ERROR,
                                            String.format("%s @Packet fied have default getter and setter methods or should be public",
                                                    child.getSimpleName()));
                                    return true;
                                }
                                modifier = mod;
                            } else if (mod == Modifier.FINAL) {
                                log.printMessage(Diagnostic.Kind.ERROR,
                                        String.format("%s @Packet fied cannot be final",
                                                child.getSimpleName())
                                );
                                return true;
                            }
                        }

                        //Check if @Packet annotated field as has @Wrap annotation
                        Wrap wrap = child.getAnnotation(Wrap.class);
                        TypeMirror wrapType = null;
                        if (wrap != null) {
                            try {
                                wrap.value();
                            } catch (MirroredTypeException e) {
                                wrapType = e.getTypeMirror();
                                if (!typeUtils.isAssignable(wrapType, child.asType())) {
                                    log.printMessage(Diagnostic.Kind.ERROR,
                                            String.format("%s @Wrap annotated value %s is not assignable from %s",
                                                    child.getSimpleName(), wrapType.toString(), child.asType().toString())
                                    );
                                    return true;
                                }
                            }
                        }

                        //Check if the field type is acceptable
                        TypeMirror type = child.asType();
                        if (isAcceptable(type) || isEnum(type)) {
                            fields.add(new PackedField(name, type, modifier, false, wrapType));
                        } else if (isArray(type)) {
                            TypeMirror arrayType = getTypeOfArray(type);
                            if (arrayType != null) {
                                if (isAcceptable(arrayType)) {
                                    fields.add(new PackedField(name, type, modifier, true, wrapType));
                                } else {
                                    log.printMessage(Diagnostic.Kind.ERROR,
                                            String.format("%s @Packed annotation can only be placed on arrays with type of" +
                                                            ": primitives and wrappers, Enum classes and objects " +
                                                            "implementing the boxable interface with @Box annotation",
                                                    child.getSimpleName())
                                    );
                                    log.printMessage(Diagnostic.Kind.ERROR, arrayType.toString());
                                }
                            } else {
                                log.printMessage(Diagnostic.Kind.ERROR,
                                        String.format("%s @Packed annotated array should have an specified type",
                                                child.getSimpleName())
                                );
                                return true;
                            }
                        } else {
                            log.printMessage(Diagnostic.Kind.ERROR,
                                    String.format("%s @Packed annotation can only be placed on: primitives and wrappers," +
                                                    "Enum classes and objects implementing the boxable interface with @Box annotation",
                                            child.getSimpleName())
                            );
                            return true;
                        }
                    }
                }

                if (fields.size() > 0) {
                    brewJava(typeElement, fields);
                }
            } else {
                log.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s @Box annotated class should always implement the Boxable interface",
                                element.getSimpleName())
                );
                return true;
            }
        }
        return true;
    }

    private void brewJava(TypeElement classElement, List<PackedField> fields) {
        try {
            String original = classElement.getSimpleName().toString();
            String originalQualified = classElement.getQualifiedName().toString();
            String simple = original + CLASS_EXTENSION;
            String qualified = originalQualified + CLASS_EXTENSION;

            JavaFileObject jfo = filer.createSourceFile(qualified);
            JavaWriter writer = new JavaWriter(jfo.openWriter());
            writer.emitPackage(getPackage(classElement))
                    .emitImports(Boxer.class.getName(), List.class.getName(), ArrayList.class.getName(), originalQualified)
                    .emitAnnotation(Generated.class, JavaWriter.stringLiteral(getClass().getCanonicalName()))
                    .beginType(simple, "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL))
                    .beginMethod("void", METHOD_WRITE, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), original, "boxable", "Boxer", "boxer");
            for (PackedField field : fields) {

                //Check if its an array
                if (!field.isArray()) {

                    //Check what type the field is
                    if (isPrimitiveOrWrapper(field.type())) {
                        writer.emitStatement("boxer.add%s(\"%s\", boxable.%s)",
                                unboxedName(field.type()), field.name(), field.getter()
                        );
                    } else if (isString(field.type())) {
                        writer.emitStatement("boxer.addString(\"%s\", boxable.%s)", field.name(), field.getter());
                    } else if (isEnum(field.type())) {
                        writer.emitStatement("boxer.addEnum(\"%s\", boxable.%s)", field.name(), field.getter());
                    } else {
                        writer.emitStatement("boxer.addBoxable(\"%s\", boxable.%s)", field.name(), field.getter());
                    }
                } else {

                    //Check if type if is [] or List and use appropriete signature
                    String signature = field.type().getKind() == TypeKind.ARRAY ? "Array" : "List";
                    if (isPrimitiveOrWrapper(field.type())) {
                        writer.emitStatement("boxer.add%s%s(\"%s\", boxable.%s)",
                                unboxedName(field.type()), signature, field.name(), field.getter()
                        );
                    } else if (isString(field.type())) {
                        writer.emitStatement("boxer.addString%s(\"%s\", boxable.%s)", signature, field.name(), field.getter());
                    } else if (isEnum(field.type())) {
                        writer.emitStatement("boxer.addEnum%s(\"%s\", boxable.%s)", signature, field.name(), field.getter());
                    } else {
                        writer.emitStatement("boxer.addBoxable%s(\"%s\", boxable.%s)", signature, field.name(), field.getter());
                    }
                }
            }

            writer.endMethod()
                    .beginMethod(classElement.getQualifiedName().toString(),
                            METHOD_READ, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "Boxer", "boxer")
                    .emitStatement(original + " boxable = new " + original + "()");

            for (PackedField field : fields) {

                //Check if its an Array
                if (!field.isArray()) {

                    //Check what type the field is
                    if (isPrimitiveOrWrapper(field.type())) {
                        writer.emitStatement("boxable.%s", field.setter(
                                        String.format("boxer.get%s(\"%s\")", unboxedName(field.type()), field.name()))
                        );
                    } else if (isString(field.type())) {
                        writer.emitStatement("boxable.%s", field.setter(
                                        String.format("boxer.getString(\"%s\")", field.name()))
                        );
                    } else if (isEnum(field.type())) {
                        writer.emitStatement("boxable.%s", field.setter(
                                        String.format("boxer.getEnum(\"%s\", %s.class)", field.name(), field.type()))
                        );
                    } else {
                        writer.emitStatement("boxable.%s", field.setter(
                                        String.format("boxer.getBoxable(\"%s\", %s.class)", field.name(), field.type()))
                        );
                    }
                } else {

                    //get Array type
                    TypeMirror arrayType = getTypeOfArray(field.type());
                    if (arrayType != null) {

                        //Check if its an [] or a List
                        if (field.type().getKind() == TypeKind.ARRAY) {

                            //Check what type the field is
                            if (isPrimitiveOrWrapper(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.get%sArray(\"%s\")", unboxedName(field.type()), field.name()))
                                );
                            } else if (isString(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getStringArray(\"%s\")", field.name()))
                                );
                            } else if (isEnum(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getEnumArray(\"%s\", %s.class)", field.name(), arrayType))
                                );
                            } else {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getBoxableArray(\"%s\", %s.class)", field.name(), arrayType))
                                );
                            }
                        } else {

                            //Check if the list is of type list or of types like: ArrayList, Stack etc.
                            String listtype = field.type().toString();
                            TypeMirror declaredListType = typeUtils.getDeclaredType(
                                    elementUtils.getTypeElement("java.util.List"), arrayType
                            );
                            if (typeUtils.isSameType(declaredListType, field.type())) {
                                if (field.wrapper() != null) {
                                    listtype = field.wrapper().toString();
                                } else {
                                    listtype = "ArrayList";
                                }
                            }

                            //Check what type the field is
                            if (isPrimitiveOrWrapper(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.get%sList(\"%s\", %s.class)",
                                                        unboxedName(field.type()), field.name(), listtype))
                                );
                            } else if (isString(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getStringList(\"%s\", %s.class)",
                                                        field.name(), listtype))
                                );
                            } else if (isEnum(arrayType)) {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getEnumList(\"%s\", %s.class, %s.class)",
                                                        field.name(), arrayType, listtype))
                                );
                            } else {
                                writer.emitStatement("boxable.%s", field.setter(
                                                String.format("boxer.getBoxableList(\"%s\", %s.class, %s.class)",
                                                        field.name(), arrayType, listtype))
                                );
                            }
                        }
                    }
                }
            }
            writer.emitStatement("return boxable")
                    .endMethod()
                    .endType()
                    .close();
        } catch (IOException e) {
            log.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
        }
    }

    private boolean isAcceptable(TypeMirror type) {
        return (isPrimitiveOrWrapper(type)
                || typeUtils.isAssignable(type, TYPE_BOXABLE)
                || typeUtils.isSameType(type, TYPE_STRING));
    }

    private String unboxedName(TypeMirror type) {
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

    public boolean isString(TypeMirror type) {
        return typeUtils.isSameType(type, TYPE_STRING);
    }

    public boolean isEnum(TypeMirror type) {
        return typeUtils.asElement(type)
                .getKind() == ElementKind.ENUM;
    }

    private boolean isArray(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY || typeUtils.isAssignable(type, TYPE_LIST);
    }

    private TypeMirror getTypeOfArray(TypeMirror type) {
        if (type.getKind() == TypeKind.ARRAY) {
            return elementUtils.getTypeElement(type.toString().substring(0, type.toString().length() - 2)).asType();
        } else {
            return ((DeclaredType) type).getTypeArguments().get(0);
        }
    }

    private String getPackage(TypeElement type) throws IOException {
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (!pkg.isUnnamed()) {
            return pkg.getQualifiedName().toString();
        } else {
            return "";
        }
    }

    private String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
