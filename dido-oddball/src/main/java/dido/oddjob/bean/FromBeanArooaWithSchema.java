package dido.oddjob.bean;

import dido.data.AbstractNamedData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.NamedData;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanArooaWithSchema<T> implements Function<T, NamedData> {

    private final DataSchema schema;

    private final PropertyAccessor propertyAccessor;

    public FromBeanArooaWithSchema(DataSchema schema, PropertyAccessor propertyAccessor) {
        this.schema = schema;
        this.propertyAccessor = propertyAccessor;
    }

    @Override
    public NamedData apply(T t) {
        return new AbstractNamedData() {
            @Override
            public DataSchema getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return get(schema.getFieldNameAt(index));
            }

            @Override
            public Object getNamed(String field) {
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
