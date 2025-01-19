package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Operations on a single filed that can be used in an {@link OpTransformBuilder} to create an
 * {@link DidoTransform}.
 */
public class FieldOps {

    /**
     * Create an operation that copies the field at an index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyAt(int index) {
        return copyAt(index, -1);
    }

    /**
     * Create an operation that copies the field from an index at the other index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyAt(int index, int at) {

        return (incomingSchema, schemaSetter) -> {

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter = readStrategy.getFieldGetterAt(index);

            SchemaField schemaField = incomingSchema.getSchemaFieldAt(index);

            if (schemaField == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }
            if (at >= 0) {
                schemaField = schemaField.mapToIndex(at);
            }

            SchemaField finalField = schemaSetter.addField(schemaField);

            return writableSchema ->
                    new Copy(getter, writableSchema.getFieldSetterNamed(finalField.getName()));
        };
    }

    /**
     * Create an operation that copies the named field. The field will be copied to the same index
     * in the resultant schema.
     *
     * @param name The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyNamed(String name) {

        return copyNamed(name, name);
    }

    /**
     * Create an operation that copies the named field to another name. The field will be copied to the same
     * index in the resultant schema.
     *
     * @param from The field name to copy.
     * @param to   The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyNamed(String from, String to) {

        return copyNamedAt(from, -1, to);
    }

    /**
     * Create an operation that copies the named field at the index given. If the index is
     * zero the field will be added to the end of the schema. If the field is negative, the existing
     * field will be used.
     *
     * @param from The field name to copy.
     * @param at   The index to copy to.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyNamedAt(String from, int at) {
        return copyNamedAt(from, at, from);
    }

    /**
     * Create an operation that copies the named field to another name at the index given. If the index is
     * zero the field will be added to the end of the schema. If the field is negative, the existing
     * field will be used.
     *
     * @param from The field name to copy.
     * @param at   The index to copy to.
     * @param to   The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyNamedAt(String from, int at, String to) {

        Objects.requireNonNull(from, "From");

        return (incomingSchema, schemaSetter) -> {

            SchemaField schemaField = incomingSchema.getSchemaFieldNamed(from);

            if (schemaField == null) {
                throw new NoSuchFieldException(from, incomingSchema);
            }

            if (to != null) {
                schemaField = schemaField.mapToFieldName(to);
            }
            if (at >= 0) {
                schemaField = schemaField.mapToIndex(at);
            }

            SchemaField finalField = schemaSetter.addField(schemaField);

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter = readStrategy.getFieldGetterNamed(from);

            return dataFactory -> new Copy(getter, dataFactory.getFieldSetterNamed(finalField.getName()));
        };
    }


    public static <T> OpDef computeNamed(String to,
                                         Function<? super DidoData, ? extends T> func,
                                         Class<T> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            }
            schemaSetter.addField(field);

            return dataFactory -> new Compute(dataFactory.getFieldSetterNamed(to), func);
        };
    }

    public static OpDef setNamed(String to,
                                 Object value) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {

                field = SchemaField.of(0, to, value == null ? Void.class : value.getClass());
            }

            schemaSetter.addField(field);

            return setterFactoryFor(to, value, field.getType());
        };
    }

    public static OpDef setNamed(String to,
                                 Object value,
                                 Class<?> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            } else {
                field = SchemaField.of(field.getIndex(), to, type);
            }

            schemaSetter.addField(field);

            return setterFactoryFor(to, value, type);
        };
    }

    public static OpDef setIntNamed(String to,
                                    int value) {

        return setNamed(to, value, int.class);
    }

    /**
     * @param to    The field name to.
     * @param value The value to set.
     * @param type  The type.
     * @return The prepare step.
     */
    static OpDef.Prepare setterFactoryFor(String to, Object value, Class<?> type) {

        return writableSchema -> {
            FieldSetter setter = writableSchema.getFieldSetterNamed(to);
            if (value == null) {
                return (data, out) -> setter.clear(out);
            } else if (boolean.class.isAssignableFrom(type)) {
                boolean boolValue = (boolean) value;
                return (data, out) -> setter.setBoolean(out, boolValue);
            } else if (byte.class.isAssignableFrom(type)) {
                byte byteValue = (byte) value;
                return (data, out) -> setter.setByte(out, byteValue);
            } else if (short.class.isAssignableFrom(type)) {
                short shortValue = (short) value;
                return (data, out) -> setter.setShort(out, shortValue);
            } else if (char.class.isAssignableFrom(type)) {
                char charValue = (char) value;
                return (data, out) -> setter.setChar(out, charValue);
            } else if (int.class.isAssignableFrom(type)) {
                int intValue = (int) value;
                return (data, out) -> setter.setInt(out, intValue);
            } else if (long.class.isAssignableFrom(type)) {
                long longValue = (long) value;
                return (data, out) -> setter.setLong(out, longValue);
            } else if (float.class.isAssignableFrom(type)) {
                float floatValue = (float) value;
                return (data, out) -> setter.setFloat(out, floatValue);
            } else if (double.class.isAssignableFrom(type)) {
                double doubleValue = (double) value;
                return (data, out) -> setter.setDouble(out, doubleValue);
            } else {
                return (data, out) -> setter.set(out, value);
            }
        };
    }


    public static OpDef removeNamed(String name) {

        return (incomingSchema, schemaSetter) -> {
            SchemaField field = incomingSchema.getSchemaFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, incomingSchema);
            }
            schemaSetter.removeField(field);
            return dataFactory -> (dataIn, dataOut) -> {
                // Nothing to do - the new data is assumed not to have the field.
            };
        };
    }

    static class Copy implements BiConsumer<DidoData, WritableData> {

        private final FieldGetter getter;

        private final FieldSetter setter;

        Copy(FieldGetter getter, FieldSetter setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public void accept(DidoData data, WritableData out) {
            if (getter.has(data)) {
                setter.set(out, getter.get(data));
            } else {
                setter.clear(out);
            }
        }
    }

    static class Compute implements BiConsumer<DidoData, WritableData> {

        private final FieldSetter setter;

        private final Function<? super DidoData, ?> func;

        Compute(FieldSetter setter, Function<? super DidoData, ?> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data, WritableData out) {
            Object now = func.apply(data);
            if (now == null) {
                setter.clear(out);
            } else {
                setter.set(out, now);
            }
        }
    }
}
