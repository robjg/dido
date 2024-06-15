package dido.oddjob.bean;

import dido.data.AbstractGenericData;
import dido.data.GenericData;
import dido.data.GenericDataSchema;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanArooaWithSchema<T> implements Function<T, GenericData<String>> {

    private final GenericDataSchema<String> schema;

    private final PropertyAccessor propertyAccessor;

    public FromBeanArooaWithSchema(GenericDataSchema<String> schema, PropertyAccessor propertyAccessor) {
        this.schema = schema;
        this.propertyAccessor = propertyAccessor;
    }

    @Override
    public GenericData<String> apply(T t) {
        return new AbstractGenericData<>() {
            @Override
            public GenericDataSchema<String> getSchema() {
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
