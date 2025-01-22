package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Objects;
import java.util.function.*;

/**
 * Operations on a single filed that can be used in an {@link OpTransformBuilder} to create an
 * {@link DidoTransform}.
 */
public class FieldOps {

    private interface OpFactory<O> {

        O with(CopyTo<O> to);
    }

    /**
     * Specify the copy from. Either the field name or the field index can be specified.
     *
     * @param <O> The Operation Type passed through to the {@link CopyTo#with()}
     */
    public static class CopyField<O> {

        private final OpFactory<O> opFactory;

        private String from;

        private int index;

        private CopyField(OpFactory<O> opFactory) {
            this.opFactory = opFactory;
        }

        public CopyTo<O> from(String name) {
            Objects.requireNonNull(name, "Field name can not be null");
            this.from = name;
            return new CopyTo<>(this);
        }

        public CopyTo<O> index(int index) {
            if (index < 1) {
                throw new IllegalArgumentException("Field index must be greater than 0");
            }
            this.index = index;
            return new CopyTo<>(this);
        }
    }

    private static class SchemaFieldAndGetter {

        private final SchemaField schemaField;

        private final FieldGetter fieldGetter;

        SchemaFieldAndGetter(SchemaField schemaField, FieldGetter fieldGetter) {
            this.schemaField = schemaField;
            this.fieldGetter = fieldGetter;
        }
    }

    public static class CopyTo<O> {

        private final OpFactory<O> opFactory;

        private final String from;

        private final int index;

        private String to;

        private int at;

        private CopyTo(CopyField<O> copyField) {
            this.opFactory = copyField.opFactory;
            this.from = copyField.from;
            this.index = copyField.index;
            this.at = copyField.index > 0 ? copyField.index : -2;
        }

        /**
         * The name of the field to copy to. If the name exists the field will be replaced.
         *
         * @param to The field name.
         * @return these fluent options.
         */
        public CopyTo<O> to(String to) {
            this.to = to;
            return this;
        }

        public CopyTo<O> at(int at) {
            this.at = at;
            return this;
        }

        public CopyTo<O> atSameIndex() {
            return at(-1);
        }

        public CopyTo<O> atLastIndex() {
            return at(0);
        }

        public O with() {
            return opFactory.with(this);
        }

        private SchemaFieldAndGetter deriveFrom(DataSchema incomingSchema) {

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter;
            SchemaField schemaField = null;
            if (from == null) {
                if (index > 0) {
                    schemaField = incomingSchema.getSchemaFieldAt(index);
                }
                if (schemaField == null) {
                    throw new NoSuchFieldException(index, incomingSchema);
                }
                getter = readStrategy.getFieldGetterAt(index);
            } else {
                schemaField = incomingSchema.getSchemaFieldNamed(from);
                if (schemaField == null) {
                    throw new NoSuchFieldException(from, incomingSchema);
                }
                getter = readStrategy.getFieldGetterNamed(from);
            }

            return new SchemaFieldAndGetter(schemaField, getter);
        }

        private SchemaField deriveTo(DataSchema incomingSchema,
                                     SchemaField fromField) {

            SchemaField schemaField;
            if (to == null) {
                schemaField = null;
                if (at > 0) {
                    // If a field exists with the index then use it.
                    schemaField = incomingSchema.getSchemaFieldNamed(fromField.getName());
                }
                if (schemaField == null) {
                    schemaField = fromField;
                }
            } else {
                // If a field exists with the given name then use it.
                schemaField = incomingSchema.getSchemaFieldNamed(to);
                if (schemaField == null) {
                    schemaField = fromField.mapToFieldName(to);
                    // New field, so by default we want it at the end.
                    if (at == -2) {
                        atLastIndex();
                    }
                }
            }

            if (at >= 0) {
                schemaField = schemaField.mapToIndex(at);
            }

            return schemaField;
        }

    }

    public static class CopyDef {

        private final CopyTo<?> copyTo;

        public CopyDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        public OpDef out() {

            return (incomingSchema, schemaSetter) -> {

                SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                SchemaField schemaField = from.schemaField;

                schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                SchemaField finalField = schemaSetter.addField(schemaField);

                return dataFactory -> new Copy(
                        from.fieldGetter,
                        dataFactory.getFieldSetterNamed(finalField.getName()));
            };
        }
    }

