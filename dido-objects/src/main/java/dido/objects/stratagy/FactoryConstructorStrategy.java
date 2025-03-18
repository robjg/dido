package dido.objects.stratagy;

import dido.how.DataException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FactoryConstructorStrategy<T> implements ConstructionStrategy<T> {

    private final Type type;

    /** The factory object */
    private final Object factory;

    private final Method method;

    private final Map<String, ValueSetter> setters;

    public FactoryConstructorStrategy(Type type,
                                      Object factory,
                                      Method method,
                                      Map<String, ValueSetter> setters) {
        this.type = type;
        this.factory = factory;
        this.method = method;
        this.setters = setters;
    }


    public static <T> ConstructionStrategy<T> from(Class<?> cl,
                                     String method) {

        List<Method> methods = Arrays.stream(cl.getMethods())
                .filter(m -> m.getName().equals(method))
                .collect(Collectors.toList());

        if (methods.size() != 1) {
            throw DataException.of("Unexpected number of methods " + methods.size() +
                    ": " + methods);
        }

        return from(cl, methods.get(0));
    }

    public static <T> ConstructionStrategy<T> from(Class<?> cl,
                                                   Method m) {

        Parameter[] parameters = m.getParameters();
        Map<String, ValueSetter> setters = new LinkedHashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            setters.put(name, new MethodSetter(name,
                    parameter.getParameterizedType(), i));
        }

        return new FactoryConstructorStrategy<>(cl, null, m, setters);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Supplier<ObjectConstructor<T>> getConstructorSupplier() {
        return MethodConstructor::new;
    }

    @Override
    public Collection<ValueSetter> getSetters() {
        return setters.values();
    }

    @Override
    public ValueSetter getSetter(String name) {
        return setters.get(name);
    }

    static class MethodSetter implements ValueSetter {

        private final String name;

        private final Type type;

        private final int index;

        MethodSetter(String name, Type type, int index) {
            this.name = name;
            this.type = type;
            this.index = index;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public void setValue(ObjectConstructor<?> target, Object value) {
            ((AllowNonStatic) target).setValue(index, value);
        }
    }

    private interface AllowNonStatic {

        void setValue(int index, Object value);
    }

    class MethodConstructor implements ObjectConstructor<T>, AllowNonStatic {

        private final Object[] values;

        MethodConstructor() {
            this.values = new Object[setters.size()];
        }

        @Override
        public T actualize() {
            try {
                //noinspection unchecked
                return (T) method.invoke(factory, values);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw DataException.of("Failed creating instance from " + method +
                        " with parameters " + Arrays.toString(values));
            }
        }

        @Override
        public void setValue(int index, Object value) {
            values[index] = value;
        }
    }
}
