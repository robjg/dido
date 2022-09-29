package dido.oddjob.bean;

import dido.data.*;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FromBeanTransformerTest {

    @Test
    void testInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataFromBeanExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<GenericData<String>> results = lookup.lookup("capture.beans", List.class);

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("orderId", String.class)
                .addRepeatingField("orderLines", nestedSchema)
                .build();

        IndexedData<String> expectedData = ArrayData.valuesFor(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 5),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 4)));

        assertThat(results.get(0), is(expectedData));
    }
}
