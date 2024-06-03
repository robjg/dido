package dido.sql;

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


    public DataInHow<String, Connection> toIn()  {
        return SqlDataInHow.fromSql(sql)
                .classLoader(classLoader)
                .batchSize(batchSize)
                .make();
    }

    public DataOutHow<String, Connection> toOut() {
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

    @Override
    public String toString() {
        return "SqlDido{" +
                "sql='" + sql + '\'' +
                '}';
    }
}
