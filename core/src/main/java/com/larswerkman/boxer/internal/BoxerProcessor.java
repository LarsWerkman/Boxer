package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.Boxer;
import com.larswerkman.boxer.annotations.Box;
import com.larswerkman.boxer.annotations.Packet;
import com.larswerkman.boxer.annotations.Wrap;
import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.*;

/**
 * Created by lars on 13-11-14.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
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
            //Check if implements Boxable interface
            if (typeUtils.isAssignable(typeElement.asType(), TYPE_BOXABLE)) {
                List<PackedField> fields = new ArrayList<PackedField>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for (Element child : enclosedElements) {
                    Packet annotation = child.getAnnotation(Packet.class);
                    if (annotation != null) {
                        String name = child.getSimpleName().toString();
                        Modifier modifier = null;
                        for (Modifier mod : child.getModifiers()) {
                            if (mod == Modifier.PUBLIC || mod == Modifier.PRIVATE || mod == Modifier.PROTECTED) {
                                modifier = mod;
                            } else if (mod == Modifier.FINAL) {
                                log.printMessage(Diagnostic.Kind.ERROR,
                                        "Packet annotation can't be placed on final types");
                                return true;
                            }
                        }

                        Wrap wrap = child.getAnnotation(Wrap.class);
                        TypeMirror wrapType = null;
                        if (wrap != null) {
                            try {
                                wrap.value();
                            } catch (MirroredTypeException e) {
                                wrapType = e.getTypeMirror();
                            }
                        }

                        TypeMirror type = ((VariableElement) child).asType();
                        if (isAcceptable(type) || isEnum(type)) {
                            fields.add(new PackedField(name, type, modifier, false, wrapType));
                        } else if (isArray(type)) {
                            TypeMirror arrayType = getTypeOfArray(type);
                            if (arrayType != null) {
                                if (isAcceptable(arrayType)) {
                                    fields.add(new PackedField(name, type, modifier, true, wrapType));
                                } else {
                                    log.printMessage(Diagnostic.Kind.ERROR, arrayType.toString());
                                }
                            } else {
                                log.printMessage(Diagnostic.Kind.ERROR, type.toString());
                            }
                        } else {
                            log.printMessage(Diagnostic.Kind.ERROR,
                                    "Packet annotation can only be placed on primitives " +
                                            "or one objects the are assignable from the Boxable interface");
                            return true;
                        }
                    }
                }

                if (fields.size() > 0) {
                    brewJava(typeElement, fields);
                }
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
                    .beginType(simple, "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL))
                    .beginMethod("void", METHOD_WRITE, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), original, "boxable", "Boxer", "boxer");
            for (PackedField field : fields) {
                if (!field.isArray()) {
                    if (isPrimitiveOrWrapper(field.type()) || typeUtils.isSameType(field.type(), TYPE_STRING)) {
                        writer.emitStatement("boxer.add%s(\"%s\", boxable.%s)",
                                mappingName(field.type()), field.name(), field.getter());
                    } else if (typeUtils.isAssignable(field.type(), TYPE_BOXABLE)) {
                        writer.emitStatement("boxer.put(\""
                                        + field.name() + "\"," + field.type()
                                        + CLASS_EXTENSION + "." + METHOD_WRITE
                                        + "(boxable." + field.getter() + "))"
                        );//TODO
                    } else if (isEnum(field.type())) {
                        writer.emitStatement("boxer.add%s(\"%s\", boxable.%s)",
                                mappingName(field.type()), field.name(), field.getter());
                    }
                } else {
                    TypeMirror arrayType = getTypeOfArray(field.type());//FIXME
                    if (arrayType != null) {
                        if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                            writer.emitStatement("List<Object> %sArray = new ArrayList<Object>()", field.name());
                        } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                            writer.emitStatement("List<HashMap<String, Object>> %sArray = new ArrayList<HashMap<String, Object>>()", field.name());
                        }

                        if (field.type().getKind() == TypeKind.ARRAY) {
                            writer.beginControlFlow("for(int i = 0; i < boxable.%s.length; i++)", field.name());
                            if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                                writer.emitStatement("%sArray.add(boxable.%s[i])", field.getter());
                            } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                                writer.emitStatement("%sArray.add(%s.%s(boxable.%s[i]))",
                                        field.name(), arrayType + CLASS_EXTENSION,
                                        METHOD_WRITE, field.getter()
                                );
                            }
                            writer.endControlFlow();
                        } else {
                            writer.beginControlFlow("for(int i = 0; i < boxable.%s.size(); i++)", field.name());
                            if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                                writer.emitStatement("%sArray.add(boxable.%s.get(i))", field.getter());
                            } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                                writer.emitStatement("%sArray.add(%s.%s(boxable.%s.get(i)))",
                                        field.name(), arrayType + CLASS_EXTENSION,
                                        METHOD_WRITE, field.getter()
                                );
                            }
                            writer.endControlFlow();
                        }
                        writer.emitStatement("map.put(\"%s\", %sArray)", field.name(), field.name());
                    }
                }
            }
            writer.endMethod()
                    .beginMethod(classElement.getQualifiedName().toString(),
                            METHOD_READ, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "Boxer", "boxer")
                    .emitStatement(original + " boxable = new " + original + "()");
            for (PackedField field : fields) {
                if (!field.isArray()) {
                    if (isPrimitiveOrWrapper(field.type()) || typeUtils.isSameType(field.type(), TYPE_STRING)) {
                        writer.emitStatement("boxable.%s", field.setter(
                                String.format("boxer.get%s(\"%s\")", mappingName(field.type()), field.name()))
                        );
                    } else if (typeUtils.isAssignable(field.type(), TYPE_BOXABLE)) {
                        writer.emitStatement("boxable." + field.setter(
                                field.type() + CLASS_EXTENSION + "." + METHOD_READ + "((HashMap) map.get(\"" + field.name() + "\"))"
                        ));
                    } else if (isEnum(field.type())) {
                        writer.emitStatement("boxable.%s", field.setter(
                                String.format("boxer.get%s(\"%s\", %s.class)", mappingName(field.type()), field.name(), field.type()))
                        );
                    }
                } else {
                    TypeMirror arrayType = getTypeOfArray(field.type());
                    if (arrayType != null) {
                        if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                            writer.emitStatement("List<Object> %sArray = (ArrayList) map.get(\"%s\")", field.name(), field.name());
                        } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                            writer.emitStatement("List<HashMap<String, Object>> %sArray = (ArrayList) map.get(\"%s\")", field.name(), field.name());
                        }

                        if (field.type().getKind() == TypeKind.ARRAY) {
                            writer.emitStatement("%s %sTemp = new %s[%sArray.size()]", field.type(), field.name(), arrayType, field.name());
                            writer.beginControlFlow("for(int i = 0; i < %sArray.size(); i++)", field.name());
                            if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                                writer.emitStatement("%sTemp[i] = (%s) %sArray.get(i)", field.name(), arrayType, field.name());
                            } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                                writer.emitStatement("%sTemp[i] = %s%s.%s((HashMap) %sArray.get(i))",
                                        field.name(), arrayType, CLASS_EXTENSION, METHOD_READ, field.name()
                                );
                            }
                            writer.endControlFlow();
                        } else {
                            TypeMirror rawListType = typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.List"), arrayType);
                            if (typeUtils.isSameType(rawListType, field.type())) {
                                if (field.wrapper() != null) {
                                    writer.emitStatement("%s %sTemp = new %s<%s>()", field.type(), field.name(), field.wrapper(), arrayType);
                                } else {
                                    writer.emitStatement("%s %sTemp = new ArrayList<%s>()", field.type(), field.name(), arrayType);
                                }
                            } else {
                                writer.emitStatement("%s %sTemp = new %s()", field.type(), field.name(), field.type());
                            }
                            writer.beginControlFlow("for(int i = 0; i < %sArray.size(); i++)", field.name());
                            if (isPrimitiveOrWrapper(arrayType) || typeUtils.isSameType(arrayType, TYPE_STRING)) {
                                writer.emitStatement("%sTemp.addBoxable((%s) %sArray.get(i))", field.name(), arrayType, field.name());
                            } else if (typeUtils.isAssignable(arrayType, TYPE_BOXABLE)) {
                                writer.emitStatement("%sTemp.addBoxable(%s%s.%s((HashMap) %sArray.get(i)))",
                                        field.name(), arrayType, CLASS_EXTENSION, METHOD_READ, field.name()
                                );
                            }
                            writer.endControlFlow();
                        }
                        writer.emitStatement("boxable." + field.setter("%sTemp"), field.name());
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

    private String mappingName(TypeMirror type) {
        if (type.toString().equals("boolean") || type.toString().equals("java.lang.Boolean")) {
            return "Boolean";
        } else if (type.toString().equals("byte") || type.toString().equals("java.lang.Byte")) {
            return "Byte";
        } else if (type.toString().equals("char") || type.toString().equals("java.lang.Character")) {
            return "Char";
        } else if (type.toString().equals("short") || type.toString().equals("java.lang.Short")) {
            return "Short";
        } else if (type.toString().equals("int") || type.toString().equals("java.lang.Integer")) {
            return "Int";
        } else if (type.toString().equals("long") || type.toString().equals("java.lang.Long")) {
            return "Long";
        } else if (type.toString().equals("float") || type.toString().equals("java.lang.Float")) {
            return "Float";
        } else if (type.toString().equals("double") || type.toString().equals("java.lang.Double")) {
            return "Double";
        } else if (type.toString().equals("int") || type.toString().equals("java.lang.Integer")) {
            return "Int";
        } else if (type.toString().equals("int") || type.toString().equals("java.lang.Integer")) {
            return "Int";
        } else if(typeUtils.isSameType(type, TYPE_STRING)){
            return "String";
        } else if (isEnum(type)){
            return "Enum";
        }
        return "";
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
}
