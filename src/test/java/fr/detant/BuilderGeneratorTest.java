package fr.detant;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;
import org.truth0.Truth;

public class BuilderGeneratorTest {

    @Test
    public void shouldProcessWithoutError() {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource("Entity.java"))
                .processedWith(new EntityBuilderProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("EntityBuilder.java"))
        ;
    }
}
