package dido.oddjob.stream;

import dido.data.GenericData;
import org.oddjob.beanbus.Destination;
import dido.pickles.CloseableSupplier;
import dido.pickles.StreamIn;
import org.oddjob.framework.adapt.Start;
import org.oddjob.framework.adapt.Stop;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class StreamInDriver<F> implements Runnable, Closeable {

    private String name;

    private StreamIn<F> streamIn;

    private InputStream input;

    private Consumer<? super GenericData<F>> to;

    private volatile int count;

    private volatile boolean stop;

    @Start
    @Override
    public void run() {

        stop = false;

        count = 0;
        try (CloseableSupplier<GenericData<F>> supplier =
                     Objects.requireNonNull(streamIn, "No In")
                .supplierFor(Objects.requireNonNull(input, "No Output Stream"))) {
            while (!stop) {
                GenericData<F> data = supplier.get();
                if (data == null) {
                    break;
                }

                ++count;

                if (to != null) {
                    to.accept(data);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Stop
    @Override
    public void close() throws IOException {

        stop = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StreamIn<F> getStreamIn() {
        return streamIn;
    }

    public void setStreamIn(StreamIn<F> streamIn) {
        this.streamIn = streamIn;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public Consumer<? super GenericData<F>> getTo() {
        return to;
    }

    @Destination
    public void setTo(Consumer<? super GenericData<F>> to) {
        this.to = to;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName());
    }
}
