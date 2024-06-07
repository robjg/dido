package dido.oddjob.types;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;

import java.util.List;

/**
 * Create an item of Generic Data from values and possibly a schema. Used for testing.
 */
public class GenericDataType implements ValueFactory<GenericData<String>>, ArooaSessionAware {

    private DataSchema<String> schema;

    private List<Object> values;

    private ArooaSession session;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public GenericData<String> toValue() throws ArooaConversionException {

        if (schema == null) {
            return ArrayData.of(values.toArray());
        }
        else {
            ArooaConverter converter = session.getTools().getArooaConverter();
            ArrayData.Builder builder = ArrayData.builderForSchema(schema);
            for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                Object value;
                if (i > values.size()) {
                    value = null;
                }
                else {
                    value = values.get(i - 1);
                }

                Object v = converter.convert(value, schema.getTypeAt(i));
                builder.setAt(i, v);
            }
            return builder.build();
        }
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "GenericData from " + values;
    }
}
