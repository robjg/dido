package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldOperations {

    /**
     * Create an operation that copies the field at an index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static TransformerDefinition copyAt(int index) {

        return (incomingSchema, schemaSetter) -> {

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter = readStrategy.getFieldGetterAt(index);

            SchemaField originalField = incomingSchema.getSchemaFieldAt(index);

            if (originalField == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }

            schemaSetter.addField(originalField);

                return writableSchema ->
                    new Copy(getter, writableSchema.getFieldSetterNamed(originalField.getName()));
        };
    }

    /**
     * Create an operation that copies the field at an index.
     *
     * @param name The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static TransformerDefinition copyNamed(String name) {

        return (incomingSchema, schemaSetter) -> {

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter = readStrategy.getFieldGetterNamed(name);

            SchemaField originalField = incomingSchema.getSchemaFieldNamed(name);

            if (originalField == null) {
                throw new NoSuchFieldException(name, incomingSchema);
            }

            schemaSetter.addField(originalField);

            return writableSchema ->
                    new Copy(getter, writableSchema.getFieldSetterNamed(originalField.getName()));
        };
    }

    public static TransformerDefinition copyNamed(String from, String to) {

        Objects.requireNonNull(from, "From");
        String finalTo = Objects.requireNonNullElse(to, from);

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(from)
                    .mapTo(0, finalTo);

            schemaSetter.addField(field);

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter = readStrategy.getFieldGetterNamed(from);

            return dataFactory -> new Copy(getter, dataFactory.getFieldSetterNamed(finalTo));
        };
    }

    public static <T> TransformerDefinition computeNamed(String to,
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

    public static TransformerDefinition setNamed(String to,
                                                 Object value) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                throw new NoSuchFieldException(to, incomingSchema);
            }

            schemaSetter.addField(field);

            return setterFactoryFor(to, value, field.getType());
        };
    }

    public static TransformerDefinition setNamed(String to,
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

    static TransformerFactory setterFactoryFor(String to, Object value, Class<?> type) {

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


    public static TransformerDefinition removeNamed(String name) {

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
