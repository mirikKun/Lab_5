import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BindInstanceTest {


    private static class Example implements MyInterface {
    }

    interface MyInterface {
    }

    static abstract class AbstractExample {
    }

    private DependencyInjectionLibrary dependencyInjectionLibrary;

    @BeforeEach
    void setup(){
        dependencyInjectionLibrary = new DependencyInjectionLibrary();
    }


    @Test
    void success_bindInstance() {

        Example example = new Example();

        dependencyInjectionLibrary.bindInstance(Example.class, example);

        final Example instance = dependencyInjectionLibrary.getInstance(Example.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    void success_interface() {
        Example example = new Example();

        dependencyInjectionLibrary.bindInstance(MyInterface.class, example);

        final MyInterface instance = dependencyInjectionLibrary.getInstance(MyInterface.class);

        assertThat(instance).isSameAs(example);
    }

    @Test
    void success_abstractClass() {

        AbstractExample example = new AbstractExample() {
        };

        dependencyInjectionLibrary.bindInstance(AbstractExample.class, example);

        final AbstractExample instance = dependencyInjectionLibrary.getInstance(AbstractExample.class);

        assertThat(instance).isSameAs(example);
    }
}