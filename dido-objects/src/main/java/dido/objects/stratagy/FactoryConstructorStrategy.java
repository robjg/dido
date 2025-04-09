package dido.objects.stratagy;

import dido.how.DataException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/**
 * Creates a {@link ConstructionStrategy}s from a factory methods.
 *
 * @param <T>
 */
public class FactoryConstructorStrategy<T> implements ConstructionStrategy<T> {

    private final Type type;

    /**
     * The factory object
     */
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

    /**
     * Create a {@link ConstructionStrategy} from a named static method of a class.
     * The field names may be provided or the parameter names of the method are used as
     * the field name. To use the source parameter names the code must be
     * compiled with -parameters.
     *
     * @param cl     The class.
     * @param methodName The method name.
     * @param fieldNames The name of the fields to be used instead of the parameter names.
     *
     * @param <T>    The type of the Object being created.
     *
     * @return A Construction Strategy.
     */
    public static <T> ConstructionStrategy<T> from(Class<?> cl,
                                                   String methodName,
                                                   String... fieldNames) {


        List<Method> methods = Arrays.stream(cl.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .toList();

        Method method;
        if (methods.size() == 1) {
            method = methods.getFirst();
        }
        else {
            method = methods.stream()
                    .filter(m -> m.getParameters().length == fieldNames.length)
                    .findFirst()
                    .orElseThrow(() -> new DataException("Unexpected number of methods "
                            + methods.size() + ": " + methods));
        }

        return from(cl, method, fieldNames);
    }

    /**
     * Create a {@link ConstructionStrategy} from a static method of a class.
     * The field names may be provided or the parameter names of the method are used as
     * the field name. To use the source parameter names the code must be
     * compiled with -parameters.
     *
     * @param cl     The class.
     * @param method The method.
     * @param fieldNames The name of the fields to be used instead of the parameter names.
     *
     * @param <T>    The type of the Object being created.
     *
     * @return A Construction Strategy.
     */
    public static <T> ConstructionStrategy<T> from(Class<?> cl,
                                                   Method method,
                                                   String... fieldNames) {

        Parameter[] parameters = method.getParameters();
        Map<String, ValueSetter> setters = new LinkedHashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name;
            if (i < fieldNames.length) {
                name = fieldNames[i];
            } else {
                name = parameter.getName();
            }
            setters.put(name, new MethodSetter(name,
                    parameter.getParameterizedType(), i));
        }

        return new FactoryConstructorStrategy<>(cl, null, method, setters);
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
                throw new DataException("Failed creating instance from " + method +
                        " with parameters " + Arrays.toString(values));
            }
        }

        @Override
        public void setValue(int index, Object value) {
            values[index] = value;
        }
    }
}
