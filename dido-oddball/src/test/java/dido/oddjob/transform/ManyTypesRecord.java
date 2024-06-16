package dido.oddjob.transform;

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
                .addField("AnIntString", String.class)
                .addField("ADoubleString", String.class)
                .addField("ABooleanString", String.class)
                .build();

        return ArrayData.valuesFor(schema).of("65", "456.57", "true");
    }
}
