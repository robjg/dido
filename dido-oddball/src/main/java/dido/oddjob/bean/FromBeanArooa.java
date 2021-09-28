package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanArooa {

    private final PropertyAccessor accessor;

    public FromBeanArooa(PropertyAccessor accessor) {
        this.accessor = accessor;
    }

    public <T> Function<T, GenericData<String>> ofUnknown() {

        return new Unknown<T>();
    }

    public <T> Function<T, GenericData<String>> ofClass(Class<T> aClass) {

        DataSchema<String> schema = schemaFrom(new SimpleArooaClass(aClass));

        return bean -> new Impl(schema, bean);
    }

    public <T> Function<T, GenericData<String>> ofArooaClass(ArooaClass arooaClass) {

        DataSchema<String> schema = schemaFrom(arooaClass);

        return bean -> new Impl(schema, bean);
    }

    class Unknown<T> implements Function<T, GenericData<String>> {

        private DataSchema<String> schema;

        @Override
        public GenericData<String> apply(T bean) {

            if (schema == null) {
                schema = schemaFrom(accessor.getClassName(bean));
            }

            return new Impl(schema, bean);
        }
    }

    class Impl implements GenericData<String> {

        private final DataSchema<String> schema;

        private final Object bean;

        Impl(DataSchema<String> schema, Object bean) {
            this.schema = schema;
            this.bean = bean;
        }

        @Override
        public DataSchema<String> getSchema() {
            return schema;
        }

        @Override
        public <T> T getObjectAt(int index, Class<T> type) {
            //noinspection unchecked
            return (T) accessor.getProperty(bean, schema.getFieldAt(index));
        }

        @Override
        public boolean hasIndex(int index) {
            return getObjectAt(index, Object.class) != null;
        }

        @Override
        public <T> T getObject(String field, Class<T> type) {
            //noinspection unchecked
            return (T) accessor.getProperty(bean, field);
        }

        @Override
        public Object getObject(String field) {
            return accessor.getProperty(bean, field);
        }

        @Override
        public boolean hasField(String field) {
            return accessor.getProperty(bean, field) != null;
        }

        @Override
        public boolean getBoolean(String field) {
            return (boolean) accessor.getProperty(bean, field);
        }

        @Override
        public byte getByte(String field) {
            return (byte) accessor.getProperty(bean, field);
        }

        @Override
        public char getChar(String field) {
            return (char) accessor.getProperty(bean, field);
        }

        @Override
        public int getShort(String field) {
            return (short) accessor.getProperty(bean, field);
        }

        @Override
        public int getInt(String field) {
            return (int) accessor.getProperty(bean, field);
        }

        @Override
        public long getLong(String field) {
            return (long) accessor.getProperty(bean, field);
        }

        @Override
        public float getFloat(String field) {
            return (float) accessor.getProperty(bean, field);
        }

        @Override
        public double getDouble(String field) {
            return (double) accessor.getProperty(bean, field);
        }

        @Override
        public String getString(String field) {
            return (String) accessor.getProperty(bean, field);
        }
    }

    DataSchema<String> schemaFrom(ArooaClass arooaClass) {

        BeanOverview beanOverview = arooaClass.getBeanOverview(this.accessor);

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (String property : beanOverview.getProperties()) {

            if ("class".equals(property)) {
                continue;
            }

            if (!beanOverview.hasReadableProperty(property)) {
                continue;
            }
            if (beanOverview.isIndexed(property)) {
                continue;
            }
            if (beanOverview.isMapped(property)) {
                continue;
            }

            schemaBuilder.addField(property, beanOverview.getPropertyType(property));
        }

        return schemaBuilder.build();
    }
}
