package dido.poi;

import dido.data.ArrayData;
import dido.how.DataOut;
import dido.poi.columns.Columns;
import dido.test.OurDirs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

class DataOutPoiTest {

    File workDir;

    private static final Logger logger = LoggerFactory.getLogger(DataOutPoiTest.class);

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws IOException {

        logger.info("----------------------------    {}   -------------------------", testInfo.getDisplayName());

        workDir = OurDirs.workPathDir(DataOutPoiTest.class).toFile();
    }

    @Test
    void writeWithCells() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (DataOut dataOut = DataOutPoi.with()
                .columns(List.of(
                        Columns.text()
                                .name("Fruit")
                                .make(),
                        Columns.numeric()
                                .type(int.class)
                                .name("Qty")
                                .make(),
                        Columns.numeric()
                                .type(double.class)
                                .name("Price")
                                .make()
                )).toOutputStream(out)) {

            dataOut.accept(ArrayData.of("Apple", 5, 20));

        }
    }

}