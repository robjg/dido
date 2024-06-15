package dido.oddjob.bean;

import dido.data.*;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Converts a Java Bean to {@link GenericData} using an Arooa {@link PropertyAccessor}.
 *
 * Handles nested schemas but they must be complete. Partial only applies to the root bean.
 * TODO: Fix this.
 */
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

    public With with() {
        return new With();
    }

    public class With {

        private GenericDataSchema<String> schema;

        private boolean partial;

        public With schema(GenericDataSchema<String> schema) {
            this.schema = schema;
            return this;
        }

        public With partial(boolean partialSchema) {
            this.partial = partialSchema;
            return this;
        }

        public <T> Function<T, DidoData> ofUnknownClass() {

            GenericDataSchema<String> schema = this.schema;
            boolean partial = this.partial;
            if (schema == null) {
                return new Unknown<>(GenericDataSchema.emptySchema());
            }
            else if (partial) {
                return new Unknown<>(schema);
            }
            else {
                return new Known<>(schema);
            }
        }

        public <T> Function<T, DidoData> ofClass(Class<T> aClass) {

            return ofArooaClass(new SimpleArooaClass(aClass));
        }

        public <T> Function<T, DidoData> ofArooaClass(ArooaClass arooaClass) {

            return FromBeanArooa.this.ofArooaClassWithSchema(arooaClass, schema, partial);
        }
    }

    public <T> Function<T, DidoData> ofUnknownClass() {

        return new Unknown<>(GenericDataSchema.emptySchema());
    }

    public <T> Function<T, DidoData> ofClass(Class<T> aClass) {

        GenericDataSchema<String> schema = schemaFrom(new SimpleArooaClass(aClass));

        return bean -> new Impl(schema, bean);
    }

    public <T> Function<T, DidoData> ofArooaClass(ArooaClass arooaClass) {

        GenericDataSchema<String> schema = schemaFrom(arooaClass);

        return bean -> new Impl(schema, bean);
    }

    protected <T> Function<T, DidoData> ofArooaClassWithSchema(ArooaClass arooaClass,
                                                                          GenericDataSchema<String> schema,
                                                                          boolean partial) {

        GenericDataSchema<String> outSchema = schemaFrom(arooaClass,
                schema, partial);

        return new Known<>(outSchema);
    }

    class Unknown<T> implements Function<T, DidoData> {

        private final GenericDataSchema<String> schema;

        private GenericDataSchema<String> outSchema;

        Unknown(GenericDataSchema<String> schema) {
            this.schema = schema;
        }

        @Override
        public DidoData apply(T bean) {

            if (outSchema == null) {
                outSchema = schemaFrom(accessor.getClassName(bean), schema, true);
            }

            return new Impl(outSchema, bean);
        }

        @Override
        public String toString() {
            return "FromBean, Partial Schema=" + schema;
        }
    }

    class Known<T> implements Function<T, DidoData> {

        private final GenericDataSchema<String> schema;

        Known(GenericDataSchema<String> schema) {
            this.schema = schema;
        }

        @Override
        public DidoData apply(T t) {
            if (t == null) {
                return null;
            }
            else {
                return new Impl(schema, t);
            }
        }

        @Override
        public String toString() {
            return "FromBean, Schema=" + schema;
        }
    }

    class Impl extends AbstractData {

        private final DataSchema schema;

        private final Object bean;

        Impl(DataSchema schema, Object bean) {
            this.schema = schema;
            this.bean = bean;
        }

        @Override
        public DataSchema getSchema() {
            return schema;
        }

        protected Object getFrom(SchemaField schemaField) {

            Object value = accessor.getProperty(bean, schemaField.getName());

            if (schemaField.isNested()) {
                DataSchema nestedSchema = schemaField.getNestedSchema();
                if (schemaField.isRepeating()) {
                    if (value == null) {
                        return RepeatingData.of();
                    }

                    List<DidoData> data = new ArrayList<>();
                    if (value instanceof Iterable) {
                        for (Object element : ((Iterable<?>) value)) {
                            data.add(new Impl(nestedSchema, element));
                        }
                    } else if (value instanceof Object[]) {
                        for (Object element : ((Object[]) value)) {
                            data.add(new Impl(nestedSchema, element));
                        }
                    } else {
                        throw new IllegalArgumentException("Can't extract Repeating Data from " + value);
                    }
                    return RepeatingData.of(data);
                } else {
                    if (value == null) {
                        return null;
                    } else {
                        return new Impl(nestedSchema, value);
                    }
                }
            } else {
                return value;
            }
        }

        @Override
        public Object getAt(int index) {
            SchemaField schemaField = schema.getSchemaFieldAt(index);
            if (schemaField == null) {
                throw new NullPointerException("No Property for index [" + index + "]");
            }
            return getFrom(schemaField);
        }

        @Override
        public boolean hasIndex(int index) {
            return getAtAs(index, Object.class) != null;
        }

        @Override
        public Object get(String field) {

            SchemaField schemaField = schema.getSchemaFieldNamed(field);
            if (schemaField == null) {
                throw new NullPointerException("No Property for for [" + field + "]");
            }

            return getFrom(schemaField);
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
        public String toString() {
            return DidoData.toStringFieldsOnly(this);
        }
    }


    protected GenericDataSchema<String> schemaFrom(ArooaClass arooaClass) {

        return schemaFrom(arooaClass, GenericDataSchema.emptySchema(), true);
    }

    protected GenericDataSchema<String> schemaFrom(ArooaClass arooaClass,
                                                   GenericDataSchema<String> schema,
                                                   boolean partial) {

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

            GenericSchemaField<String> schemaField = schema.getSchemaField(property);

            if (schemaField == null) {
                if (!partial) {
                    continue;
                }
                schemaField = GenericSchemaField.of(schemaBuilder.getLastIndex() + 1, property,
                        beanOverview.getPropertyType(property));
            }
            else {
                schemaField.mapToIndex(schemaBuilder.getLastIndex() + 1);
            }

            schemaBuilder.addGenericSchemaField(schemaField);
        }

        return schemaBuilder.build();
    }
}
