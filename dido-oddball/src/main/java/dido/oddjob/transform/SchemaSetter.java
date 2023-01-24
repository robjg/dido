package dido.oddjob.transform;

public interface SchemaSetter<F> {

    void setFieldAt(int index, F field, Class<?> fieldType);


}
