package dido.data.generic;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.NoSuchFieldException;
import dido.data.ReadStrategy;

import java.util.Objects;

public interface GenericReadStrategy<F> extends ReadStrategy {

    FieldGetter getFieldGetter(F field);

    static <F> GenericReadStrategy<F> forSchema(GenericDataSchema<F> schema) {

        if (schema instanceof GenericReadStrategy) {
            return (GenericReadStrategy<F>) schema;
        } else {
            return new GenericReadStrategy<F>() {

                @Override
                public FieldGetter getFieldGetter(F field) {
                    if (schema.hasField((Objects.requireNonNull(field)))) {

                        return new FieldGetter() {
                            @Override
                            public Object get(DidoData data) {
                                return ((GenericData<F>) data).get(field);
                            }

                            @Override
                            public boolean has(DidoData data) {
                                return ((GenericData<F>) data).has(field);
                            }

                            @Override
                            public boolean getBoolean(DidoData data) {
                                return ((GenericData<F>) data).getBoolean(field);
                            }

                            @Override
                            public char getChar(DidoData data) {
                                return ((GenericData<F>) data).getChar(field);
                            }

                            @Override
                            public byte getByte(DidoData data) {
                                return ((GenericData<F>) data).getByte(field);
                            }

                            @Override
                            public short getShort(DidoData data) {
                                return ((GenericData<F>) data).getShort(field);
                            }

                            @Override
                            public int getInt(DidoData data) {
                                return ((GenericData<F>) data).getInt(field);
                            }

                            @Override
                            public long getLong(DidoData data) {
                                return ((GenericData<F>) data).getLong(field);
                            }

                            @Override
                            public float getFloat(DidoData data) {
                                return ((GenericData<F>) data).getFloat(field);
                            }

                            @Override
                            public double getDouble(DidoData data) {
                                return ((GenericData<F>) data).getDouble(field);
                            }

                            @Override
                            public String getString(DidoData data) {
                                return ((GenericData<F>) data).getString(field);
                            }
                        };
                    } else {
                        throw new NoSuchFieldException(field.toString(), schema);
                    }
                }

                @Override
                public FieldGetter getFieldGetterAt(int index) {
                    F field = schema.getFieldAt(index);
                    if (field == null) {
                        throw new NoSuchFieldException(index, schema);
                    } else {
                        return getFieldGetter(field);
                    }
                }

                @Override
                public FieldGetter getFieldGetterNamed(String name) {
                    F field = schema.getFieldNamed(name);
                    if (field == null) {
                        throw new NoSuchFieldException(name, schema);
                    } else {
                        return getFieldGetter(field);
                    }
                }
            };
        }
    }
}
