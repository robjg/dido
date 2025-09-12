package dido.replay;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DataPlayerJobTest {

    @Test
    void whenFromToTimeSet() throws Exception {

        Path dir = new File(Objects.requireNonNull(getClass().getResource("/data/stuff-data.json")).getFile())
                .getParentFile().toPath();

        List<DidoData> results = new ArrayList<>();

        try (DataPlayerJob dataPlayerJob = new DataPlayerJob()) {

            dataPlayerJob.setDir(dir);
            dataPlayerJob.setFilesPrefix("stuff-");
            dataPlayerJob.setFromTime(Instant.parse("2023-01-23T07:07:52Z"));
            dataPlayerJob.setToTime(Instant.parse("2023-01-23T07:07:53Z"));
            dataPlayerJob.setTo(results::add);

            dataPlayerJob.run();
        }

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("type", String.class)
                .addNamed("quantity", int.class)
                .addNamed("price", double.class)
                .build();

        MatcherAssert.assertThat(results, Matchers.contains(
                MapData.withSchema(expectedSchema).of("Orange", 10, 31.6)));
    }
}