package dido.oddjob.transpose;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

public class ManyTypesRecord implements ValueFactory<GenericData<String>> {

    @Override
    public GenericData<String> toValue() throws ArooaConversionException {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("AnIntString", String.class)
                .addField("ADoubleString", String.class)
                .addField("ABooleanString", String.class)
                .build();

        return ArrayData.valuesFor(schema).of("65", "456.57", "true");
    }
}
