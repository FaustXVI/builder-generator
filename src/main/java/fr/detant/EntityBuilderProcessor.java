package fr.detant;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("javax.persistence.Entity")
public class EntityBuilderProcessor extends AbstractProcessor implements Processor {

    private VelocityEngine velocityEngine = createVelocityEngine();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Entity.class).stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(e -> (TypeElement) e)
                .forEach(this::createBuilder);
        return false;
    }

    private void createBuilder(TypeElement clazz) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(clazz.getQualifiedName() + "Builder");
            try(Writer writer = sourceFile.openWriter()) {
                Template template = velocityEngine.getTemplate("BuilderTemplate.vm");
                VelocityContext context = new VelocityContext();
                context.put("className", clazz.getSimpleName());
                template.merge(context, writer);
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private VelocityEngine createVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
        return velocityEngine;
    }
}
