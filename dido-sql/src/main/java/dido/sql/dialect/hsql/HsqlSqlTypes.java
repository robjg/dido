package dido.sql.dialect.hsql;

import dido.sql.dialect.std.StdSqlTypes;

import java.sql.Types;
import java.util.HashMap;

public class HsqlSqlTypes extends StdSqlTypes {

    private final static HashMap<Integer, Class<?>> overrideTypes = new HashMap<>();

    static {

        overrideTypes.put(Types.BIT, byte[].class);
        overrideTypes.put(Types.TINYINT, Integer.class);
        overrideTypes.put(Types.SMALLINT, Integer.class);
        overrideTypes.put(Types.INTEGER, Integer.class);
        overrideTypes.put(Types.BIGINT, Long.class);
        overrideTypes.put(Types.FLOAT, Double.class);
        overrideTypes.put(Types.REAL, Double.class);
        overrideTypes.put(Types.DOUBLE, Double.class);
        overrideTypes.put(Types.BOOLEAN, Boolean.class);
    }

    @Override
    public Class<?> getJavaType(int sqlType) {
        Class<?> javaType = overrideTypes.get(sqlType);
        if (javaType == null) {
            return super.getJavaType(sqlType);
        }
        else {
            return javaType;
        }
    }
}
