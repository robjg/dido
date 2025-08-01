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
    void testNestedExampleInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataFromNestedBeanExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> results = lookup.lookup("capture.beans", List.class);

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("orderId", String.class)
                .addRepeatingNamed("orderLines", nestedSchema)
                .build();

        IndexedData expectedData = ArrayData.withSchema(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.withSchema(nestedSchema)
                                        .of("Apple", 5),
                                ArrayData.withSchema(nestedSchema)
                                        .of("Pear", 4)));

        assertThat(results.get(0), is(expectedData));
    }
}
