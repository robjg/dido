package dido.sql;

import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.DataOutHow;

import javax.inject.Inject;
import java.sql.Connection;

public class SqlDido implements DataInHow<String, Connection>, DataOutHow<String, Connection> {

    private String sql;

    private ClassLoader classLoader;

    private int batchSize;

    @Override
    public Class<Connection> getInType() {
        return Connection.class;
    }

    @Override
    public DataIn<String> inFrom(Connection dataIn) throws Exception {
        return SqlDataInHow.fromSql(sql)
                .classLoader(classLoader)
                .batchSize(batchSize)
                .make()
                .inFrom(dataIn);
    }

    @Override
    public Class<Connection> getOutType() {
        return Connection.class;
    }

    @Override
    public DataOut<String> outTo(Connection connection) throws Exception {
        return SqlDataOutHow.fromSql(sql)
                .classLoader(classLoader)
                .batchSize(batchSize)
                .make()
                .outTo(connection);
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
