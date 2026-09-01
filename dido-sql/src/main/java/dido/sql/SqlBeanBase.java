package dido.sql;

import javax.inject.Inject;

/**
 * Common Base class for SQL in and out Beans.
 *
 */
public class SqlBeanBase {

    /**
     * @oddjob.description The SQL that will extract or insert the data.
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

}
