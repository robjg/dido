package dido.replay;

import dido.data.DidoData;
import dido.how.CloseableConsumer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.InstantSource;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @oddjob.description Records data it receives to files. This recorder writes
 * three files simultaneously; one of data, one of schemas, and one of time stamps.
 * The data and schema are written as JSON lines. The timestamps are lines
 * of text written using ISO-8601 format. The default names of the files are
 * {@code data.jsonl, schema.jsonl, time.txt}. These name can be prefixed with
 * an optional property {@code filesPrefix}.
 *
 * @oddjob.example Records data.
 * {@oddjob.xml.resource dido/replay/RecordPlayExample.xml}
 */
public class DataRecorderService implements Consumer<DidoData> {

    /**
     * @oddjob.description The name of the component.
     * @oddjob.required No.
     */
    private volatile String name;

    /**
     * @oddjob.description Directory where the files will be created.
     * @oddjob.required No.
     */
    private volatile Path dir;

    /**
     * @oddjob.description Optional file name prefix.
     * @oddjob.required No.
     */
    private volatile String filesPrefix;

    /**
     * @oddjob.description Override where the data will be written to.
     * @oddjob.required No.
     */
    private volatile OutputStream dataOut;

    /**
     * @oddjob.description Override where the schema will be written to.
     * @oddjob.required No.
     */
    private volatile OutputStream schemaOut;

    /**
     * @oddjob.description Override where the time will be written to.
     * @oddjob.required No.
     */
    private volatile OutputStream timeOut;

    /**
     * @oddjob.description The clock to use for the timestamp.
     * @oddjob.required No. Default to system time.
     */
    private volatile InstantSource clock;

    /**
     * @oddjob.description Count of items recorded.
     * @oddjob.required Read only.
     */
    private final AtomicInteger count = new AtomicInteger();

    private CloseableConsumer<? super DidoData> recorder;

    /**
     * @oddjob.description An onward consumer of the data.
     * @oddjob.required No.
     */
    private Consumer<? super DidoData> to;

    public void start() throws IOException {

        count.set(0);

        recorder = DataRecorder.withSettings()
                .dataOut(dataOut)
                .schemaOut(schemaOut)
                .timeOut(timeOut)
                .dir(dir)
                .filesPrefix(filesPrefix)
                .clock(clock)
                .make();
    }

    @Override
    public void accept(DidoData data) {

        recorder.accept(data);

        count.incrementAndGet();
    }

    public void stop() throws Exception {
        recorder.close();
        recorder = null;
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

    public OutputStream getDataOut() {
        return dataOut;
    }

    public void setDataOut(OutputStream dataOut) {
        this.dataOut = dataOut;
    }

    public OutputStream getSchemaOut() {
        return schemaOut;
    }

    public void setSchemaOut(OutputStream schemaOut) {
        this.schemaOut = schemaOut;
    }

    public OutputStream getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(OutputStream timeOut) {
        this.timeOut = timeOut;
    }

    public InstantSource getClock() {
        return clock;
    }

    public void setClock(InstantSource clock) {
        this.clock = clock;
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

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }

}
