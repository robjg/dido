package dido.how.lines;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.schema.SchemaBuilder;
import dido.how.DataIn;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataLinesTest {

    @Test
    void linesInOutDefaultFieldName() {

        String string = "Apple" + System.lineSeparator() +
                "Orange" + System.lineSeparator() +
                "Pear" + System.lineSeparator();

        StringReader reader = new StringReader(string);

        StringBuilder builder = new StringBuilder();

        List<DidoData> lines;

        try (DataIn in = DataInLines.fromReader(reader)) {

            lines = in.stream().toList();
        }

        assertThat(lines.size(), is(3));

        DidoData data = lines.get(1);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Line", String.class)
                .build();

        assertThat(data.getSchema(), is(expectedSchema));

        assertThat(data.getStringNamed("Line"), is("Orange"));

        try (DataOut out = DataOutLines.toAppendable(builder)) {

            lines.forEach(out);
        }

        assertThat(builder.toString(), is(string));

    }

    @Test
    void linesWithSchemaAndFieldName() {

        String string = "Apple" + System.lineSeparator() +
                "Orange" + System.lineSeparator() +
                "Pear" + System.lineSeparator();

        StringReader reader = new StringReader(string);

        StringBuilder builder = new StringBuilder();

        try (DataIn in = DataInLines.with()
                .fieldName("Foo")
                .fromReader(reader);
             DataOut out = DataOutLines.with()
                     .fieldName("Foo")
                     .forSchema(in.getSchema())
                     .toAppendable(builder)) {

            in.stream().forEach(out);
        }

        assertThat(builder.toString(), is(string));
    }

    @Test
    void asFunc() {

        List<String> strings = List.of("Apple",
                "Orange", "Pear");

        List<String> results = strings.stream()
                .map(DataInLines.with()
                        .fieldName("Foo")
                        .mapFromString())
                .map(DataOutLines.with()
                        .fieldName("Foo")
                        .mapToString())
                .toList();

        assertThat(results, is(strings));
    }
}
