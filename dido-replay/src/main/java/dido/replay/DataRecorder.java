package dido.replay;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.CloseableConsumer;
import dido.json.SchemaAsJson;
import dido.json.StreamOutJsonLines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class DataRecorder implements CloseableConsumer<DidoData> {

    private static final Logger logger = LoggerFactory.getLogger(DataRecorder.class);
    private final Clock clock;

    private final CloseableConsumer<? super DidoData> dataConsumer;

    private final CloseableConsumer<? super DataSchema> schemaConsumer;

    private final CloseableConsumer<? super Instant> timeConsumer;

    private DataRecorder(Outputs outputs, Clock clock) {

        this.clock = Objects.requireNonNullElse(clock, Clock.systemUTC());

        dataConsumer =  new StreamOutJsonLines().outTo(outputs.dataOut);

        schemaConsumer = SchemaAsJson.toJsonStream(outputs.schemaOut);

        timeConsumer = new CloseableConsumer<>() {
            @Override
            public void close() throws Exception {
                outputs.timeOut.close();
            }

            @Override
            public void accept(Instant instant) {
                try {
                    outputs.timeOut.write(instant.toString().getBytes(StandardCharsets.UTF_8));
                    outputs.timeOut.write('\n');
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static class Settings {

        private volatile Path dir;

        private volatile String filesPrefix;

        private volatile OutputStream dataOut;

        private volatile OutputStream schemaOut;

        private volatile OutputStream timeOut;

        private Clock clock;

        public Settings dir(Path dir) {
            this.dir = dir;
            return this;
        }

        public Settings filesPrefix(String filesPrefix) {
            this.filesPrefix = filesPrefix;
            return this;
        }

        public Settings dataOut(OutputStream dataOut) {
            this.dataOut = dataOut;
            return this;
        }

        public Settings schemaOut(OutputStream schemaOut) {
            this.schemaOut = schemaOut;
            return this;
        }

        public Settings timeOut(OutputStream timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Settings clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public CloseableConsumer<DidoData> make() throws IOException {

            ReplayFileHelper fileHelper = ReplayFileHelper.withSettings()
                    .dir(this.dir)
                    .filesPrefix(this.filesPrefix)
                    .make();

            if (dataOut == null) {
                logger.info("Writing data to {}", fileHelper.getDataFile());
                dataOut = Files.newOutputStream(fileHelper.getDataFile());
            }

            if (schemaOut == null) {
                logger.info("Writing schemas to {}", fileHelper.getSchemaFile());
                schemaOut = Files.newOutputStream(fileHelper.getSchemaFile());
            }

            if (timeOut == null) {
                logger.info("Writing time to {}", fileHelper.getTimeFile());
                timeOut = Files.newOutputStream(fileHelper.getTimeFile());
            }

            return new DataRecorder(new Outputs(dataOut, schemaOut, timeOut), clock);
        }

    }

    public static Settings withSettings() {

        return new Settings();
    }


    static class Outputs {

        private final OutputStream dataOut;

        private final OutputStream schemaOut;

        private final OutputStream timeOut;

        public Outputs(OutputStream dataOut, OutputStream schemaOut, OutputStream timeOut) {
            this.dataOut = Objects.requireNonNull(dataOut, "No Data Output");
            this.schemaOut = Objects.requireNonNull(schemaOut, "No Schema Output");
            this.timeOut = Objects.requireNonNull(timeOut, "No Time Output");
        }
    }

    @Override
    public void close() throws Exception {
        dataConsumer.close();
        schemaConsumer.close();
        timeConsumer.close();
    }

    @Override
    public void accept(DidoData data) {

        dataConsumer.accept(data);
        schemaConsumer.accept(data.getSchema());
        timeConsumer.accept(clock.instant());
    }
}
