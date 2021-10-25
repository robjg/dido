package dido.oddjob.transpose;

public interface SchemaSetter<F> {

    void setFieldAt(int index, F field, Class<?> fieldType);


}
