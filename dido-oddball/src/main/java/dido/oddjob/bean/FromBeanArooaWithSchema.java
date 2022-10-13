package dido.oddjob.bean;

import dido.data.AbstractGenericData;
import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanArooaWithSchema<T> implements Function<T, GenericData<String>> {

    private final DataSchema<String> schema;

    private final PropertyAccessor propertyAccessor;

    public FromBeanArooaWithSchema(DataSchema<String> schema, PropertyAccessor propertyAccessor) {
        this.schema = schema;
        this.propertyAccessor = propertyAccessor;
    }

    @Override
    public GenericData<String> apply(T t) {
        return new AbstractGenericData<>() {
            @Override
            public DataSchema<String> getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return get(schema.getFieldAt(index));
            }

            @Override
            public Object get(String field) {
                return propertyAccessor.getProperty(t, field);
            }

            @Override
            public boolean hasIndex(int index) {
                return schema.getTypeAt(index) != null;
            }

            @Override
            public String toString() {
                return GenericData.toStringFieldsOnly(this);
            }
        };
    }
}
