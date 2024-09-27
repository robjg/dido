package dido.oddjob.bean;

import dido.data.NoSuchFieldException;
import dido.data.*;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Converts a Java Bean to {@link DidoData} using an Arooa {@link PropertyAccessor}.
 * <p>
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

        private DataSchema schema;

        private boolean partial;

        public With schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public With partial(boolean partialSchema) {
            this.partial = partialSchema;
            return this;
        }

        public <T> Function<T, NamedData> ofUnknownClass() {

            DataSchema schema = this.schema;
            boolean partial = this.partial;
            if (schema == null) {
                return new Unknown<>(DataSchema.emptySchema());
            } else if (partial) {
                return new Unknown<>(schema);
            } else {
                return new Known<>(new Schema(schema));
            }
        }

        public <T> Function<T, NamedData> ofClass(Class<T> aClass) {

            return ofArooaClass(new SimpleArooaClass(aClass));
        }

        public <T> Function<T, NamedData> ofArooaClass(ArooaClass arooaClass) {

            return FromBeanArooa.this.ofArooaClassWithSchema(arooaClass, schema, partial);
        }
    }

    public <T> Function<T, NamedData> ofUnknownClass() {

        return new Unknown<>(DataSchema.emptySchema());
    }

    public <T> Function<T, NamedData> ofClass(Class<T> aClass) {

        Schema schema = schemaFrom(new SimpleArooaClass(aClass));

        return bean -> new Impl(schema, bean);
    }

    public <T> Function<T, DidoData> ofArooaClass(ArooaClass arooaClass) {

        Schema schema = schemaFrom(arooaClass);

        return bean -> new Impl(schema, bean);
    }

    protected <T> Function<T, NamedData> ofArooaClassWithSchema(ArooaClass arooaClass,
                                                                DataSchema schema,
                                                                boolean partial) {

        Schema outSchema = schemaFrom(arooaClass,
                schema, partial);

        return new Known<>(outSchema);
    }

    class Unknown<T> implements Function<T, NamedData> {

        private final DataSchema schema;

        private Schema outSchema;

        Unknown(DataSchema schema) {
            this.schema = schema;
        }

        @Override
        public NamedData apply(T bean) {

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

    class Known<T> implements Function<T, NamedData> {

        private final Schema schema;

        Known(Schema schema) {
            this.schema = schema;
        }

        @Override
        public NamedData apply(T t) {
            if (t == null) {
                return null;
            } else {
                return new Impl(schema, t);
            }
        }

        @Override
        public String toString() {
            return "FromBean, Schema=" + schema;
        }
    }

    class Impl extends AbstractNamedData {

        private final Schema schema;

        private final Object bean;

        Impl(Schema schema, Object bean) {
            this.schema = schema;
            this.bean = bean;
        }

        @Override
        public ReadableSchema getSchema() {
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
                            data.add(new Impl(new Schema(nestedSchema), element));
                        }
                    } else if (value instanceof Object[]) {
                        for (Object element : ((Object[]) value)) {
                            data.add(new Impl(new Schema(nestedSchema), element));
                        }
                    } else {
                        throw new IllegalArgumentException("Can't extract Repeating Data from " + value);
                    }
                    return RepeatingData.of(data);
                } else {
                    if (value == null) {
                        return null;
                    } else {
                        return new Impl(new Schema(nestedSchema), value);
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
                throw new NoSuchFieldException(index, schema);
            }
            return getFrom(schemaField);
        }

        @Override
        public boolean hasIndex(int index) {
            return getAt(index) != null;
        }

        @Override
        public Object getNamed(String name) {

            SchemaField schemaField = schema.getSchemaFieldNamed(name);
            if (schemaField == null) {
                throw new NoSuchFieldException(name, schema);
            }

            return getFrom(schemaField);
        }

        @Override
        public boolean hasNamed(String name) {
            return accessor.getProperty(bean, name) != null;
        }

        @Override
        public boolean getBooleanNamed(String name) {
            return (boolean) accessor.getProperty(bean, name);
        }

        @Override
        public byte getByteNamed(String name) {
            return (byte) accessor.getProperty(bean, name);
        }

        @Override
        public char getCharNamed(String name) {
            return (char) accessor.getProperty(bean, name);
        }

        @Override
        public short getShortNamed(String name) {
            return (short) accessor.getProperty(bean, name);
        }

        @Override
        public int getIntNamed(String name) {
            return (int) accessor.getProperty(bean, name);
        }

        @Override
        public long getLongNamed(String name) {
            return (long) accessor.getProperty(bean, name);
        }

        @Override
        public float getFloatNamed(String name) {
            return (float) accessor.getProperty(bean, name);
        }

        @Override
        public double getDoubleNamed(String name) {
            return (double) accessor.getProperty(bean, name);
        }

        @Override
        public String getStringNamed(String name) {
            return (String) accessor.getProperty(bean, name);
        }

        @Override
        public String toString() {
            return DidoData.toStringFieldsOnly(this);
        }
    }


    protected Schema schemaFrom(ArooaClass arooaClass) {

        return schemaFrom(arooaClass, DataSchema.emptySchema(), true);
    }

    protected Schema schemaFrom(ArooaClass arooaClass,
                                DataSchema schema,
                                boolean partial) {

        BeanOverview beanOverview = arooaClass.getBeanOverview(this.accessor);

        SchemaFactory schemaFactory = new SchemaFactory();

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

            SchemaField schemaField = schema.getSchemaFieldNamed(property);

            if (schemaField == null) {
                if (!partial) {
                    continue;
                }
                schemaField = SchemaField.of(schemaFactory.lastIndex() + 1, property,
                        beanOverview.getPropertyType(property));
            } else {
                schemaField.mapToIndex(schemaFactory.lastIndex() + 1);
            }

            schemaFactory.addSchemaField(schemaField);
        }

        return schemaFactory.toSchema();
    }

    protected static class Schema extends DataSchemaImpl implements ReadableSchema {

        Schema(Collection<SchemaField> fields,
               int firstIndex,
               int lastIndex) {

            super(fields, firstIndex, lastIndex);
        }

        Schema(DataSchema schema) {

            super(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex());
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            if (hasIndex(index)) {
                return FieldGetter.getterAt(index);

            } else {
                throw new NoSuchFieldException(index, Schema.this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            if (hasNamed(name)) {
                return getFieldGetterNamed(name);
            } else {
                throw new NoSuchFieldException(name, Schema.this);
            }
        }
    }

    static class SchemaFactory extends SchemaFactoryImpl<Schema> {

        @Override
        protected Schema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new Schema(fields, firstIndex, lastIndex);
        }
    }

}
