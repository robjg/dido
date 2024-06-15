package dido.replay;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.CloseableSupplier;
import dido.json.JsonStringToData;
import dido.json.SchemaAsJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public class DataPlayer implements CloseableSupplier<DataPlayer.TimedData> {

    private static final Logger logger = LoggerFactory.getLogger(DataPlayer.class);

    private final CloseableSupplier<String> dataSupplier;

    private final CloseableSupplier<DataSchema> schemaSupplier;

    private final CloseableSupplier<Instant> timestampSupplier;

    private DataPlayer(Inputs inputs) throws Exception {

        dataSupplier = new CloseableSupplier<>() {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputs.dataIn));

            @Override
            public void close() throws Exception {
                reader.close();
            }

            @Override
            public String get() {
                try {
                    return reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        schemaSupplier = SchemaAsJson.fromJsonStream(inputs.schemaIn);

        timestampSupplier = new CloseableSupplier<>() {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputs.timeIn));

            @Override
            public void close() throws Exception {
                inputs.dataIn.close();
            }

            @Override
            public Instant get() {
                try {
                    return Instant.parse(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static class Settings {

        private Path dir;

        private String filesPrefix;

        private InputStream dataIn;

        private InputStream schemaIn;

        private InputStream timeIn;

        public Settings dir(Path dir) {
            this.dir = dir;
            return this;
        }

        public Settings filesPrefix(String filesPrefix) {
            this.filesPrefix = filesPrefix;
            return this;
        }

        public Settings dataIn(InputStream dataIn) {
            this.dataIn = dataIn;
            return this;
        }

        public Settings schemaIn(InputStream schemaIn) {
            this.schemaIn = schemaIn;
            return this;
        }

        public Settings timeIn(InputStream timeIn) {
            this.timeIn = timeIn;
            return this;
        }

        public CloseableSupplier<TimedData> make() throws Exception {

            ReplayFileHelper fileHelper = ReplayFileHelper.withSettings()
                    .dir(this.dir)
                    .filesPrefix(this.filesPrefix)
                    .make();

            if (dataIn == null) {
                logger.info("Reading data from {}", fileHelper.getDataFile());
                dataIn = Files.newInputStream(fileHelper.getDataFile());
            }

            if (schemaIn == null) {
                logger.info("Reading schemas from {}", fileHelper.getSchemaFile());
                schemaIn = Files.newInputStream(fileHelper.getSchemaFile());
            }

            if (timeIn == null) {
                logger.info("Reading time from {}", fileHelper.getTimeFile());
                timeIn = Files.newInputStream(fileHelper.getTimeFile());
            }

            return new DataPlayer(new Inputs(dataIn, schemaIn, timeIn));
        }
    }

    public static Settings withSettings() {

        return new Settings();
    }

    static class Inputs {

        private final InputStream dataIn;

        private final InputStream schemaIn;

        private final InputStream timeIn;

        public Inputs(InputStream dataIn, InputStream schemaIn, InputStream timeIn) {
            this.dataIn = Objects.requireNonNull(dataIn, "No Data Input");
            this.schemaIn = Objects.requireNonNull(schemaIn, "No Schema Input");
            this.timeIn = Objects.requireNonNull(timeIn, "No Time Input");
        }
    }

    @Override
    public void close() throws Exception {
        dataSupplier.close();
        schemaSupplier.close();
        timestampSupplier.close();
    }

    @Override
    public TimedData get() {

        DataSchema schema = schemaSupplier.get();
        if (schema == null) {
            return null;
        }

        String jsonString = dataSupplier.get();

        DidoData data = JsonStringToData.asWrapperWithSchema(schema).apply(jsonString);

        Instant timestamp = timestampSupplier.get();

        return new TimedData(data, timestamp);
    }

    public static class TimedData {

        private final DidoData data;

        private final Instant timestamp;

        public TimedData(DidoData data, Instant timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }

        public DidoData getData() {
            return data;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "PlaybackPair{" +
                    "data=" + data +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
