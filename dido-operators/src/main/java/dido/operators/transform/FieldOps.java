package dido.operators.transform;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.useful.AbstractFieldGetter;
import dido.data.util.TypeUtil;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

/**
 * Operations on a single filed that can be used in an {@link OpTransformBuilder} to create an
 * {@link DidoTransform}.
 */
public class FieldOps {

    /**
     * Provide fluent copy builders with the same structure. An {@link CopyField}
     * can then define different operations.
     *
     * @param <O> The Operation that will be doing the copy.
     */
    private interface CopyOpFactory<O> {

        O with(CopyTo<O> to);
    }

    /**
     * Specify the copy from. Either the field name or the field index can be specified.
     *
     * @param <O> The Operation Type passed through to the {@link CopyTo#with()}
     */
    public static class CopyField<O> {

        private final CopyOpFactory<O> opFactory;

        private String from;

        private int index;

        private CopyField(CopyOpFactory<O> opFactory) {
            this.opFactory = opFactory;
        }

        /**
         * The name of the field to copy from.
         *
         * @param name A field name. Must exist in the schema. Must not be null.
         *
         * @return An ongoing fluent copy builder to specify the 'to' field.
         */
        public CopyTo<O> from(String name) {
            Objects.requireNonNull(name, "Field name can not be null");
            this.from = name;
            return new CopyTo<>(this);
        }

        /**
         * The index of the field to copy from.
         *
         * @param index A field index. Must exist in the schema. Must be &gt; 0.
         * @return An ongoing fluent copy builder to specify the 'to' field.
         */
        public CopyTo<O> index(int index) {
            if (index < 1) {
                throw new IllegalArgumentException("Field index must be greater than 0");
            }
            this.index = index;
            return new CopyTo<>(this);
        }
    }

    private record SchemaFieldAndGetter(SchemaField schemaField,
                                        FieldGetter fieldGetter) {
    }

    /**
     * Fluent Builder to specify the 'to' options for a copy operation.
     *
     * @param <O> The type of copy operation.
     */
    public static class CopyTo<O> {

        private final CopyOpFactory<O> opFactory;

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
         *
         * @return these fluent options.
         */
        public CopyTo<O> to(String to) {
            this.to = to;
            return this;
        }

        /**
         * The index of the field to copy to. If index exists the field will be replaced.
         *
         * @param at The field index.
         *
         * @return these fluent options.
         */
        public CopyTo<O> at(int at) {
            this.at = at;
            return this;
        }

        /**
         * Copy to the same index in the resultant schema.
         *
         * @return these fluent options.
         */
        public CopyTo<O> atSameIndex() {
            return at(-1);
        }

        /**
         * Copy to the current end of the resultant schema.
         *
         * @return these fluent options.
         */
        public CopyTo<O> atLastIndex() {
            return at(0);
        }

        /**
         * Continue to the final stage of building a copy operation.
         *
         * @return The operation specification.
         */
        public O with() {
            return opFactory.with(this);
        }

        SchemaFieldAndGetter deriveFrom(ReadSchema incomingSchema) {

            FieldGetter getter;
            SchemaField schemaField = null;
            if (from == null) {
                if (index > 0) {
                    schemaField = incomingSchema.getSchemaFieldAt(index);
                }
                if (schemaField == null) {
                    throw new NoSuchFieldException(index, incomingSchema);
                }
                getter = incomingSchema.getFieldGetterAt(index);
            } else {
                schemaField = incomingSchema.getSchemaFieldNamed(from);
                if (schemaField == null) {
                    throw new NoSuchFieldException(from, incomingSchema);
                }
                getter = incomingSchema.getFieldGetterNamed(from);
            }

            return new SchemaFieldAndGetter(schemaField, getter);
        }

