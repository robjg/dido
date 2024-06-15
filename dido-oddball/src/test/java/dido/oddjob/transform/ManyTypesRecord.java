package dido.oddjob.transform;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.GenericDataSchema;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

public class ManyTypesRecord implements ValueFactory<DidoData> {

    @Override
    public DidoData toValue() throws ArooaConversionException {

        GenericDataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("AnIntString", String.class)
                .addField("ADoubleString", String.class)
                .addField("ABooleanString", String.class)
                .build();

        return ArrayData.valuesFor(schema).of("65", "456.57", "true");
    }
}
