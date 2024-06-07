package dido.replay;

import dido.data.DidoData;
import dido.data.GenericData;
import dido.how.CloseableConsumer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DataRecorderService implements Consumer<DidoData> {

    private volatile String name;

    private volatile Path dir;

    private volatile String filesPrefix;

    private volatile OutputStream dataOut;

    private volatile OutputStream schemaOut;

    private volatile OutputStream timeOut;

    private volatile Clock clock;

    private final AtomicInteger count = new AtomicInteger();

    private CloseableConsumer<? super DidoData> recorder;

    private Consumer<? super GenericData<String>> to;

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

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
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

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }

}
