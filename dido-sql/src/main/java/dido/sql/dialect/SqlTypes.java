package dido.sql.dialect;

import java.lang.reflect.Type;

/**
 * Mapping between Java and SQL Types.
 */
public interface SqlTypes {

    Class<?> getJavaType(int sqlType);

    int getSqlType(Type javaType);

    String getSqlTypeName(int sqlType);
}