    public static class CopyDefFactory<T> implements OpFactory<CopyDef> {

        @Override
        public CopyDef with(CopyTo<CopyDef> to) {
            return new CopyDef(to);
        }
    }

    /**
     * Create a copy operation using fluent field locations.
     *
     * @return Fluent fields to define the copy.
     */
    public static CopyField<CopyDef> copy() {
        return new CopyField<>(new CopyDefFactory<>());
    }

    /**
     * Create an operation that copies the field at an index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyAt(int index) {
        return copy().index(index)
                .with().out();
    }

    /**
     * Create an operation that copies the field from an index at the other index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyAt(int index, int at) {

        return copy().index(index)
                .at(at)
                .with().out();
    }

    /**
     * Create an operation that copies the named field. The field will be copied to the same index
     * in the resultant schema.
     *
     * @param name The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static OpDef copyNamed(String name) {

        return copy().from(name)
                .with().out();
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

        return copy().from(from)
                .to(to)
                .with().out();
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
        return copy().from(from)
                .at(at)
                .with().out();
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

        return copy().from(from)
                .to(to)
                .at(at)
                .with().out();
    }

    /**
     * Create an operation to set the field at the given index to be the given value. If the index
     * exists the field name will be preserved.
     *
     * @param at    The index to set the value at.
     * @param value The value to set.
     * @return The Operation Definition.
     */
    public static OpDef setAt(int at,
                              Object value) {
        return setNamedAt(at, null, value, null);
    }

    /**
     * Create an operation to set the field at the given index to be the given value, with a schema type
     * of the given type. If the index exists the field name will be preserved. Specifying a type is useful
     * when the new field is to be a primitive type, or a super class of the value. No check is made that the
     * value is assignable to the type.
     *
     * @param at    The index to set the value at.
     * @param value The value to set.
     * @param type  The type of the field.
     * @return The Operation Definition.
     */
    public static OpDef setAt(int at,
                              Object value,
                              Class<?> type) {
        return setNamedAt(at, null, value, type);
    }

    /**
     * Create an operation to set a field with the given name to be the given value. If the field of this name
     * exists the field index will be preserved.
     *
     * @param name  The name of the field.
     * @param value The value to set.
     * @return The Operation Definition.
     */
    public static OpDef setNamed(String name,
                                 Object value) {
        return setNamedAt(-1, name, value, null);
    }

    /**
     * Create an operation to set a field with the given name to be the given value, with a schema type
     * of the given type. If the field of this name exists the field index will be preserved. Specifying a type
     * is useful when the new field is to be a primitive type, or a super class of the value. No check is made that the
     * value is assignable to the type.
     *
     * @param name  The name of the field.
     * @param value The value to set.
     * @param type  The type of the field.
     * @return The Operation Definition.
     */
    public static OpDef setNamed(String name,
                                 Object value,
                                 Class<?> type) {
        return setNamedAt(-1, name, value, type);
    }

    /**
     * Create an operation to set a field with the given name to be the given value, at the given index.
     * If the index is 0, the new field will be added to the schema, if it is negative the existing index
     * is used if it exists.
     *
     * @param at    The index to set the value at.
     * @param name  The name of the field.
     * @param value The value to set.
     * @return The Operation Definition.
     */
    public static OpDef setNamedAt(int at,
                                   String name,
                                   Object value) {

        return setNamedAt(at, name, value, null);
    }

