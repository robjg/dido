package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.beanutils.MagicBeanClassCreator;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ToBeanArooa {

    private static final AtomicInteger instance = new AtomicInteger();

    private final PropertyAccessor accessor;

    public ToBeanArooa(PropertyAccessor accessor) {
        this.accessor = accessor;
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

    static class Impl<T> implements Function<GenericData<String>, T> {

        private final ArooaClass arooaClass;

        private final PropertyAccessor accessor;

        private final BeanOverview beanOverview;

        Impl(ArooaClass arooaClass, PropertyAccessor accessor) {
            this.arooaClass = arooaClass;
            this.accessor = accessor;
            this.beanOverview = arooaClass.getBeanOverview(accessor);
        }

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

                Class<?> type = schema.getTypeAt(index);
                Object value = data.getAs(propertyName, type);

                accessor.setSimpleProperty(t, propertyName, value);
            }

            return t;
        }
    }
}
