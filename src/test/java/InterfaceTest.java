import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;


class InterfaceTest {

    interface A {
    }

    interface B {
    }

    public static class ExampleOne implements A {
    }

    public static class ExampleTwo implements A, B {
    }


    private DependencyInjectionLibrary dependencyInjectionLibrary;

    @BeforeEach
    void setup() {
        dependencyInjectionLibrary = new DependencyInjectionLibrary();
    }


    @Test
    void success_withBinding() {
        dependencyInjectionLibrary.bindInterface(A.class, ExampleOne.class);

        final A instance = dependencyInjectionLibrary.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleOne.class);
    }




    @Test
    void success_lastBindingIsUsed() {
        dependencyInjectionLibrary.bindInterface(A.class, ExampleOne.class);
        dependencyInjectionLibrary.bindInterface(A.class, ExampleTwo.class);

        final A instance = dependencyInjectionLibrary.getInstance(A.class);
        assertThat(instance).isNotNull().isInstanceOf(ExampleTwo.class);
    }



    @Singleton interface WannabeSingleton {
    }

    public static class NonSingleton implements WannabeSingleton {
    }

    @Test
    void fail_interfacesCantBeMarkedAsSingleton() {

        dependencyInjectionLibrary.bindInterface(WannabeSingleton.class, NonSingleton.class);

        final WannabeSingleton instanceOne = dependencyInjectionLibrary.getInstance(WannabeSingleton.class);
        final WannabeSingleton instanceTwo = dependencyInjectionLibrary.getInstance(WannabeSingleton.class);

        assertThat(instanceOne).isNotSameAs(instanceTwo);
    }

}
