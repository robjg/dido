package dido.oddjob.beanbus;

import dido.data.GenericData;
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

public class DataInDriver<F, I> implements Runnable, Closeable, ArooaSessionAware {

    private ArooaSession session;

    private String name;

    private DataInHow<F, I> how;

    private ArooaValue from;

    private Consumer<? super GenericData<F>> to;

    private final AtomicInteger count = new AtomicInteger();

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

        DataInHow<F, I> how = Objects.requireNonNull(this.how, "No How");

        I from;
        try {
            from = session.getTools().getArooaConverter().convert(
                    Objects.requireNonNull(this.from, "Nothing to Read From."),
                    how.getInType());
        }
        catch (ArooaConversionException e) {
            throw new IllegalArgumentException(e);
        }

        try (DataIn<F> supplier = how.inFrom(from)) {

            while (!stop) {
                GenericData<F> data = supplier.get();
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

    public DataInHow<F, I> getHow() {
        return how;
    }

    public void setHow(DataInHow<F, I> how) {
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

    public Consumer<? super GenericData<F>> getTo() {
        return to;
    }

    @Destination
    public void setTo(Consumer<? super GenericData<F>> to) {
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
