package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Objects;
import java.util.function.Consumer;
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

            Getter getter = incomingSchema.getDataGetterAt(index);

            SchemaField originalField = incomingSchema.getSchemaFieldAt(index);

            if (originalField == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }

            schemaSetter.addField(originalField);

            return dataFactory ->
                    new Copy(getter, dataFactory.getSetterNamed(originalField.getName()));
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

            Getter getter = incomingSchema.getDataGetterNamed(name);

            SchemaField originalField = incomingSchema.getSchemaFieldNamed(name);

            if (originalField == null) {
                throw new NoSuchFieldException(name, incomingSchema);
            }

            schemaSetter.addField(originalField);

            return dataFactory ->
                    new Copy(getter, dataFactory.getSetterNamed(originalField.getName()));
        };
    }

    public static TransformerDefinition copyNamed(String from, String to) {

        Objects.requireNonNull(from, "From");
        String finalTo = Objects.requireNonNullElse(to, from);

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(from)
                    .mapTo(0, finalTo);

            schemaSetter.addField(field);

            Getter getter = incomingSchema.getDataGetterNamed(from);

            return dataFactory -> new Copy(getter, dataFactory.getSetterNamed(finalTo));
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

            return dataFactory -> new Compute(dataFactory.getSetterNamed(to), func);
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

        return dataFactory -> {
            Setter setter = dataFactory.getSetterNamed(to);
            if (value == null) {
                return data -> setter.clear();
            } else if (boolean.class.isAssignableFrom(type)) {
                boolean boolValue = (boolean) value;
                return data -> setter.setBoolean(boolValue);
            } else if (byte.class.isAssignableFrom(type)) {
                byte byteValue = (byte) value;
                return data -> setter.setByte(byteValue);
            } else if (short.class.isAssignableFrom(type)) {
                short shortValue = (short) value;
                return data -> setter.setShort(shortValue);
            } else if (char.class.isAssignableFrom(type)) {
                char charValue = (char) value;
                return data -> setter.setChar(charValue);
            } else if (int.class.isAssignableFrom(type)) {
                int intValue = (int) value;
                return data -> setter.setInt(intValue);
            } else if (long.class.isAssignableFrom(type)) {
                long longValue = (long) value;
                return data -> setter.setLong(longValue);
            } else if (float.class.isAssignableFrom(type)) {
                float floatValue = (float) value;
                return data -> setter.setFloat(floatValue);
            } else if (double.class.isAssignableFrom(type)) {
                double doubleValue = (double) value;
                return data -> setter.setDouble(doubleValue);
            } else {
                return data -> setter.set(value);
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
            return dataFactory -> dataIn -> {
            };
        };
    }

    static class Copy implements Consumer<DidoData> {

        private final Getter getter;

        private final Setter setter;

        Copy(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public void accept(DidoData data) {
            if (getter.has(data)) {
                setter.set(getter.get(data));
            } else {
                setter.clear();
            }
        }
    }

    static class Compute implements Consumer<DidoData> {

        private final Setter setter;

        private final Function<? super DidoData, ?> func;

        Compute(Setter setter, Function<? super DidoData, ?> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data) {
            Object now = func.apply(data);
            if (now == null) {
                setter.clear();
            } else {
                setter.set(now);
            }
        }
    }
}