    /**
     * Create an operation to set a field with the given name to be the given value, at the given index,
     * with a schema type of the given type.
     * If the index is 0, the new field will be added to the schema, if it is negative the existing index
     * is used if it exists. Specifying a type
     * is useful when the new field is to be a primitive type, or a super class of the value. No check is made that the
     * value is assignable to the type.
     *
     * @param at    The index to set the value at.
     * @param name  The name of the field.
     * @param value The value to set.
     * @param type  The type of the field.
     * @return The Operation Definition.
     */
    public static OpDef setNamedAt(int at,
                                   String name,
                                   Object value,
                                   Class<?> type) {

        Class<?> type_ = Objects.requireNonNullElseGet(type,
                () -> value == null ? Void.class : value.getClass());

        return (incomingSchema, schemaSetter) -> {

            SchemaField schemaField = null;
            if (name == null) {
                if (at > 0) {
                    schemaField = incomingSchema.getSchemaFieldAt(at);
                }
            } else {
                schemaField = incomingSchema.getSchemaFieldNamed(name);
            }

            if (schemaField == null) {
                schemaField = SchemaField.of(Math.max(at, 0), name, type_);
            } else {
                schemaField = SchemaField.of(schemaField.getIndex(), schemaField.getName(), type_);
            }

            if (at >= 0) {
                schemaField = schemaField.mapToIndex(at);
            }

            schemaField = schemaSetter.addField(schemaField);

            return setterFactoryFor(schemaField.getName(), value, schemaField.getType());
        };
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

    /**
     * Remove a field by name.
     *
     * @param name The name of the field.
     * @return The operation definition.
     */
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

    /**
     * Remove a field by index.
     *
     * @param index The index of the field.
     * @return The operation definition.
     */
    public static OpDef removeAt(int index) {

        return (incomingSchema, schemaSetter) -> {
            SchemaField field = incomingSchema.getSchemaFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }
            schemaSetter.removeField(field);
            return dataFactory -> (dataIn, dataOut) -> {
                // Nothing to do - the new data is assumed not to have the field.
            };
        };
    }

    public static class UnaryMapDef<T> {

        private final CopyTo<?> copyTo;

        private UnaryOperator<T> func;

