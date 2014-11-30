package com.larswerkman.boxer.internal;

import com.larswerkman.boxer.Box;
import com.larswerkman.boxer.Packet;
import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
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
@SupportedAnnotationTypes("com.larswerkman.boxer.Box")
public class BoxerProcessor extends AbstractProcessor {

    public static final String CLASS_EXTENSION = "Boxer";
    public static final String METHOD_READ = "read";
    public static final String METHOD_WRITE = "write";

    private Messager log;
    private Filer filer;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        log = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();

        TypeElement boxableType = elementUtils.getTypeElement("com.larswerkman.boxer.Boxable");
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Box.class);
        for(Element element : elements){
            TypeElement typeElement = (TypeElement) element;
            //Check if implements Boxable interface
            if(typeUtils.isAssignable(typeElement.asType(), boxableType.asType())){
                List<PackedField> fields = new ArrayList<PackedField>();
                List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                for(Element child : enclosedElements){
                    Packet annotation = child.getAnnotation(Packet.class);
                    if(annotation != null){

                        String name = child.getSimpleName().toString();
                        Modifier modifier = null;
                        for(Modifier mod : child.getModifiers()){
                            if(mod == Modifier.PUBLIC || mod == Modifier.PRIVATE || mod == Modifier.PROTECTED){
                                modifier = mod;
                            } else if(mod == Modifier.FINAL){
                                log.printMessage(Diagnostic.Kind.ERROR,
                                        "Packet annotation can't be placed on final types");
                                return true;
                            }
                        }
                        TypeMirror type = ((VariableElement) child).asType();
                        if(type.getKind().isPrimitive() || typeUtils.isAssignable(type, boxableType.asType())) {
                            fields.add(new PackedField(name, type, modifier));
                        } else {
                            log.printMessage(Diagnostic.Kind.ERROR,
                                    "Packet annotation can only be placed on primitives " +
                                            "or one objects the are assignable from the Boxable interface");
                            return true;
                        }
                    }
                }

                if(fields.size() > 0){
                    brewJava(typeElement, fields);
                }
            }
        }
        return true;
    }

    private void brewJava(TypeElement classElement, List<PackedField> fields){
        try {
            String original = classElement.getSimpleName().toString();
            String originalQualified = classElement.getQualifiedName().toString();
            String simple = original + CLASS_EXTENSION;
            String qualified = originalQualified + CLASS_EXTENSION;

            JavaFileObject jfo = filer.createSourceFile(qualified);
            JavaWriter writer = new JavaWriter(jfo.openWriter());
            writer.emitPackage(getPackage(classElement))
                    .emitImports(HashMap.class.getName(), originalQualified)
                    .beginType(simple + "", "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL))
                    .beginMethod("HashMap<String, Object>", METHOD_WRITE, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), original, "boxable")
                    .emitStatement("HashMap<String, Object> map = new HashMap<String, Object>()");
            for(PackedField field : fields) {
                if(field.type().getKind().isPrimitive()) {
                    writer.emitStatement("map.put(\"" + field.name() + "\", boxable." + field.getter() + ")");
                } else {
                    writer.emitStatement("map.put(\""
                            + field.name() + "\"," + field.type()
                            + CLASS_EXTENSION + "." + METHOD_WRITE
                            + "(boxable." + field.getter() + "))"
                    );
                }
            }
            writer.emitStatement("return map")
                    .endMethod()
                    .beginMethod(classElement.getQualifiedName().toString(),
                            METHOD_READ, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), "HashMap<String, Object>", "map")
                    .emitStatement(original + " boxable = new " + original + "()");
            for(PackedField field : fields){
                if(field.type().getKind().isPrimitive()) {
                    writer.emitStatement("boxable." + field.setter("(" + field.type() + ") map.get(\"" + field.name() + "\")"));
                } else {
                    writer.emitStatement("boxable." + field.setter(
                            field.type() + CLASS_EXTENSION + "." + METHOD_READ + "((HashMap) map.get(\"" + field.name() + "\"))"
                    ));
                }
            }
            writer.emitStatement("return boxable")
                    .endMethod()
                    .endType()
                    .close();
        } catch (IOException e){
            log.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
        }
    }

    private String getPackage(TypeElement type) throws IOException {
        PackageElement pkg = processingEnv.getElementUtils().getPackageOf(type);
        if (!pkg.isUnnamed()) {
            return pkg.getQualifiedName().toString();
        } else {
            return "";
        }
    }
}
