package dido.sql;

import dido.data.AbstractData;
import dido.data.DataSchema;
import dido.data.NamedData;
import dido.how.DataException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetWrapper extends AbstractData implements NamedData {

    private final ResultSet resultSet;

    private final DataSchema schema;

    private ResultSetWrapper(ResultSet resultSet, DataSchema schema) {
        this.resultSet = resultSet;
        this.schema = schema;
    }

    public static NamedData from(ResultSet resultSet, DataSchema schema) {
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
    public DataSchema getSchema() {
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

}
