package dido.sql;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractDataSchema;
import dido.how.DataException;
import dido.how.FieldAccessException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ResultSetWrapper extends AbstractData implements DidoData {

    private final ResultSet resultSet;

    private final Schema schema;

    private ResultSetWrapper(ResultSet resultSet, Schema schema) {
        this.resultSet = resultSet;
        this.schema = schema;
    }

    public static DidoData from(ResultSet resultSet,
                                 ClassLoader classLoader)
            throws SQLException, ClassNotFoundException {

        return from(resultSet, null,
                classLoader);
    }

    public static DidoData from(ResultSet resultSet,
                                 DataSchema schema,
                                 ClassLoader classLoader)
            throws SQLException, ClassNotFoundException {

        return new ResultSetWrapper(resultSet,
                new Schema(resultSet.getMetaData(),
                        Objects.requireNonNullElseGet(schema, DataSchema::emptySchema),
                        classLoader));
    }


    @Override
    public ReadSchema getSchema() {
        return schema;
    }

    @Override
    public Object getNamed(String field) {
        return schema.getFieldGetterNamed(field).get(this);
    }

    @Override
    public boolean hasNamed(String field) {
        try {
            resultSet.getObject(field);
            return !resultSet.wasNull();
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public boolean getBooleanNamed(String field) {
        try {
            return resultSet.getBoolean(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public byte getByteNamed(String field) {
        try {
            return resultSet.getByte(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public char getCharNamed(String field) {
        try {
            return (char) resultSet.getInt(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public short getShortNamed(String field) {
        try {
            return resultSet.getShort(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public int getIntNamed(String field) {
        try {
            return resultSet.getInt(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public long getLongNamed(String field) {
        try {
            return resultSet.getLong(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public float getFloatNamed(String field) {
        try {
            return resultSet.getFloat(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public double getDoubleNamed(String field) {
        try {
            return resultSet.getDouble(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public String getStringNamed(String field) {
        try {
            return resultSet.getString(field);
        } catch (SQLException e) {
            throw new FieldAccessException(field, schema, e);
        }
    }

    @Override
    public Object getAt(int index) {
        return schema.getFieldGetterAt(index).get(this);
    }

    @Override
    public boolean hasAt(int index) {
        try {
            resultSet.getObject(index);
            return !resultSet.wasNull();
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public boolean getBooleanAt(int index) {
        try {
            return resultSet.getBoolean(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public byte getByteAt(int index) {
        try {
            return resultSet.getByte(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public char getCharAt(int index) {
        try {
            return (char) resultSet.getInt(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public short getShortAt(int index) {
        try {
            return resultSet.getShort(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public int getIntAt(int index) {
        try {
            return resultSet.getInt(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public long getLongAt(int index) {
        try {
            return resultSet.getLong(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public float getFloatAt(int index) {
        try {
            return resultSet.getFloat(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public double getDoubleAt(int index) {
        try {
            return resultSet.getDouble(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    @Override
    public String getStringAt(int index) {
        try {
            return resultSet.getString(index);
        } catch (SQLException e) {
            throw new FieldAccessException(index, schema, e);
        }
    }

    static class ColumnGetter implements FieldGetter {

        final int column;

        ColumnGetter(int column) {
            this.column = column;
        }

        @Override
        public Object get(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getObject(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public boolean has(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                wrapper.resultSet.getObject(column);
                return !((ResultSetWrapper) data).resultSet.wasNull();
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public boolean getBoolean(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getBoolean(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public byte getByte(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getByte(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public char getChar(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return (char) wrapper.resultSet.getInt(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public short getShort(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getShort(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public int getInt(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getInt(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public long getLong(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getLong(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public float getFloat(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getFloat(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

        @Override
        public double getDouble(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getDouble(column);
            } catch (SQLException e) {
                throw DataException.of(e);
            }
        }

        @Override
        public String getString(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getString(column);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }

    }

    static class TypeOverrideGetter extends ColumnGetter {

        private final Class<?> type;

        TypeOverrideGetter(int index,
                           Class<?> type) {
            super(index);
            this.type = type;
        }

        @Override
        public Object get(DidoData data) {
            ResultSetWrapper wrapper = ((ResultSetWrapper) data);
            try {
                return wrapper.resultSet.getObject(column, type);
            } catch (SQLException e) {
                throw new FieldAccessException(column, wrapper.schema, e);
            }
        }
    }

    static class TypeOverrides {

        final Map<String, SchemaField> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        TypeOverrides(DataSchema schema) {
            for (SchemaField field : schema.getSchemaFields()) {
                types.put(field.getName(), field);
            }
        }

        SchemaField getOverride(String label) {
            return types.get(label);
        }
    }

    static class Schema extends AbstractDataSchema implements ReadSchema {

        private final Map<String, SchemaField> nameToSchemaField = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        private final SchemaField[] indexToSchemaField;

        private final FieldGetter[] getters;

        Schema(ResultSetMetaData metaData,
               DataSchema overrideSchema,
               ClassLoader classLoader) throws SQLException, ClassNotFoundException {

            int columnCount = metaData.getColumnCount();

            this.indexToSchemaField = new SchemaField[columnCount];
            this.getters = new FieldGetter[columnCount];

            TypeOverrides typeOverrides = new TypeOverrides(overrideSchema);
            SchemaUtils schemaUtils = new SchemaUtils(classLoader);

            for (int index = 0; index < columnCount; ++index) {
                int column = index + 1;

                String name = metaData.getColumnLabel(column);
                Class<?> type = schemaUtils.getColumnType(column, metaData);

                SchemaField override = typeOverrides.getOverride(name);

                if (override == null) {
                    getters[index] = new ColumnGetter(column);
                } else {
                    name = override.getName();
                    if (override.getType() != type) {
                        type = override.getType();
                        getters[index] = new TypeOverrideGetter(column, type);
                    }
                }

                SchemaField schemaField = SchemaField.of(column, name, type);
                nameToSchemaField.put(name, schemaField);
                indexToSchemaField[index] = schemaField;
            }
        }

        @Override
        public boolean hasNamed(String name) {
            return nameToSchemaField.containsKey(name);
        }

        @Override
        public SchemaField getSchemaFieldAt(int index) {
            return index > 0 && index <= indexToSchemaField.length ?
                    indexToSchemaField[index - 1] : null;
        }

        @Override
        public SchemaField getSchemaFieldNamed(String name) {
            return nameToSchemaField.get(name);
        }

        @Override
        public boolean hasIndex(int index) {
            return index > 0 && index <= indexToSchemaField.length;
        }

        @Override
        public int firstIndex() {
            return 1;
        }

        @Override
        public int nextIndex(int index) {
            return index == indexToSchemaField.length ? 0 : index + 1;
        }

        @Override
        public int lastIndex() {
            return indexToSchemaField.length;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                return getters[index - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, this);
            }
            return getters[index - 1];
        }
    }

}