        public UnaryMapDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        public OpDef unaryOperator(UnaryOperator<T> func) {

            return (incomingSchema, schemaSetter) -> {

                SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                SchemaField schemaField = from.schemaField;

                schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                SchemaField finalField = schemaSetter.addField(schemaField);

                //noinspection unchecked
                return dataFactory -> new Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                        data -> func.apply((T) from.fieldGetter.get(data)));
            };
        }
    }

    public static class UnaryMapDefFactory<T> implements OpFactory<UnaryMapDef<T>> {

        @Override
        public UnaryMapDef<T> with(CopyTo<UnaryMapDef<T>> to) {
            return new UnaryMapDef<>(to);
        }
    }

    public static <T> CopyField<UnaryMapDef<T>> unaryMap() {
        return new CopyField<>(new UnaryMapDefFactory<>());
    }

    public static <T> OpDef mapAt(int at,
                                  UnaryOperator<T> func) {
        return mapNamedAt(null, at, null, func, null);
    }

    public static <T> OpDef mapNamed(String from,
                                     UnaryOperator<T> func) {
        return mapNamedAt(from, -1, from, func, null);
    }

    public static <T> OpDef mapNamed(String from,
                                     String to,
                                     UnaryOperator<T> func) {
        return mapNamedAt(from, -1, to, func, null);
    }

    public static <T, R> OpDef mapNamed(String from,
                                        String to,
                                        Function<? super T, ? extends R> func,
                                        Class<R> type) {
        return mapNamedAt(from, -1, to, func, type);
    }

    public static <T, R> OpDef mapNamedAt(String from,
                                          int at,
                                          String to,
                                          Function<? super T, ? extends R> func,
                                          Class<R> type) {

        return (incomingSchema, schemaSetter) -> {

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter getter;
            SchemaField schemaField = null;
            if (from == null) {
                if (at > 0) {
                    schemaField = incomingSchema.getSchemaFieldAt(at);
                }
                if (schemaField == null) {
                    throw new NoSuchFieldException(at, incomingSchema);
                }
                getter = readStrategy.getFieldGetterAt(at);
            } else {
                schemaField = incomingSchema.getSchemaFieldNamed(from);
                if (schemaField == null) {
                    throw new NoSuchFieldException(from, incomingSchema);
                }
                getter = readStrategy.getFieldGetterNamed(from);
            }

            if (to != null) {
                schemaField = schemaField.mapToFieldName(to);
            }
            if (at >= 0) {
                schemaField = schemaField.mapToIndex(at);
            }
            if (type != null && schemaField.getType() != type) {
                schemaField = SchemaField.of(schemaField.getIndex(), schemaField.getName(), type);
            }

            SchemaField finalField = schemaSetter.addField(schemaField);

            //noinspection unchecked
            return dataFactory -> new Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                    data -> func.apply((T) getter.get(data)));
        };
    }

    // Int to Int

    public static class IntToIntMapDef {

        private final CopyTo<?> copyTo;

        public IntToIntMapDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        public OpDef unaryOperator(IntUnaryOperator func) {

            return (incomingSchema, schemaSetter) -> {

                SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                SchemaField schemaField = from.schemaField;

                schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                SchemaField finalField = schemaSetter.addField(schemaField);

                return dataFactory -> new IntCompute(
                        dataFactory.getFieldSetterNamed(finalField.getName()),
                        data -> func.applyAsInt(from.fieldGetter.getInt(data)));
            };
        }
    }

    public static class IntToIntMapDefFactory implements OpFactory<IntToIntMapDef> {

        @Override
        public IntToIntMapDef with(CopyTo<IntToIntMapDef> to) {
            return new IntToIntMapDef(to);
        }
    }

    /**
     * Create an operation to copy an int field applying a unary operation
     * using fluent field locations.
     *
     * @return fluent fields to define the copy.
     */
    public static CopyField<IntToIntMapDef> mapIntToInt() {
        return new CopyField<>(new IntToIntMapDefFactory());
    }

    // Double To Double

    public static class DoubleToDoubleMapDef {

        private final CopyTo<?> copyTo;

        public DoubleToDoubleMapDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        public OpDef unaryOperator(DoubleUnaryOperator func) {

            return (incomingSchema, schemaSetter) -> {

                SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                SchemaField schemaField = from.schemaField;

                schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                SchemaField finalField = schemaSetter.addField(schemaField);

                return dataFactory -> new DoubleCompute(
                        dataFactory.getFieldSetterNamed(finalField.getName()),
                        data -> func.applyAsDouble(from.fieldGetter.getDouble(data)));
            };
        }
    }

    public static class DoubleToDoubleMapDefFactory implements OpFactory<DoubleToDoubleMapDef> {

        @Override
        public DoubleToDoubleMapDef with(CopyTo<DoubleToDoubleMapDef> to) {
            return new DoubleToDoubleMapDef(to);
        }
    }

    /**
     * Create an operation to copy a double field applying a unary operation
     * using fluent field locations.
     *
     * @return fluent fields to define the copy.
     */
    public static CopyField<DoubleToDoubleMapDef> mapDoubleToDouble() {
        return new CopyField<>(new DoubleToDoubleMapDefFactory());
    }

    // Whole Data Computes - Move to experimental.

    public static <T> OpDef computeFromDataNamed(String to,
                                                 Function<? super DidoData, ? extends T> func,
                                                 Class<T> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            } else {
                field = SchemaField.of(field.getIndex(), to, type);
            }

            SchemaField finalField = schemaSetter.addField(field);

            return dataFactory -> new Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                    func);
        };
    }

    public static <T> OpDef computeNamedGetter(String to,
                                               Function<? super ReadSchema,
                                                       Function<? super DidoData, ? extends T>> func,
                                               Class<T> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            } else {
                field = SchemaField.of(field.getIndex(), to, type);
            }

            SchemaField finalField = schemaSetter.addField(field);

            return dataFactory -> new Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                    func.apply(ReadSchema.from(incomingSchema)));
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

    static class IntCompute implements BiConsumer<DidoData, WritableData> {

        private final FieldSetter setter;

        private final ToIntFunction<? super DidoData> func;

        IntCompute(FieldSetter setter, ToIntFunction<? super DidoData> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data, WritableData out) {
            int now = func.applyAsInt(data);
            setter.setInt(out, now);
        }
    }

    static class DoubleCompute implements BiConsumer<DidoData, WritableData> {

        private final FieldSetter setter;

        private final ToDoubleFunction<? super DidoData> func;

        DoubleCompute(FieldSetter setter, ToDoubleFunction<? super DidoData> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data, WritableData out) {
            double now = func.applyAsDouble(data);
            setter.setDouble(out, now);
        }
    }
}
