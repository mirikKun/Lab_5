import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;



import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;


 class InstanceProviderTest {

    public interface InstanceProvider {
        <T> T getInstance(Class<T> type);
    }

    public static class Example {
    }

    @Singleton
    public static class SingletonExample {
    }

    private DependencyInjectionLibrary dependencyInjectionLibrary;

    @BeforeEach
    void setup() {
        dependencyInjectionLibrary = new DependencyInjectionLibrary();
    }

    @Test
    void success_instanceProvider() {
        dependencyInjectionLibrary.bindProvider(InstanceProvider.class, () -> dependencyInjectionLibrary::getInstance);

        final InstanceProvider provider = dependencyInjectionLibrary.getInstance(InstanceProvider.class);

        final Example example = provider.getInstance(Example.class);

        assertThat(example).isNotNull();
    }

    @Test
    void success_singleton() {
        dependencyInjectionLibrary.bindProvider(InstanceProvider.class, () -> dependencyInjectionLibrary::getInstance);


        final InstanceProvider provider = dependencyInjectionLibrary.getInstance(InstanceProvider.class);

        final SingletonExample singleton1 = provider.getInstance(SingletonExample.class);
        final SingletonExample singleton2 = provider.getInstance(SingletonExample.class);
        final SingletonExample singleton3 = dependencyInjectionLibrary.getInstance(SingletonExample.class);

        assertThat(singleton1).isSameAs(singleton2);
        assertThat(singleton2).isSameAs(singleton3);
    }

}