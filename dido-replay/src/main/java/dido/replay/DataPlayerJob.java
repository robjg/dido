package dido.replay;

import dido.data.GenericData;
import dido.how.CloseableSupplier;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DataPlayerJob implements Runnable, AutoCloseable {

    private volatile String name;

    private volatile Path dir;

    private volatile String filesPrefix;

    private volatile InputStream dataIn;

    private volatile InputStream schemaIn;

    private volatile InputStream timeIn;

    private volatile Consumer<? super GenericData<String>> to;

    private final AtomicInteger count = new AtomicInteger();

    private final AtomicReference<Thread> currentThread = new AtomicReference<>();

    private volatile Instant lastTime;

    @Override
    public void close() {

        Thread thread = currentThread.get();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {

        count.set(0);

        Consumer<? super GenericData<String>> to = Objects.requireNonNull(this.to, "No to");

        try (CloseableSupplier<DataPlayer.TimedData> supplier =
                     DataPlayer.withSettings()
                             .dataIn(dataIn)
                             .schemaIn(schemaIn)
                             .timeIn(timeIn)
                             .dir(dir)
                             .filesPrefix(filesPrefix)
                             .make()) {

            lastTime = Instant.now();

            currentThread.set(Thread.currentThread());

            while (!Thread.currentThread().isInterrupted()) {

                DataPlayer.TimedData timedData = supplier.get();
                if (timedData == null) {
                    break;
                }

                long wait = ChronoUnit.MILLIS.between(lastTime, timedData.getTimestamp());
                lastTime = timedData.getTimestamp();

                if (wait > 0) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                to.accept(timedData.getData());

                count.incrementAndGet();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed during Playback", e);
        } finally {
            currentThread.set(null);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public String getFilesPrefix() {
        return filesPrefix;
    }

    public void setFilesPrefix(String filesPrefix) {
        this.filesPrefix = filesPrefix;
    }

    public InputStream getDataIn() {
        return dataIn;
    }

    public void setDataIn(InputStream dataIn) {
        this.dataIn = dataIn;
    }

    public InputStream getSchemaIn() {
        return schemaIn;
    }

    public void setSchemaIn(InputStream schemaIn) {
        this.schemaIn = schemaIn;
    }

    public InputStream getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(InputStream timeIn) {
        this.timeIn = timeIn;
    }

    public Consumer<? super GenericData<String>> getTo() {
        return to;
    }

    public void setTo(Consumer<? super GenericData<String>> to) {
        this.to = to;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public Instant getLastTime() {
        return lastTime;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
