package dido.replay;

import dido.data.DidoData;
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

    private volatile Instant fromTime;

    private volatile Instant toTime;

    private volatile int playBackSpeed;

    private volatile Consumer<? super DidoData> to;

    private final AtomicInteger count = new AtomicInteger();

    private final AtomicReference<Thread> currentThread = new AtomicReference<>();

    private volatile Instant lastTime;

    private volatile long wait;

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

        Consumer<? super DidoData> to = Objects.requireNonNull(this.to, "No to");

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

                Instant timestamp = timedData.getTimestamp();
                if (fromTime != null && timestamp.isBefore(fromTime)) {
                    continue;
                }
                if (toTime != null && timestamp.isAfter(toTime)) {
                    break;
                }
                wait = ChronoUnit.MILLIS.between(lastTime, timestamp);
                if (playBackSpeed != 0) {
                    wait = wait / playBackSpeed;
                }
                if (wait > 0) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                lastTime = timedData.getTimestamp();
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

    public Instant getFromTime() {
        return fromTime;
    }

    public void setFromTime(Instant fromTime) {
        this.fromTime = fromTime;
    }

    public Instant getToTime() {
        return toTime;
    }

    public void setToTime(Instant toTime) {
        this.toTime = toTime;
    }

    public int getPlayBackSpeed() {
        return playBackSpeed;
    }

    public void setPlayBackSpeed(int playBackSpeed) {
        this.playBackSpeed = playBackSpeed;
    }

    public Consumer<? super DidoData> getTo() {
        return to;
    }

    public void setTo(Consumer<? super DidoData> to) {
        this.to = to;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public Instant getLastTime() {
        return lastTime;
    }

    public long getWait() {
        return wait;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
