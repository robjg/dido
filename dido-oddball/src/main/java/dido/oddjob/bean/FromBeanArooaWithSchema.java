package dido.oddjob.bean;

import dido.data.AbstractData;
import dido.data.DataSchema;
import dido.data.DidoData;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanArooaWithSchema<T> implements Function<T, DidoData> {

    private final DataSchema schema;

    private final PropertyAccessor propertyAccessor;

    public FromBeanArooaWithSchema(DataSchema schema, PropertyAccessor propertyAccessor) {
        this.schema = schema;
        this.propertyAccessor = propertyAccessor;
    }

    @Override
    public DidoData apply(T t) {
        return new AbstractData() {
            @Override
            public DataSchema getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return get(schema.getFieldNameAt(index));
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
                return DidoData.toStringFieldsOnly(this);
            }
        };
    }
}
