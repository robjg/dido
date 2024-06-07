package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.how.DataException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetWrapper implements DidoData {

    private final ResultSet resultSet;

    private final DataSchema<String> schema;

    private ResultSetWrapper(ResultSet resultSet, DataSchema<String> schema) {
        this.resultSet = resultSet;
        this.schema = schema;
    }

    public static DidoData from(ResultSet resultSet, DataSchema<String> schema) {
        return new ResultSetWrapper(resultSet, schema);
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
    public boolean hasField(String field) {
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
    public DataSchema<String> getSchema() {
        return schema;
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

    @Override
    public int hashCode() {
        return IndexedData.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }
}
