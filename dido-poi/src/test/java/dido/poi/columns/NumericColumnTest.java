package dido.poi.columns;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.DataInPoi;
import dido.poi.DataOutPoi;
import dido.test.OurDirs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class NumericColumnTest {

    private static final Logger logger = LoggerFactory.getLogger(NumericColumnTest.class);

    Path workDir;


    DataSchema schema = DataSchema.builder()
            .add(byte.class)
            .add(short.class)
            .add(int.class)
            .add(long.class)
            .add(float.class)
            .add(double.class)
            .build();

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws IOException {

        logger.info("----------------------------    {}   -------------------------", testInfo.getDisplayName());

        workDir = OurDirs.workPathDir(NumericColumnTest.class);
    }


    @Test
    void readAllTypes() {

        List<DidoData> results;

        try (DataIn in = DataInPoi
                .with()
                .firstRow(2)
                .firstColumn(2)
                .schema(schema)
                .columns(List.of(
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make()
                ))
                .fromInputStream(getClass().getResourceAsStream("/excel/Numbers.xlsx"))) {

            results = in.stream()
                    .collect(Collectors.toList());
        }

        assertThat(results.size(), is(2));

        DidoData result1 = results.get(0);

        assertThat(result1.getSchema(), is(schema));

        assertThat(result1.getByteAt(1), is((byte) 42));
        assertThat(result1.getShortAt(2), is((short) 42));
        assertThat(result1.getIntAt(3), is(42));
        assertThat(result1.getLongAt(1), is(42L));
        assertThat(result1.getFloatAt(1), is(42.0f));
        assertThat(result1.getDoubleAt(1), is(42.0));
    }

    @Test
    void writeAllTypes() {

        List<DidoData> data = DidoData.valuesWithSchema(schema)
                .many()
                .of((byte) 42, (short) 42, 42, 42L, 42.0F, 42.0)
                .of((byte) 42, (short) 42, 42, 42L, 42.24F, 42.24)
                .toList();

        Path path = workDir.resolve("NumbersOut.xlsx");

        try (DataOut out = DataOutPoi
                .with()
                .firstRow(2)
                .firstColumn(2)
                .columns(List.of(
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make(),
                        Columns.numeric()
                                .make()
                ))
                .toPath(path)) {

            data.forEach(out);
        }

        // Then

        List<DidoData> expected = ArrayData.valuesWithSchema(schema)
                .many()
                .of((byte) 42, (short) 42, 42, 42L, 42.0f, 42.0)
                .of((byte) 42, (short) 42, 42, 42L, 42.24f, 42.24)
                .toList();

        try (DataIn in = DataInPoi.with()
                .firstRow(2)
                .firstColumn(2)
                .schema(schema)
                .fromPath(path)) {

            List<DidoData> results = in.stream()
                    .collect(Collectors.toList());

            assertThat(results, is(expected));
        }
    }

}