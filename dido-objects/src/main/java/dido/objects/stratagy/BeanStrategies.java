package dido.objects.stratagy;

import dido.how.DataException;
import dido.objects.izers.ConstructionStrategyDeserializer;
import dido.objects.izers.DestructionStrategySerializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provide {@link ConstructionStrategy} and {@link DestructionStrategy}s for Java Beans.
 *
 * @param <T> The Type of Bean.
 * @see ConstructionStrategyDeserializer
 * @see DestructionStrategySerializer
 */
public class BeanStrategies<T> implements DestructionStrategy, ConstructionStrategy<T> {

    private final Class<T> cl;

    private final Map<String, BeanSetterGetter> setterGetterMap;

    private BeanStrategies(Class<T> cl, Map<String, BeanSetterGetter> setterGetterMap) {
        this.cl = cl;
        this.setterGetterMap = setterGetterMap;
    }

    public static class Settings {

        private boolean includeClass;

        private String[] fields;

        public Settings includeClass(boolean includeClass) {
            this.includeClass = includeClass;
            return this;
        }

        public Settings fields(String... fields) {
            this.fields = fields;
            return this;
        }

        public <T> BeanStrategies<T> from(Class<T> fromClass) {

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(fromClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                Map<String, BeanSetterGetter> map = new LinkedHashMap<>();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if ("class".equals(propertyDescriptor.getName()) && !includeClass) {
                        continue;
                    }
                    BeanSetterGetter beanSetterGetter = new BeanSetterGetter(propertyDescriptor);
                    map.put(beanSetterGetter.getName(), beanSetterGetter);
                }
                if (fields != null) {
                    Map<String, BeanSetterGetter> selected = new LinkedHashMap<>();
                    for (String field : fields) {
                        BeanSetterGetter setterGetter = map.get(field);
                        if (setterGetter == null) {
                            throw DataException.of("No property " + field + " in " + fromClass);
                        }
                        selected.put(field, setterGetter);
                    }
                    map = selected;
                }
                return new BeanStrategies<>(fromClass, map);
            } catch (IntrospectionException e) {
                throw DataException.of("Failed creating getters from " + fromClass, e);
            }
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static <T> BeanStrategies<T> from(Class<T> fromClass) {

        return with().from(fromClass);
    }

    @Override
    public Type getType() {
        return cl;
    }

    @Override
    public Collection<ValueGetter> getGetters() {

        return new ArrayList<>(setterGetterMap.values());
    }

    @Override
    public ValueGetter getGetter(String name) {
        return setterGetterMap.get(name);
    }

    @Override
    public Supplier<ObjectConstructor<T>> getConstructorSupplier() {
        return () -> {
            try {
                return new Constructor<>(cl.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw DataException.of("Failed creating instance from " + cl, e);
            }
        };
    }

    @Override
    public Collection<ValueSetter> getSetters() {

        return new ArrayList<>(setterGetterMap.values());
    }

    @Override
    public ValueSetter getSetter(String name) {
        return setterGetterMap.get(name);
    }

    static class Constructor<T> implements ObjectConstructor<T> {

        private final T instance;

        Constructor(T instance) {
            this.instance = instance;
        }

        @Override
        public T actualize() {
            return instance;
        }
    }


    static class BeanSetterGetter implements ValueGetter, ValueSetter {

        private final PropertyDescriptor propertyDescriptor;

        BeanSetterGetter(PropertyDescriptor propertyDescriptor) {
            this.propertyDescriptor = propertyDescriptor;
        }

        @Override
        public String getName() {
            return propertyDescriptor.getName();
        }

        @Override
        public Type getType() {
            return propertyDescriptor.getPropertyType();
        }

        @Override
        public Object getValue(Object target) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                throw new DataException("Failed extracting " + getName() +
                        " from " + target + " of " + target.getClass() +
                        " as property is not readable.");

            }
            try {
                return readMethod.invoke(target);
            } catch (Exception e) {
                throw new DataException("Failed extracting " + getName() +
                        " from " + target + " of " + target.getClass(), e);
            }
        }

        @Override
        public void setValue(ObjectConstructor<?> target, Object value) {
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                throw new DataException("Failed setting " + getName() +
                        " with " + value + " of " + target.getClass() +
                        " as property is not writable.");
            }
            try {
                writeMethod.invoke(((Constructor<?>) target).instance, value);
            } catch (Exception e) {
                throw new DataException("Failed setting " + getName() +
                        " with " + value + " of " + target.getClass(), e);
            }
        }
    }

}
