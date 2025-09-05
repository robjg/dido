package dido.poi.layouts;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.data.schema.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import dido.test.OurDirs;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextCellTest {

    @Test
    public void testWriteRead() throws Exception {

        TextCell test = new TextCell();
        test.setName("Fruit");
        test.setIndex(2);

        DataRows rows = new DataRows();
        rows.setOf(0, test);
        rows.setWithHeader(true);

        PoiWorkbook workbook = new PoiWorkbook();

        DataOut out = rows.outTo(workbook);

        out.accept(ArrayData.of(null, "Apple"));
        out.accept(ArrayData.of(null, "Orange"));

        out.close();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamedAt(2, "Fruit", String.class)
                .build();

        try (DataIn reader = rows.inFrom(workbook)) {

            List<DidoData> results = reader.stream()
                    .collect(Collectors.toList());


            assertThat(results, contains(
                    MapData.builderForSchema(expectedSchema)
                            .with("Fruit", "Apple")
                            .build(),
                    MapData.builderForSchema(expectedSchema)
                            .with("Fruit", "Orange").build()
            ));
        }
    }

    @Disabled
    @Test
    public void testWriteAndReadTextCellOfNamedValues() throws ArooaPropertyException, ArooaConversionException, IOException {

        if (true) {
            return;
        }

        File file = new File(getClass().getResource(
                "TextCellOfNamedValues.xml").getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", OurDirs.workPathDir(TextCellTest.class).toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);
        oddjob.setProperties(properties);

        oddjob.run();

        assertEquals(ParentState.COMPLETE,
                oddjob.lastStateEvent().getState());

        OddjobLookup lookup = new OddjobLookup(oddjob);

        String[] expected = lookup.lookup("text-read.data.input", String[].class);

        String[] result = lookup.lookup("text-write.data.output", String[].class);

        assertEquals(expected[0], result[0]);
        assertEquals(expected[1], result[1]);
        assertEquals(expected[2], result[2]);

        assertEquals(expected.length, result.length);

        oddjob.destroy();
    }

}
