package dido.sql;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.how.DataException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetWrapper extends AbstractData implements NamedData {

    private final ResultSet resultSet;

    private final Schema schema;

    private ResultSetWrapper(ResultSet resultSet, Schema schema) {
        this.resultSet = resultSet;
        this.schema = schema;
    }

    public static NamedData from(ResultSet resultSet, DataSchema schema) {
        return new ResultSetWrapper(resultSet,
                schema instanceof DataSchemaImpl ? new Schema((DataSchemaImpl) schema) :
                new Schema(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex()));
    }

    @Override
    public ReadableSchema getSchema() {
        return schema;
    }

    @Override
    public Object get(String field) {
        try {
            return resultSet.getObject(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public <T> T getAs(String field, Class<T> type) {
        try {
            return resultSet.getObject(field, type);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean has(String field) {
        try {
            resultSet.getObject(field);
            return !resultSet.wasNull();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean getBoolean(String field) {
        try {
            return resultSet.getBoolean(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public byte getByte(String field) {
        try {
            return resultSet.getByte(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public char getChar(String field) {
        try {
            return (char) resultSet.getInt(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public short getShort(String field) {
        try {
            return resultSet.getShort(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public int getInt(String field) {
        try {
            return resultSet.getInt(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public long getLong(String field) {
        try {
            return resultSet.getLong(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public float getFloat(String field) {
        try {
            return resultSet.getFloat(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public double getDouble(String field) {
        try {
            return resultSet.getDouble(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public String getString(String field) {
        try {
            return resultSet.getString(field);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public Object getAt(int index) {
        try {
            return resultSet.getObject(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public <T> T getAtAs(int index, Class<T> type) {
        try {
            return resultSet.getObject(index, type);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean hasIndex(int index) {
        try {
            resultSet.getObject(index);
            return !resultSet.wasNull();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean getBooleanAt(int index) {
        try {
            return resultSet.getBoolean(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public byte getByteAt(int index) {
        try {
            return resultSet.getByte(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public char getCharAt(int index) {
        try {
            return (char) resultSet.getInt(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public short getShortAt(int index) {
        try {
            return resultSet.getShort(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public int getIntAt(int index) {
        try {
            return resultSet.getInt(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public long getLongAt(int index) {
        try {
            return resultSet.getLong(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public float getFloatAt(int index) {
        try {
            return resultSet.getFloat(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public double getDoubleAt(int index) {
        try {
            return resultSet.getDouble(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public String getStringAt(int index) {
        try {
            return resultSet.getString(index);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    static class IndexGetter implements Getter {

        private final int index;


        IndexGetter(int index) {
            this.index = index;
        }

        @Override
        public Object get(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getObject(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public <T> T getAs(Class<T> type, DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getObject(index, type);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public boolean has(DidoData data) {
            try {
                ((ResultSetWrapper) data).resultSet.getObject(index);
                return !((ResultSetWrapper) data).resultSet.wasNull();
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public boolean getBoolean(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getBoolean(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public byte getByte(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getByte(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public char getChar(DidoData data) {
            try {
                return (char) ((ResultSetWrapper) data).resultSet.getInt(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public short getShort(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getShort(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public int getInt(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getInt(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public long getLong(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getLong(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public float getFloat(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getFloat(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public double getDouble(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getDouble(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        @Override
        public String getString(DidoData data) {
            try {
                return ((ResultSetWrapper) data).resultSet.getString(index);
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

    }

    static class Schema extends DataSchemaImpl implements ReadableSchema {

        Schema(DataSchemaImpl schema) {
            super(schema);
        }

        Schema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
            super(schemaFields, firstIndex, lastIndex);
        }

        @Override
        public Getter getDataGetterAt(int index) {
            if (!hasIndex(index)) {
                throw new NoSuchFieldException(index, this);
            }
            return new IndexGetter(index);
        }
    }

}
