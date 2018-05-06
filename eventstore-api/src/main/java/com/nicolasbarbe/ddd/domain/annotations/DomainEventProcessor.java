package com.nicolasbarbe.ddd.domain.annotations;


import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes("com.nicolasbarbe.ddd.domain.annotations.DomainEvent")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DomainEventProcessor extends AbstractProcessor {

    private static final String SUFFIX = "Impl";

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DomainEvent.class)) {
            
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only interfaces can be annotated with " + DomainEvent.class.getSimpleName() );
            }

            TypeElement typeElement = (TypeElement) annotatedElement;

            String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            String className   = typeElement.getSimpleName().toString() + SUFFIX;



            List fieldSpecs = methods(typeElement)
                    .map( method -> FieldSpec.builder(TypeName.get(((ExecutableElement)method).getReturnType()), method.getSimpleName().toString())
                            .addModifiers(Modifier.PROTECTED)
                            .build()
                    )
                    .collect(Collectors.toList());         

            List methodSpecs = methods(typeElement)
                    .map( method -> MethodSpec.methodBuilder(method.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addCode("return " + method.getSimpleName().toString() +";\n")
                            .returns(TypeName.get(((ExecutableElement)method).getReturnType()))
                            .build()
                    )
                    .collect(Collectors.toList());

            AnnotationSpec jsonTypeInfoAnnotation = AnnotationSpec.builder(JsonTypeInfo.class)
                    .addMember("use", "$1L", JsonTypeInfo.Id.NAME)
                    .addMember("include", "$1L", JsonTypeInfo.As.PROPERTY)
                    .addMember("property", "$1L", "type")
                    .build();
           
            TypeSpec typeSpec = TypeSpec.classBuilder( className )
                    .addAnnotation(jsonTypeInfoAnnotation)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(TypeName.get(typeElement.asType()))
                    .addFields(fieldSpecs)
                    .addMethod( MethodSpec.constructorBuilder()
                            .addParameters(methods(typeElement)
                                    .map(method -> ParameterSpec.builder( TypeName.get(((ExecutableElement)method).getReturnType()), method.getSimpleName().toString()).build())
                                    .collect(Collectors.toList()))
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("super(-1)")
                            .addCode(methods(typeElement)
                                    .map(method -> method.getSimpleName().toString() + " = " + method.getSimpleName().toString() + ";")
                                    .collect( Collectors.joining( "\n" ) ))
                            .build())
                    .addMethods(methodSpecs)
                    .build();

            try {
                JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return false;
    }

    private Stream<? extends Element> methods(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter( el -> el.getKind() == ElementKind.METHOD );
    }



    public abstract class AbstractDomainEvent{
        private int version;

        public AbstractDomainEvent(int version) {
            this.version = version;
        }

        public int getVersion() {
            return version;
        }
    }

}
