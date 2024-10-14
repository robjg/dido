package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataInHow;
import dido.how.DataOutHow;

import javax.inject.Inject;
import java.sql.Connection;

/**
 * @oddjob.description Export and Import with SQL.
 *
 * @oddjob.example Insert and Extract data.
 * <p>
 * {@oddjob.xml.resource dido/sql/SimpleSqlExample.xml}
 *
 *
 */
public class SqlDido {


    /**
     * @oddjob.description The SQL that will extract or insert the data.
     * @oddjob.required Yes.
     */
    private String sql;

    /**
     * @oddjob.description The classloader used to derive the schema from a query. Only required if the
     * table include custom data types.
     * @oddjob.required No.
     */
    private ClassLoader classLoader;

    /**
     * @oddjob.description Batch size. This is passed directly to JDBC.
     * @oddjob.required No.
     */
    private int batchSize;

    /**
     * @oddjob.description An override schema that supplies a desired type to the underlying
     * {@link java.sql.ResultSet#getObject(int, Class)} method. Weather this is honoured is
     * dependent on the JDBC implementation.
     * @oddjob.required No.
     */
    private DataSchema schema;

    public DataInHow<Connection, DidoData> toIn()  {
        return SqlDataInHow.fromSql(sql)
                .classLoader(classLoader)
                .batchSize(batchSize)
                .schema(schema)
                .make();
    }

    public DataOutHow<Connection> toOut() {
        return SqlDataOutHow.fromSql(sql)
                .classLoader(classLoader)
                .batchSize(batchSize)
                .make();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Inject
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "SqlDido{" +
                "sql='" + sql + '\'' +
                '}';
    }
}
