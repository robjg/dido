package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaField;
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

    public <T> Function<GenericData<String>, T> ofArooaClass(ArooaClass arooaClass) {
        return new Impl<>(arooaClass, accessor);
    }

    public <T> Function<GenericData<String>, T> ofClass(Class<T> theClass) {
        return ofArooaClass(new SimpleArooaClass(theClass));
    }

    public <T> Function<GenericData<String>, T> ofSchema(DataSchema<String> schema) {

        final MagicBeanClassCreator classCreator =
                new MagicBeanClassCreator("BeanOfSchema" +
                        instance.incrementAndGet());

        for (String field : schema.getFields()) {

            classCreator.addProperty(field, schema.getType(field));
        }

        return ofArooaClass(classCreator.create());
    }

    public <T> Function<GenericData<String>, T> ofUnknown() {
        return new Unknown<>();
    }

    class Unknown<T> implements Function<GenericData<String>, T> {

        private Function<GenericData<String>, T> delegate;

        private DataSchema<String> lastSchema;

        @Override
        public T apply(GenericData<String> data) {

            if (delegate == null || lastSchema != data.getSchema()) {
                lastSchema = data.getSchema();
                delegate = ofSchema(lastSchema);
            }

            return delegate.apply(data);
        }
    }

    class Impl<T> implements Function<GenericData<String>, T> {

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
        public T apply(GenericData<String> data) {

            @SuppressWarnings("unchecked")
            T t = (T) arooaClass.newInstance();

            DataSchema<String> schema = data.getSchema();

            String[] propertyNames = beanOverview.getProperties();

            Collection<String> fields = data.getSchema().getFields();

            for (String propertyName : propertyNames) {

                if (!beanOverview.hasWriteableProperty(propertyName)) {
                    continue;
                }

                if (!fields.contains(propertyName)) {
                    continue;
                }

                int index = schema.getIndex(propertyName);
                if (!data.hasIndex(index)) {
                    continue;
                }

                Class<?> type = beanOverview.getPropertyType(propertyName);

                SchemaField<String> schemaField = schema.getSchemaFieldAt(index);

                Object value;
                if (schemaField.isNested()) {
                    if (schemaField.isRepeating()) {
                        Class<?> componentType;
                        if (type.isArray()) {
                            componentType = type.getComponentType();
                            Function<GenericData<String>, ?> func = ofClass(componentType);

                            value = Arrays.stream((GenericData<String>[]) data.get(propertyName))
                                    .map(func)
                                    .toArray();
                        }
                        else if (Iterable.class.isAssignableFrom(type)) {
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
                            Function<GenericData<String>, ?> func = ofClass(componentType);

                            value = StreamSupport.stream(
                                    ((Iterable<GenericData<String>>) data.get(propertyName)).spliterator(), false)
                                    .map(func)
                                    .collect(Collectors.toList());
                        }
                        else {
                            componentType = null;
                            value = null;
                        }

                        if (componentType == null) {
                            throw new IllegalArgumentException("Unable to work out component type for " + propertyName);
                        }
                    }
                    else {
                        value = ofClass(type).apply((GenericData<String>) data.get(propertyName));
                    }
                }
                else {
                    value = data.getAs(propertyName, type);
                }

                accessor.setSimpleProperty(t, propertyName, value);
            }

            return t;
        }
    }

}
