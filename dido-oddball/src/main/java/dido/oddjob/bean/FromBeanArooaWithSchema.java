package dido.oddjob.bean;

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
        return new GenericData<String>() {
            @Override
            public DataSchema<String> getSchema() {
                return schema;
            }

            @Override
            public <T> T getObjectAt(int index, Class<T> type) {
                return getObject(schema.getFieldAt(index), type);
            }

            @Override
            public <T> T getObject(String field, Class<T> type) {
                return type.cast(propertyAccessor.getProperty(t, field));
            }

            @Override
            public boolean hasIndex(int index) {
                return schema.getTypeAt(index) != null;
            }
        };
    }
}
