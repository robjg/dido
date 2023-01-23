package dido.replay;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ReplayFileHelper {

    public static final String DATA_FILE_NAME = "data.json";

    public static final String SCHEMA_FILE_NAME = "schema.json";

    public static final String TIME_FILE_NAME = "time.txt";

    private final Path dataFile;

    private final Path schemaFile;

    private final Path timeFile;

    private ReplayFileHelper(Settings settings) {

        Path dir = Objects.requireNonNullElse(settings.dir, Paths.get("."));

        String fileNamePrefix = Objects.requireNonNullElse(
                settings.filesPrefix, "");

        this.dataFile = dir.resolve( fileNamePrefix + DATA_FILE_NAME);
        this.schemaFile = dir.resolve(fileNamePrefix + SCHEMA_FILE_NAME);
        this.timeFile = dir.resolve(fileNamePrefix + TIME_FILE_NAME);

    }


    public static class Settings {

        private Path dir;

        private String filesPrefix;

        public Settings dir(Path dir) {
            this.dir = dir;
            return this;
        }

        public Settings filesPrefix(String filesPrefix) {
            this.filesPrefix = filesPrefix;
            return this;
        }

        public ReplayFileHelper make() {
            return new ReplayFileHelper(this);
        }
    }

    public static Settings withSettings() {
        return new Settings();
    }

    public Path getDataFile() {
        return dataFile;
    }

    public Path getSchemaFile() {
        return schemaFile;
    }

    public Path getTimeFile() {
        return timeFile;
    }
}
