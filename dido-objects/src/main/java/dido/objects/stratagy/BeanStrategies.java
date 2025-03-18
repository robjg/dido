package dido.objects.stratagy;

import dido.data.DataSchema;
import dido.how.DataException;
import dido.objects.izers.ConstructionStrategyDeserializer;
import dido.objects.izers.DestructionStrategySerializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/**
 * Provide {@link ConstructionStrategy} and {@link DestructionStrategy}s for Java Beans.
 *
 * @param <T> The Type of Bean.
 *
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

        public Settings includeClass(boolean includeClass) {
            this.includeClass = includeClass;
            return this;
        }

        public <T> BeanStrategies<T> from(Class<T> fromClass) {

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(fromClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                Map<String, BeanSetterGetter> map = new HashMap<>();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if ("class".equals(propertyDescriptor.getName()) && !includeClass) {
                        continue;
                    }
                    BeanSetterGetter beanSetterGetter = new BeanSetterGetter(propertyDescriptor);
                    map.put(beanSetterGetter.getName(), beanSetterGetter);
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
        return ()-> {
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
            try {
                return propertyDescriptor.getReadMethod().invoke(target);
            } catch (Exception e) {
                throw DataException.of("Failed extracting " + getName() +
                        " from " + target + " of " + target.getClass(), e);
            }
        }

        @Override
        public void setValue(ObjectConstructor<?> target, Object value) {
            try {
                propertyDescriptor.getWriteMethod()
                        .invoke(((Constructor<?>) target).instance, value);
            } catch (Exception e) {
                throw DataException.of("Failed setting " + getName() +
                        " from " + value + " of " + target.getClass(), e);
            }
        }
    }

}