        SchemaField deriveTo(DataSchema incomingSchema,
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

        @Override
        public String toString() {
            return "Copy" +
                    (from == null ? "" : " from " + from) +
                    (index > 0 ? " index " + index : "") +
                    (to == null ? "" : " to " + to) +
                    (at > 0 ? " at " + at : "");
        }
    }

    /**
     * Builder for the definition of a simple copy.
     */
    public static class CopyDef {

        private final CopyTo<?> copyTo;

        public CopyDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        /**
         * Create the copy as a view over the incoming data.
         *
         * @return The view.
         */
        public FieldView view() {

            return new FieldView() {
                @Override
                public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                    SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                    SchemaField schemaField = from.schemaField;

                    schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                    viewDefinition.addField(schemaField, from.fieldGetter);
                }

                @Override
                public String toString() {
                    return copyTo.toString();
                }
            };
        }
    }

    static class CopyDefFactory implements CopyOpFactory<CopyDef> {

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
        return new CopyField<>(new CopyDefFactory());
    }

    /**
     * Create an operation that copies the field at an index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static FieldView copyAt(int index) {
        return copy().index(index)
                .with().view();
    }

    /**
     * Create an operation that copies the field from an index at the other index.
     *
     * @param index The index to copy.
     * @return A Copy Operation Definition.
     */
    public static FieldView copyAt(int index, int at) {

        return copy().index(index)
                .at(at)
                .with().view();
    }

