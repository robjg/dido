package dido.oddjob.beanbus;

import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataInHow;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.beanbus.Destination;
import org.oddjob.framework.adapt.Stop;

import java.io.Closeable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @oddjob.description A Bean Bus Driver that reads data from the 'from' according to the
 * given 'how'. The produced {@link DidoData} is forwarded to the next component.
 * See any of the formatters for examples of how to use.
 *
 * @oddjob.example Read lines in.
 * {@oddjob.xml.resource dido/oddjob/stream/StreamInOut.xml}
 *
 *
 * @param <I> The Input type.
 */
public class DataInDriver<I> implements Runnable, Closeable, ArooaSessionAware {

    private ArooaSession session;

    /**
     * @oddjob.description The name of the component.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.description How to read the data in.
     * @oddjob.required Yes.
     */
    private DataInHow<I> how;

    /**
     * @oddjob.description Where to read data from.
     * @oddjob.required Yes.
     */
    private ArooaValue from;

    /**
     * @oddjob.description If set, data will be forwarded here. Set automatically by BeanBus.
     * @oddjob.required No, but fairly pointless if missing.
     */
    private Consumer<? super DidoData> to;

    /**
     * @oddjob.description Count of data items read in.
     * @oddjob.required Read only.
     */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * @oddjob.description Internal stop flag. Set by calling stop.
     * @oddjob.required Read only.
     */
    private volatile boolean stop;

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public void run() {

        stop = false;

        count.set(0);

        DataInHow<I> how = Objects.requireNonNull(this.how, "No How");

        I from;
        try {
            from = session.getTools().getArooaConverter().convert(
                    Objects.requireNonNull(this.from, "Nothing to Read From."),
                    how.getInType());
        }
        catch (ArooaConversionException e) {
            throw new IllegalArgumentException(e);
        }

        try (DataIn supplier = how.inFrom(from)) {

            while (!stop) {
                DidoData data = supplier.get();
                if (data == null) {
                    break;
                }

                count.incrementAndGet();

                if (to != null) {
                    to.accept(data);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Stop
    @Override
    public void close() {

        stop = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataInHow<I> getHow() {
        return how;
    }

    public void setHow(DataInHow<I> how) {
        this.how = how;
    }

    public ArooaValue getFrom() {
        return from;
    }

    public void setFrom(ArooaValue from) {
        this.from = from;
    }

    public boolean isStop() {
        return stop;
    }

    public Consumer<? super DidoData> getTo() {
        return to;
    }

    @Destination
    public void setTo(Consumer<? super DidoData> to) {
        this.to = to;
    }

    public int getCount() {
        return count.get();
    }

    @Override
    public String toString() {
        return Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName());
    }
}
