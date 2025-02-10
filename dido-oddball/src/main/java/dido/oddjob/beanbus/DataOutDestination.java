package dido.oddjob.beanbus;

import dido.data.DidoData;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.beanbus.Destination;
import org.oddjob.framework.adapt.Start;
import org.oddjob.framework.adapt.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @oddjob.description A Bean Bus Destination that accepts {@link DidoData} and writes it out to the given
 * 'to' according to the given 'how'.
 * See any of the formatters for examples of how to use.
 *
 * @oddjob.example Writes lines out.
 * {@oddjob.xml.resource dido/oddjob/stream/StreamInOut.xml}
 *
 * @param <O> The type of the output.
 */
public class DataOutDestination<O>
        implements Runnable, AutoCloseable, Consumer<DidoData>, ArooaSessionAware {

    private static final Logger logger = LoggerFactory.getLogger(DataOutDestination.class);

    private ArooaSession session;

    /**
     * @oddjob.description The name of the component.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.description How to write the data out.
     * @oddjob.required Yes.
     */
    private DataOutHow<O> how;

    /**
     * @oddjob.description Where to write data to.
     * @oddjob.required Yes.
     */
    private ArooaValue to;

    /**
     * @oddjob.description If set, data will be forwarded here. Set automatically by BeanBus.
     * @oddjob.required No.
     */
    private Consumer<? super DidoData> next;

    /** Consumer created by applying the how. */
    private DataOut consumer;

    /**
     * @oddjob.description Count of data sent out.
     * @oddjob.required Read only.
     */
    private final AtomicInteger count = new AtomicInteger();

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Start
    @Override
    public void run() {

        count.set(0);

        DataOutHow<O> how = Objects.requireNonNull(this.how, "No How");

        O to;
        try {
            to = session.getTools().getArooaConverter().convert(
                    Objects.requireNonNull(this.to, "Nothing to Read From."),
                    how.getOutType());
        }
        catch (ArooaConversionException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            this.consumer =how.outTo(to);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void accept(DidoData data) {

        consumer.accept(data);
        count.incrementAndGet();

        if (next != null) {
            next.accept(data);
        }
    }

    @Stop
    @Override
    public void close() {

        try {
            consumer.close();
        }
        catch (Exception e) {
            logger.error("Failed to close {}", consumer, e);
        }
        consumer = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataOutHow<O> getHow() {
        return how;
    }

    public void setHow(DataOutHow<O> how) {
        this.how = how;
    }

    public ArooaValue getTo() {
        return to;
    }

    public void setTo(ArooaValue to) {
        this.to = to;
    }

    public Consumer<? super DidoData> getNext() {
        return next;
    }

    @Destination
    public void setNext(Consumer<? super DidoData> next) {
        this.next = next;
    }

    public int getCount() {
        return count.get();
    }

    @Override
    public String toString() {
        return Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName());
    }
}
