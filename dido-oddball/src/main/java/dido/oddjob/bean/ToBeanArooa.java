package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaField;
import dido.data.util.TypeUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.beanutils.MagicBeanClassCreator;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ToBeanArooa {

    private static final AtomicInteger instance = new AtomicInteger();

    private final PropertyAccessor accessor;

    public ToBeanArooa(PropertyAccessor accessor) {
        this.accessor = accessor;
    }

    public static ToBeanArooa usingAccessor(PropertyAccessor accessor) {
        return new ToBeanArooa(accessor);
    }

    public static ToBeanArooa fromSession(ArooaSession session) {
        return usingAccessor(session.getTools().getPropertyAccessor());
    }

    public <T> Function<DidoData, T> ofArooaClass(ArooaClass arooaClass) {
        return new Impl<>(arooaClass, accessor);
    }

    public <T> Function<DidoData, T> ofClass(Class<T> theClass) {
        return ofArooaClass(new SimpleArooaClass(theClass));
    }

    public <T> Function<DidoData, T> ofSchema(DataSchema schema) {

        final MagicBeanClassCreator classCreator =
                new MagicBeanClassCreator("BeanOfSchema" +
                        instance.incrementAndGet());

        for (String field : schema.getFieldNames()) {

            classCreator.addProperty(field, TypeUtil.classOf(schema.getTypeNamed(field)));
        }

        return ofArooaClass(classCreator.create());
    }

    public <T> Function<DidoData, T> ofUnknown() {
        return new Unknown<>();
    }

    class Unknown<T> implements Function<DidoData, T> {

        private Function<DidoData, T> delegate;

        private DataSchema lastSchema;

        @Override
        public T apply(DidoData data) {

            if (delegate == null || lastSchema != data.getSchema()) {
                lastSchema = data.getSchema();
                delegate = ofSchema(lastSchema);
            }

            return delegate.apply(data);
        }
    }

    class Impl<T> implements Function<DidoData, T> {

        private final ArooaClass arooaClass;

        private final PropertyAccessor accessor;

        private final BeanOverview beanOverview;

        Impl(ArooaClass arooaClass, PropertyAccessor accessor) {
            this.arooaClass = arooaClass;
            this.accessor = accessor;
            this.beanOverview = arooaClass.getBeanOverview(accessor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T apply(DidoData data) {

            @SuppressWarnings("unchecked")
            T t = (T) arooaClass.newInstance();

            DataSchema schema = data.getSchema();

            String[] propertyNames = beanOverview.getProperties();

            Collection<String> fields = data.getSchema().getFieldNames();

            for (String propertyName : propertyNames) {

                if (!beanOverview.hasWriteableProperty(propertyName)) {
                    continue;
                }

                if (!fields.contains(propertyName)) {
                    continue;
                }

                int index = schema.getIndexNamed(propertyName);
                if (!data.hasAt(index)) {
                    continue;
                }

                Class<?> type = beanOverview.getPropertyType(propertyName);

                SchemaField schemaField = schema.getSchemaFieldAt(index);

                Object value;
                if (schemaField.isNested()) {
                    if (schemaField.isRepeating()) {
                        Class<?> componentType;
                        if (type.isArray()) {
                            componentType = type.getComponentType();
                            Function<DidoData, ?> func = ofClass(componentType);

                            value = Arrays.stream((DidoData[]) data.getNamed(propertyName))
                                    .map(func)
                                    .toArray();
                        } else if (Iterable.class.isAssignableFrom(type)) {
                            // Bodge until Oddjob 1.7 provides this.
                            try {
                                PropertyDescriptor propertyDescriptor =
                                        PropertyUtils.getPropertyDescriptor(t, propertyName);
                                componentType = org.oddjob.arooa.utils.ClassUtils.getComponentTypeOfParameter(
                                        propertyDescriptor.getWriteMethod(), 0);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                throw new IllegalArgumentException(
                                        "Failed to find iterable type for " + propertyName, e);
                            }
                            Function<DidoData, ?> func = ofClass(componentType);

                            value = StreamSupport.stream(
                                            ((Iterable<DidoData>) data.getNamed(propertyName)).spliterator(), false)
                                    .map(func)
                                    .collect(Collectors.toList());
                        } else {
                            componentType = null;
                            value = null;
                        }

                        if (componentType == null) {
                            throw new IllegalArgumentException("Unable to work out component type for " + propertyName);
                        }
                    } else {
                        value = ofClass(type).apply((DidoData) data.getNamed(propertyName));
                    }
                } else {
                    value = data.getNamed(propertyName);
                }

                accessor.setSimpleProperty(t, propertyName, value);
            }

            return t;
        }
    }

}
