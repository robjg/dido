package dido.sql;

import dido.data.DataSchema;
import dido.how.DataInHow;

import java.sql.Connection;

/**
 * @oddjob.description Read Data In with SQL.
 *
 * @oddjob.example Insert and Extract data.
 * <p>
 * {@oddjob.xml.resource dido/sql/SimpleSqlExample.xml}
 *
 *
 */
public class SqlDataInBean extends SqlBeanBase {

    /**
     * @oddjob.description The SQL that will extract or insert the data.
     * @oddjob.required Yes.
     */
    private String sql;

    /**
     * @oddjob.description An override schema that supplies a desired type to the underlying
     * {@link java.sql.ResultSet#getObject(int, Class)} method. Weather this is honoured is
     * dependent on the JDBC implementation.
     * @oddjob.required No.
     */
    private DataSchema schema;

    public DataInHow<Connection> toIn()  {
        return DataInSql.with()
                .sql(sql)
                .classLoader(getClassLoader())
                .batchSize(getBatchSize())
                .schema(schema)
                .make();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "SqlDataInBean{" +
                "sql='" + sql + '\'' +
                '}';
    }
}
