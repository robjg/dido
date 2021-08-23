package dido.oddjob.stream;

import dido.data.GenericData;
import org.oddjob.beanbus.Destination;
import dido.pickles.CloseableConsumer;
import dido.pickles.StreamOut;
import org.oddjob.framework.adapt.Start;
import org.oddjob.framework.adapt.Stop;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class StreamOutDestination<F> implements Runnable, Closeable, Consumer<GenericData<F>> {

    private String name;

    private StreamOut<F> streamOut;

    private OutputStream output;

    private Consumer<? super GenericData<F>> to;

    private CloseableConsumer<GenericData<F>> consumer;

    private volatile int count;

    @Start
    @Override
    public void run() {

        count = 0;
        try {
            this.consumer = Objects.requireNonNull(streamOut, "No Out")
                    .consumerFor(Objects.requireNonNull(output, "No Output Stream"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void accept(GenericData<F> data) {

        consumer.accept(data);
        ++count;

        if (to != null) {
            to.accept(data);
        }
    }

    @Stop
    @Override
    public void close() throws IOException {

        consumer.close();
        consumer = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StreamOut<F> getStreamOut() {
        return streamOut;
    }

    public void setStreamOut(StreamOut<F> streamOut) {
        this.streamOut = streamOut;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
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
