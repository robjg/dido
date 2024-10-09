package dido.oddjob.types;

import dido.data.*;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;

import java.util.List;

/**
 * Create an item of Dido Data from values and possibly a schema. Used for testing.
 */
public class DidoDataType implements ValueFactory<DidoData>, ArooaSessionAware {

    private DataSchema schema;

    private List<Object> values;

    private ArooaSession session;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public DidoData toValue() throws ArooaConversionException {

        if (schema == null) {
            return ArrayData.of(values.toArray());
        }
        else {
            ArooaConverter converter = session.getTools().getArooaConverter();
            DataFactory<ArrayData> dataFactory = ArrayData.factoryForSchema(schema);
            WritableData writableData = dataFactory.getWritableData();
            for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                Object value;
                if (i > values.size()) {
                    value = null;
                }
                else {
                    value = values.get(i - 1);
                }

                Object v = converter.convert(value, schema.getTypeAt(i));
                writableData.setAt(i, v);
            }
            return dataFactory.toData();
        }
    }

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
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
        return "DidoData from " + values;
    }
}
