package dido.replay;

import dido.data.DidoData;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @oddjob.description Plays back Data. Data is expected to be from three inputs for
 * data, schema and time such as those recorded with {@link DataRecorderService}.
 *
 * @oddjob.example Plays back data.
 * {@oddjob.xml.resource dido/replay/RecordPlayExample.xml}
 */
public class DataPlayerJob implements Runnable, AutoCloseable {

    /**
     * @oddjob.description The name of the component.
     * @oddjob.required No.
     */
    private volatile String name;

    /**
     * @oddjob.description Directory where the files are to be found.
     * @oddjob.required No.
     */
    private volatile Path dir;

    /**
     * @oddjob.description Optional file name prefix.
     * @oddjob.required No.
     */
    private volatile String filesPrefix;

    /**
     * @oddjob.description Override where the data will be sourced from.
     * @oddjob.required No.
     */
    private volatile InputStream dataIn;

    /**
     * @oddjob.description Override where the schema will be sourced from.
     * @oddjob.required No.
     */
    private volatile InputStream schemaIn;

    /**
     * @oddjob.description Override where the time will be sourced from.
     * @oddjob.required No.
     */
    private volatile InputStream timeIn;

    /**
     * @oddjob.description If specified the player will skip forward to
     * this time or after.
     * @oddjob.required No.
     */
    private volatile Instant fromTime;

    /**
     * @oddjob.description If specified the player will stop replaying
     * after this time.
     * @oddjob.required No.
     */
    private volatile Instant toTime;

    /**
     * @oddjob.description Allows time to be speeded up.
     * @oddjob.required No, defaults to 1.0.
     */
    private volatile double playBackSpeed;

    /**
     * @oddjob.description Where the data will be sent to.
     * @oddjob.required No. Automatically set in BeanBus.
     */
    private volatile Consumer<? super DidoData> to;

    /**
     * @oddjob.description The number of data items played.
     * @oddjob.required Read Only.
     */
    private final AtomicInteger count = new AtomicInteger();

    private final AtomicReference<Thread> currentThread = new AtomicReference<>();

    /**
     * @oddjob.description The timestamp of the last data item played.
     * @oddjob.required Read Only.
     */
    private volatile Instant lastTime;

    /**
     * @oddjob.description The number of milliseconds until the next item is played.
     * @oddjob.required Read Only.
     */
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

        try (DataPlayer player =
                     DataPlayer.withSettings()
                             .dataIn(dataIn)
                             .schemaIn(schemaIn)
                             .timeIn(timeIn)
                             .dir(dir)
                             .filesPrefix(filesPrefix)
                             .make()) {

            lastTime = Instant.now();

            currentThread.set(Thread.currentThread());

            for (DataPlayer.TimedData timedData : player) {

                if (Thread.currentThread().isInterrupted()) {
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
                if (playBackSpeed > 0.0) {
                    wait = (long) (wait / playBackSpeed);
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

    public double getPlayBackSpeed() {
        return playBackSpeed;
    }

    public void setPlayBackSpeed(double playBackSpeed) {
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
