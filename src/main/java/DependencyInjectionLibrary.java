
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyInjectionLibrary {


    private Set<Class> requestedClasses = new HashSet<>();


    private Set<Class> instantiableClasses = new HashSet<>();


    private Map<Class, Object> singletonInstances = new HashMap<>();


    private Set<Class> singletonClasses = new HashSet<>();


    private Map<Class, Class> interfaceMappings = new HashMap<>();

    private Map<Class, Provider> providers = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> requestedType) {
        return getInstance(requestedType, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(Class<T> requestedType, Class<?> parent) {

            Class<T> type = requestedType;

            if (requestedType.isInterface()) {
                if (interfaceMappings.containsKey(requestedType)) {
                    type = interfaceMappings.get(requestedType);
                } else if (providers.containsKey(requestedType)) {
                    return getInstanceFromProvider(requestedType);
                }
            }

            if (!requestedType.isInterface() && Modifier.isAbstract(requestedType.getModifiers())) {
                if (providers.containsKey(requestedType)) {
                    return getInstanceFromProvider(requestedType);
                }
            }

            requestedClasses.add(type);

            if (singletonInstances.containsKey(type)) {
                return (T) singletonInstances.get(type);
            }

            if (providers.containsKey(type)) {
                final T instanceFromProvider = getInstanceFromProvider(type);
                markAsInstantiable(type);

                if (type.isAnnotationPresent(Singleton.class) || singletonClasses.contains(type)) {
                    singletonInstances.put(type, instanceFromProvider);
                }
                return instanceFromProvider;
            }

            return createNewInstance(type);
    }


    private <T> T createNewInstance(Class<T> type) {
        final Constructor<T> constructor = findConstructor(type);

        final Parameter[] parameters = constructor.getParameters();


        final List<Object> arguments = Arrays.stream(parameters).map(param -> {
                    if (param.getType().equals(Provider.class)) { return getProviderArgument(param); }
                    else { return getInstance(param.getType(), type); } }).collect(Collectors.toList());

        try {
            final T newInstance = constructor.newInstance(arguments.toArray());

            markAsInstantiable(type);

            if (type.isAnnotationPresent(Singleton.class) || singletonClasses.contains(type)) { singletonInstances.put(type, newInstance); }

            return newInstance;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public <T> void bindInterface(Class<T> interfaceType, Class<? extends T> implementationType) {
                interfaceMappings.put(interfaceType, implementationType);
    }

    public <T> void bindProvider(Class<T> classType, Provider<T> provider) {
        providers.put(classType, provider);
    }

    public <T> void bindInstance(Class<T> classType, T instance) {
        bindProvider(classType, () -> instance);
    }

    private Provider getProviderArgument(Parameter param) {
            ParameterizedType typeParam = (ParameterizedType) param.getParameterizedType();
            final Type providerType = typeParam.getActualTypeArguments()[0];
            return () -> DependencyInjectionLibrary.this.getInstance((Class) providerType);
    }


    private void markAsInstantiable(Class type) {
        if (!instantiableClasses.contains(type)) {
            instantiableClasses.add(type);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstanceFromProvider(Class<T> type) {
            final Provider<T> provider = providers.get(type);
            return provider.get();
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> type) {
        final Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length > 1) {

            final List<Constructor<?>> constructorsWithInject = Arrays
                    .stream(constructors)
                    .filter(c -> c.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList());

            return (Constructor<T>) constructorsWithInject.get(0);
        } else {
            return (Constructor<T>) constructors[0];
        }
    }

}