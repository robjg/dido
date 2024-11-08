package dido.replay;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.json.JsonStringToData;
import dido.json.SchemaAsJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;

public class DataPlayer implements Iterable<DataPlayer.TimedData>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(DataPlayer.class);

    private final Inputs inputs;

    private DataPlayer(Inputs inputs) throws Exception {

        this.inputs = inputs;

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

        public DataPlayer make() throws Exception {

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

    static class Inputs implements Closeable {

        private final InputStream dataIn;

        private final InputStream schemaIn;

        private final InputStream timeIn;

        public Inputs(InputStream dataIn, InputStream schemaIn, InputStream timeIn) {
            this.dataIn = Objects.requireNonNull(dataIn, "No Data Input");
            this.schemaIn = Objects.requireNonNull(schemaIn, "No Schema Input");
            this.timeIn = Objects.requireNonNull(timeIn, "No Time Input");
        }

        @Override
        public void close() throws IOException {
            try {
                this.dataIn.close();
            }
            finally {
                try {
                    this.schemaIn.close();
                }
                finally {
                    this.timeIn.close();
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        inputs.close();
    }

    @Override
    public Iterator<TimedData> iterator() {

        Iterator<String> dataSupplier = new BufferedReader(new InputStreamReader(inputs.dataIn))
                .lines().iterator();

        Iterator<DataSchema> schemaSupplier = SchemaAsJson.fromJsonLines(inputs.schemaIn).iterator();

        Iterator<Instant> timestampSupplier = new BufferedReader(new InputStreamReader(inputs.timeIn))
                .lines().map(Instant::parse)
                .iterator();


        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return dataSupplier.hasNext();
            }

            @Override
            public TimedData next() {
                DataSchema schema = schemaSupplier.next();

                String jsonString = dataSupplier.next();

                DidoData data = JsonStringToData.asWrapperWithSchema(schema).apply(jsonString);

                Instant timestamp = timestampSupplier.next();

                return new TimedData(data, timestamp);
            }
        };
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
