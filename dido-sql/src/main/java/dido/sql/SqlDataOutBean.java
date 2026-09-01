package dido.sql;

import dido.how.DataOutHow;

import java.beans.ExceptionListener;
import java.sql.Connection;

/**
 * @oddjob.description Write Data out with SQL.
 *
 * @oddjob.example Insert and Extract data.
 * <p>
 * {@oddjob.xml.resource dido/sql/SimpleSqlExample.xml}
 *
 *
 */
public class SqlDataOutBean extends SqlBeanBase {

    /**
     * @oddjob.description The statement that will insert the data.
     * @oddjob.required Yes.
     */
    private String sql;

    /**
     * @oddjob.description The table name. If specified the insert will be
     * automatically generated.
     * @oddjob.required No.
     */
    private String table;

    /**
     * @oddjob.description An Exception Listener. If specified it will be call
     * if the dml fails to execute. It won't be called if it fails to parse. That
     * will still result in an Exception being passed out to the framework.
     * @oddjob.required No.
     */
    private ExceptionListener exceptionListener;

    public DataOutHow<Connection> toOut() {
        return DataOutSql.with()
                .sql(sql)
                .table(table)
                .exceptionListener(exceptionListener)
                .classLoader(getClassLoader())
                .batchSize(getBatchSize())
                .make();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    @Override
    public String toString() {
        return "SqlDataOutBean{" +
                "sql='" + sql + '\'' +
                '}';
    }
}
