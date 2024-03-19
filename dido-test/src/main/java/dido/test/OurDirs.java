package dido.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static dido.test.OurDirs.BuildType.*;

/**
 * Used to work out relative directories, when running tests individually
 * from an IDE or from Ant or from Maven.
 *
 * @author rob
 */
public class OurDirs {
    private static final Logger logger = LoggerFactory.getLogger(OurDirs.class);

    private static final OurDirs INSTANCE = new OurDirs();

    public enum BuildType {
        ANT("build"),
        MAVEN("target"),
        IDE("target");

        private final String buildDir;

        BuildType(String buildDir) {
            this.buildDir = buildDir;
        }

        public String getBuildDir() {
            return buildDir;
        }
    }

    private final File base;

    private final Path buildDirPath;

    private final Path workDirPath;

    private final BuildType buildType;

    public OurDirs() {

        String baseDir = System.getProperty("basedir");

        if (baseDir == null) {
            baseDir = ".";
            buildType = IDE;
        } else {
            if (System.getProperty("ant.file") == null) {
                buildType = MAVEN;
            } else {
                buildType = ANT;
            }
        }

        base = new File(baseDir);
        buildDirPath = base.toPath().resolve(buildType.buildDir);
        try {
            workDirPath = mkDirs(buildDirPath.resolve("work"), false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed creating work dir.", e);
        }

        logger.info("Base directory is " + base.getAbsolutePath() + " (" +
                buildType + " build)");

    }

    public File base() {
        try {
            return base.getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File relative(String name) {
        try {
            return new File(base, name).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path basePath() {
        return INSTANCE.base.toPath();
    }

    public static Path relativePath(String other) {
        return basePath().resolve(other);
    }

    public static Path buildDirPath() {
        return INSTANCE.buildDirPath;
    }

    public static Path workDirPath() {

        return INSTANCE.workDirPath;
    }

    public static BuildType buildType() {
        return INSTANCE.buildType;
    }

    public static Path workPathDir(Class<?> aClass) throws IOException {
        return mkDirs(workDirPath().resolve(aClass.getSimpleName()), true);
    }

    public static Path workPathDir(Class<?> aClass, boolean recreate) throws IOException {
        return mkDirs(workDirPath().resolve(aClass.getSimpleName()), recreate);
    }

    public static Path workPathDir(String dirName, boolean recreate) throws IOException {
        return mkDirs(workDirPath().resolve(dirName), recreate);
    }

    private static Path mkDirs(Path dir, boolean recreate) throws IOException {
        if (Files.exists(dir)) {
            if (recreate) {
                deleteDir(dir);
            } else {
                if (!Files.isDirectory(dir)) {
                    throw new IllegalArgumentException(dir + " is not a directory");
                }
                return dir;
            }
        }
        return Files.createDirectories(dir);
    }

    private static void deleteDir(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static Path classesDir(Class<?> forClass) throws URISyntaxException {

        Path classes = Paths.get(forClass.getResource(
                forClass.getSimpleName() + ".class").toURI())
                .getParent();

        String classPackage = forClass.getPackage().getName();

        String[] packages = classPackage.split("\\.");
        for (int i = 0; i < packages.length; ++i) {
            classes = classes.getParent();
        }

        return classes;
    }
}
