package dido.operators.transform;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

public class ManyTypesRecord implements ValueFactory<DidoData> {

    @Override
    public DidoData toValue() throws ArooaConversionException {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("AnIntString", String.class)
                .addNamed("ADoubleString", String.class)
                .addNamed("ABooleanString", String.class)
                .build();

        return ArrayData.valuesForSchema(schema).of("65", "456.57", "true");
    }
}