    /**
     * Create an operation that copies the named field. The field will be copied to the same index
     * in the resultant schema.
     *
     * @param name The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static FieldView copyNamed(String name) {

        return copy().from(name)
                .with().view();
    }

    /**
     * Create an operation that copies the named field to another name. The field will be copied to the same
     * index in the resultant schema.
     *
     * @param from The field name to copy.
     * @param to   The field name to copy.
     * @return A Copy Operation Definition.
     */
    public static FieldView copyNamed(String from, String to) {

        return copy().from(from)
                .to(to)
                .with().view();
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
    public static FieldView copyNamedAt(String from, int at) {
        return copy().from(from)
                .at(at)
                .with().view();
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
    public static FieldView copyNamedAt(String from, int at, String to) {

        return copy().from(from)
                .to(to)
                .at(at)
                .with().view();
    }

    /**
     * Rename a field.
     *
     * @param from The existing field name.
     * @param to   The new field name.
     * @return The operation definition.
     */
    public static FieldView rename(String from, String to) {
        return renameAt(from, -1, to);
    }

    /**
     * Rename a field and give it a new index.
     *
     * @param from The existing field name.
     * @param at   The new index.
     * @param to   The new field name.
     * @return An operation definition.
     */
    public static FieldView renameAt(String from, int at, String to) {

        return (incomingSchema, definition) -> {

            copy()
                    .from(Objects.requireNonNull(from, "No From"))
                    .to(Objects.requireNonNull(to, "No To"))
                    .at(at)
                    .with().view().define(incomingSchema, definition);

            definition.removeField(incomingSchema.getSchemaFieldNamed(from));
        };
    }

    /**
     * Provide fluent setting builders with the same structure. An {@link SetField}
     * can then define different operations.
     *
     * @param <O> The Operation that will be doing the setting.
     */
    private interface SetOpFactory<O> {

        O with(SetField<O> set);
    }

    /**
     * Specify the set location operation. Either the field name or the field index can be specified.
     *
     * @param <O> The Operation Type passed through to the {@link CopyTo#with()}
     */
    public static class SetField<O> {

        private final SetOpFactory<O> opFactory;

        private String name;

        private int at;

        private SetField(SetOpFactory<O> opFactory) {
            this.opFactory = opFactory;
        }

        /**
         * The name of the field to set the value at.
         *
         * @param name The field name. If it exists the existing value will be overwritten. Must not be null.
         *
         * @return These field setting options.
         */
        public SetField<O> named(String name) {
            Objects.requireNonNull(name, "Field name can not be null");
            this.name = name;
            return this;
        }

        /**
         * The index of the field to set the value at.
         *
         * @param index A field index. Must exist in the schema. Must be &gt; 0.
         *
         * @return These field setting options.
         */
        public SetField<O> at(int index) {
            if (index < 1) {
                throw new IllegalArgumentException("Field index must be greater than 0");
            }
            this.at = index;
            return this;
        }

        /**
         * Continue to the final stage of building a set operation.
         *
         * @return The operation specification.
         */
        public O with() {
            return opFactory.with(this);
        }

        @Override
        public String toString() {
            return "Set" +
                    (name == null ? "" : " named " + name) +
                    (at > 0 ? " at " + at : "");
        }
    }

    static class SetValueFactory implements SetOpFactory<SetValue> {

        @Override
        public SetValue with(SetField<SetValue> setField) {
            return new SetValue(setField);
        }
    }

    /**
     * Builder for a Set Value operation.
     */
    public static class SetValue {

        private final SetField<?> setField;

        private Object value;

        private Type type;

        private DidoConversionProvider conversionProvider;

        SetValue(SetField<?> setField) {
            this.setField = setField;
        }

        /**
         * Provide the value to set.
         *
         * @param value The value.
         *
         * @return This builder for more options.
         */
        public SetValue value(Object value) {
            this.value = value;
            return this;
        }

        /**
         * Specify The type of the value.
         *
         * @param type The type.
         *
         * @return This builder for more options.
         */
        public SetValue type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * Provide a Conversion Provider that will be used to convert the value to the type.
         *
         * @param conversionProvider A Conversion Provider.
         *
         * @return This builder for more options.
         */
        public SetValue conversionProvider(DidoConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
            return this;
        }

        /**
         * Create the set as a view over the incoming data.
         *
         * @return The view.
         */
        public FieldView view() {

            Type valueType = value == null ? void.class : value.getClass();

            return new FieldView() {

                SchemaField finalField(ReadSchema incomingSchema) {

                    SchemaField schemaField = null;
                    if (setField.name == null) {
                        if (setField.at > 0) {
                            schemaField = incomingSchema.getSchemaFieldAt(setField.at);
                        }
                    } else {
                        schemaField = incomingSchema.getSchemaFieldNamed(setField.name);
                    }

                    if (schemaField == null) {
                        Type newType = Objects.requireNonNullElse(type, valueType);
                        schemaField = SchemaField.of(Math.max(setField.at, 0), setField.name, newType);
                    } else {
                        Type newType = Objects.requireNonNullElse(type, schemaField.getType());
                        schemaField = SchemaField.of(schemaField.getIndex(), schemaField.getName(), newType);
                    }

                    if (setField.at >= 0) {
                        schemaField = schemaField.mapToIndex(setField.at);
                    }

                    return schemaField;
                }

                Object convertedValue(Type type) {

                    if (value == null) {
                        return null;
                    } else {
                        DidoConversionProvider conversionProviderOrDefault = Objects.requireNonNullElse(
                                conversionProvider,
                                DefaultConversionProvider.defaultInstance());

                        return conversionProviderOrDefault.conversionFor(value.getClass(), type).apply(value);
                    }
                }

                @Override
                public void define(ReadSchema incomingSchema, FieldView.Definition viewDefinition) {

                    SchemaField schemaField = finalField(incomingSchema);

                    final Object value = convertedValue(schemaField.getType());

                    viewDefinition.addField(finalField(incomingSchema), constGetterFor(value));
                }

                @Override
                public OpDef asOpDef() {
                    return (incomingSchema, schemaSetter) -> {

                        SchemaField finalField = finalField(incomingSchema);

                        schemaSetter.addField(finalField);

                        final Object value = convertedValue(finalField.getType());

                        return writeSchema -> {

                            FieldSetter fieldSetter = writeSchema.getFieldSetterNamed(finalField.getName());

                            return setterFactoryFor(fieldSetter,
                                    finalField.getName(), value,
                                    TypeUtil.classOf(finalField.getType()));
                        };
                    };
                }
            };
        }
    }

    /**
     * Create a set operation using fluent field locations.
     *
     * @return Fluent fields to define the set.
     */
    public static SetField<SetValue> set() {
        return new SetField<>(new SetValueFactory());
    }

    /**
     * Create an operation to set the field at the given index to be the given value. If the index
     * exists the field name will be preserved.
     *
     * @param at    The index to set the value at.
     * @param value The value to set.
     * @return The Operation Definition.
     */
    public static FieldView setAt(int at,
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
    public static FieldView setAt(int at,
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
    public static FieldView setNamed(String name,
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
    public static FieldView setNamed(String name,
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
    public static FieldView setNamedAt(int at,
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
    public static FieldView setNamedAt(int at,
                                       String name,
                                       Object value,
                                       Class<?> type) {

        Class<?> type_ = Objects.requireNonNullElseGet(type,
                () -> value == null ? Void.class : value.getClass());

        return new FieldView() {

            SchemaField finalField(ReadSchema incomingSchema) {

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

                return schemaField;
            }

            @Override
            public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                viewDefinition.addField(finalField(incomingSchema), constGetterFor(value));
            }

            @Override
            public OpDef asOpDef() {
                return (incomingSchema, schemaSetter) -> {

                    SchemaField finalField = finalField(incomingSchema);

                    schemaSetter.addField(finalField);

                    return writeSchema -> {

                        FieldSetter fieldSetter = writeSchema.getFieldSetterNamed(finalField.getName());

                        return setterFactoryFor(fieldSetter,
                                finalField.getName(), value,
                                TypeUtil.classOf(finalField.getType()));
                    };
                };
            }
        };
    }

    static FieldGetter constGetterFor(Object value) {

        return switch (value) {
            case Boolean b -> new AbstractFieldGetter.ForBoolean() {
                @Override
                public boolean getBoolean(DidoData data) {
                    return b;
                }
            };
            case Byte b -> new AbstractFieldGetter.ForByte() {
                @Override
                public byte getByte(DidoData data) {
                    return b;
                }
            };
            case Character c -> new AbstractFieldGetter.ForChar() {
                @Override
                public char getChar(DidoData data) {
                    return c;
                }
            };
            case Short i -> new AbstractFieldGetter.ForShort() {
                @Override
                public short getShort(DidoData data) {
                    return i;
                }
            };
            case Integer i -> new AbstractFieldGetter.ForInt() {
                @Override
                public int getInt(DidoData data) {
                    return i;
                }
            };
            case Long l -> new AbstractFieldGetter.ForLong() {
                @Override
                public long getLong(DidoData data) {
                    return l;
                }
            };
            case Float v -> new AbstractFieldGetter.ForFloat() {
                @Override
                public float getFloat(DidoData data) {
                    return v;
                }
            };
            case Double v -> new AbstractFieldGetter.ForDouble() {
                @Override
                public double getDouble(DidoData data) {
                    return v;
                }
            };
            case String s -> new AbstractFieldGetter.ForString() {
                @Override
                public String getString(DidoData data) {
                    return s;
                }
            };
            case null, default -> new AbstractFieldGetter() {

                @Override
                public Object get(DidoData data) {
                    return value;
                }
            };
        };
    }

    /**
     * @param to    The field name to.
     * @param value The value to set.
     * @param type  The type.
     * @return The prepare step.
     */
    static BiConsumer<DidoData, WritableData> setterFactoryFor(FieldSetter setter,
                                                               String to,
                                                               Object value,
                                                               Class<?> type) {

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
    }

    /**
     * Remove a field by name.
     *
     * @param name The name of the field.
     * @return The operation definition.
     */
    public static FieldView removeNamed(String name) {

        return (incomingSchema, viewDefinition) -> {
            SchemaField field = incomingSchema.getSchemaFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, incomingSchema);
            }
            viewDefinition.removeField(field);
        };
    }

    /**
     * Remove a field by index.
     *
     * @param index The index of the field.
     * @return The operation definition.
     */
    public static FieldView removeAt(int index) {

        return (incomingSchema, viewDefinition) -> {
            SchemaField field = incomingSchema.getSchemaFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }
            viewDefinition.removeField(field);
        };
    }

    /**
     * Create a conversion mapping.
     *
     * @return A fluent builder.
     */
    public static CopyField<ConversionDef> conversion() {
        return new CopyField<>(new ConversionDefFactory());
    }

    public static class ConversionDefFactory implements CopyOpFactory<ConversionDef> {

        @Override
        public ConversionDef with(CopyTo<ConversionDef> to) {
            return new ConversionDef(to);
        }
    }

    public static class ConversionDef {

        private final CopyTo<?> copyTo;

        private DidoConversionProvider conversionProvider;

        ConversionDef(CopyTo<?> copyTo) {
            this.copyTo = copyTo;
        }

        public ConversionDef conversionProvider(DidoConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
            return this;
        }

        /**
         * Define a new type for the resultant field.
         *
         * @param type The type.
         * @return Ongoing mapping definition.
         */
        public FieldView toType(Type type) {

            Objects.requireNonNull(type, "No Type");

            return (incomingSchema, viewDefinition) -> {

                SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);

                SchemaField schemaField = copyTo.deriveTo(incomingSchema, from.schemaField);
                schemaField = SchemaField.of(schemaField.getIndex(), schemaField.getName(), type);

                Function<?, ?> conversion = Objects.requireNonNullElse(conversionProvider,
                                DefaultConversionProvider.defaultInstance())
                        .conversionFor(from.schemaField.getType(), type);


                FieldGetter fieldGetter = new AbstractFieldGetter() {
                    @Override
                    public Object get(DidoData data) {
                        //noinspection rawtypes,unchecked
                        return ((Function) conversion).apply(from.fieldGetter.get(data));
                    }
                };

                viewDefinition.addField(schemaField, fieldGetter);
            };
        }
    }

    public static class FuncMapDef {

        private final CopyTo<?> copyTo;

        private final Class<?> type;

        FuncMapDef(CopyTo<?> copyTo) {
            this(copyTo, null);
        }

        FuncMapDef(CopyTo<?> copyTo,
                   Class<?> type) {
            this.copyTo = copyTo;
            this.type = type;
        }

        /**
         * Define a new type for the resultant field.
         *
         * @param type The type.
         * @return Ongoing mapping definition.
         */
        public FuncMapDef type(Class<?> type) {
            return new FuncMapDef(copyTo, type);
        }

        /**
         * Apply a mapping function.
         *
         * @param func The function.
         * @return The op
         */
        public FieldView func(Function<?, ?> func) {

            return new FieldView() {
                @Override
                public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                    SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);

                    SchemaField schemaField = copyTo.deriveTo(incomingSchema, from.schemaField);
                    if (type != null) {
                        schemaField = SchemaField.of(schemaField.getIndex(), schemaField.getName(), type);
                    }

                    FieldGetter fieldGetter = new AbstractFieldGetter() {
                        @Override
                        public Object get(DidoData data) {
                            //noinspection rawtypes,unchecked
                            return ((Function) func).apply(from.fieldGetter.get(data));
                        }
                    };

                    viewDefinition.addField(schemaField, fieldGetter);
                }

                @Override
                public String toString() {
                    return copyTo + " with Function " + func;
                }
            };
        }

        /**
         * Apply a unary int operation.
         *
         * @param func The operation.
         * @return The op
         */
        public FieldView intOp(IntUnaryOperator func) {

            return new MapFieldView() {
                @Override
                public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                    SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);

                    FieldGetter fieldGetter = new AbstractFieldGetter.ForInt() {

                        @Override
                        public int getInt(DidoData data) {
                            return func.applyAsInt(from.fieldGetter.getInt(data));
                        }
                    };

                    viewDefinition.addField(
                            copyTo.deriveTo(incomingSchema, from.schemaField),
                            fieldGetter);
                }

                @Override
                BiConsumer<DidoData, WritableData> doMap(FieldGetter getter, FieldSetter setter) {
                    return (didoData, writableData) ->
                            setter.setInt(writableData, getter.getInt(didoData));
                }
            };
        }

        /**
         * Apply a unary long operation.
         *
         * @param func The operation.
         * @return The op
         */
        public FieldView longOp(LongUnaryOperator func) {

            return new MapFieldView() {

                @Override
                public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                    SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);

                    FieldGetter fieldGetter = new AbstractFieldGetter.ForLong() {
                        @Override
                        public long getLong(DidoData data) {
                            return func.applyAsLong(from.fieldGetter.getInt(data));
                        }
                    };

                    viewDefinition.addField(
                            copyTo.deriveTo(incomingSchema, from.schemaField),
                            fieldGetter);
                }

                @Override
                BiConsumer<DidoData, WritableData> doMap(FieldGetter getter, FieldSetter setter) {
                    return (didoData, writableData) ->
                            setter.setLong(writableData, getter.getLong(didoData));
                }
            };
        }

        public FieldView doubleOp(DoubleUnaryOperator func) {

            return new MapFieldView() {

                @Override
                public void define(ReadSchema incomingSchema, Definition viewDefinition) {

                    SchemaFieldAndGetter from = copyTo.deriveFrom(incomingSchema);
                    SchemaField schemaField = from.schemaField;

                    schemaField = copyTo.deriveTo(incomingSchema, schemaField);

                    FieldGetter fieldGetter = new AbstractFieldGetter.ForDouble() {
                        @Override
                        public double getDouble(DidoData data) {
                            return func.applyAsDouble(from.fieldGetter.getDouble(data));
                        }
                    };

                    viewDefinition.addField(schemaField, fieldGetter);
                }

                @Override
                BiConsumer<DidoData, WritableData> doMap(FieldGetter getter, FieldSetter setter) {
                    return (didoData, writableData) ->
                            setter.setDouble(writableData, getter.getDouble(didoData));
                }
            };
        }
    }

    static abstract class MapFieldView implements FieldView {

        @Override
        public OpDef asOpDef() {

            AtomicReference<SchemaField> schemaFieldRef = new AtomicReference<>();
            AtomicReference<FieldGetter> fieldGetterRef = new AtomicReference<>();

            return (incomingSchema, schemaSetter) -> {

                define(incomingSchema, new Definition() {
                    @Override
                    public void addField(SchemaField schemaField, FieldGetter fieldGetter) {
                        schemaFieldRef.set(schemaSetter.addField(schemaField));
                        fieldGetterRef.set(fieldGetter);
                    }

                    @Override
                    public void removeField(SchemaField schemaField) {
                        throw new UnsupportedOperationException("Unexpected");
                    }
                });

                FieldGetter getter = fieldGetterRef.get();

                return writeSchema -> {
                    FieldSetter setter = writeSchema.getFieldSetterNamed(schemaFieldRef.get().getName());

                    return doMap(getter, setter);
                };
            };
        }

        abstract BiConsumer<DidoData, WritableData> doMap(FieldGetter getter,
                                                          FieldSetter setter);
    }

    public static class FuncMapDefFactory implements CopyOpFactory<FuncMapDef> {

        @Override
        public FuncMapDef with(CopyTo<FuncMapDef> to) {
            return new FuncMapDef(to);
        }
    }

    /**
     * Create an operation to copy a field applying a function
     * using fluent field locations.
     *
     * @return fluent fields to define the copy.
     */
    public static CopyField<FuncMapDef> map() {
        return new CopyField<>(new FuncMapDefFactory());
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
