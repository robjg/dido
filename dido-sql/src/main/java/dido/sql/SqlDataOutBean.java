package dido.sql;

import dido.how.DataOutHow;

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

    public DataOutHow<Connection> toOut() {
        return DataOutSql.with()
                .sql(sql)
                .table(table)
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

    @Override
    public String toString() {
        return "SqlDataOutBean{" +
                "sql='" + sql + '\'' +
                '}';
    }
}
