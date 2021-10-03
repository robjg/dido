package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.ArooaSession;
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

    public static FromBeanArooa usingAccessor(PropertyAccessor accessor) {
        return new FromBeanArooa(accessor);
    }

    public static FromBeanArooa fromSession(ArooaSession session) {
        return usingAccessor(session.getTools().getPropertyAccessor());
    }

    public <T> Function<T, GenericData<String>> ofUnknown() {

        return new Unknown<>();
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
        public Object getAt(int index) {
            String property = schema.getFieldAt(index);
            if (property == null) {
                throw new NullPointerException("No Property for index [" + index + "]");
            }
            return accessor.getProperty(bean, property);
        }

        @Override
        public boolean hasIndex(int index) {
            return getAtAs(index, Object.class) != null;
        }

        @Override
        public Object get(String field) {
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
        public short getShort(String field) {
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

        @Override
        public boolean equals(Object o) {
            if (o instanceof IndexedData) {
                return IndexedData.equals(this, (IndexedData<?>) o);
            }
            else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return IndexedData.hashCode(this);
        }

        @Override
        public String toString() {
            return GenericData.toStringFieldsOnly(this);
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
