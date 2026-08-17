package dido.sql.dialect.std;

import dido.data.util.ClassUtils;
import dido.sql.dialect.SqlTypes;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 *  SQL Types to Java Types for most DBs.
 */
public class StdSqlTypes implements SqlTypes {

    private final static Map<Integer, Class<?>> byType = new HashMap<>();

    private final static Map<Type, Integer> byClass = new HashMap<>();

    static {

        byType.put(Types.BIT, boolean.class);
        byType.put(Types.TINYINT, byte.class);
        byType.put(Types.SMALLINT, short.class);
        byType.put(Types.INTEGER, int.class);
        byType.put(Types.BIGINT, long.class);
        byType.put(Types.FLOAT, double.class);
        byType.put(Types.REAL, float.class);
        byType.put(Types.DOUBLE, double.class);
        byType.put(Types.NUMERIC, BigDecimal.class);
        byType.put(Types.DECIMAL, BigDecimal.class);
        byType.put(Types.CHAR, String.class);
        byType.put(Types.VARCHAR, String.class);
        byType.put(Types.LONGVARCHAR, String.class);
        byType.put(Types.DATE, java.sql.Date.class);
        byType.put(Types.TIME, java.sql.Time.class);
        byType.put(Types.TIMESTAMP, java.sql.Timestamp.class);
        byType.put(Types.BINARY, byte[].class);
        byType.put(Types.VARBINARY, byte[].class);
        byType.put(Types.NULL, Void.class);
        byType.put(Types.OTHER, Object.class);
        byType.put(Types.JAVA_OBJECT, Object.class);
        byType.put(Types.DISTINCT, Object.class);
        byType.put(Types.STRUCT, Struct.class);
        byType.put(Types.ARRAY, java.sql.Array.class);
        byType.put(Types.BLOB, java.sql.Blob.class);
        byType.put(Types.CLOB, java.sql.Clob.class);
        byType.put(Types.REF, java.sql.Ref.class);
        byType.put(Types.DATALINK, Object.class);
        byType.put(Types.BOOLEAN, boolean.class);
        byType.put(Types.ROWID, long.class);
        byType.put(Types.NCHAR, String.class);
        byType.put(Types.NVARCHAR, String.class);
        byType.put(Types.LONGNVARCHAR, String.class);
        byType.put(Types.NCLOB, NClob.class);
        byType.put(Types.SQLXML, SQLXML.class);
        byType.put(Types.REF_CURSOR, Object.class);
        byType.put(Types.TIME_WITH_TIMEZONE, java.time.OffsetTime.class);
        byType.put(Types.TIMESTAMP_WITH_TIMEZONE, java.time.OffsetDateTime.class);

        byClass.put(String.class, Types.VARCHAR);
        byClass.put(Integer.class, Types.INTEGER);
        byClass.put(Long.class, Types.BIGINT);
        byClass.put(Float.class, Types.FLOAT);
        byClass.put(Double.class, Types.DOUBLE);
        byClass.put(BigDecimal.class, Types.DECIMAL);
        byClass.put(Boolean.class, Types.BOOLEAN);
        byClass.put(Instant.class, Types.TIMESTAMP_WITH_TIMEZONE);
        byClass.put(java.util.Date.class, Types.TIMESTAMP);
        byClass.put(java.sql.Date.class, Types.DATE);
        byClass.put(java.sql.Time.class, Types.TIME);
        byClass.put(java.sql.Timestamp.class, Types.TIMESTAMP);
    }

    @Override
    public Class<?> getJavaType(int sqlType) {
        return byType.getOrDefault(sqlType, Object.class);
    }

    @Override
    public int getSqlType(Type javaType) {

        if (javaType instanceof Class<?> cl) {
            javaType = ClassUtils.wrapperClassForPrimitive(cl);
        }

        return byClass.getOrDefault(javaType, Types.OTHER);
    }

    @Override
    public String getSqlTypeName(int sqlType) {
        JDBCType jdbcType = JDBCType.valueOf(sqlType);
        if (jdbcType == JDBCType.VARCHAR) {
            return "VARCHAR(128)";
        }
        else {
            return jdbcType.name();
        }
    }
}
