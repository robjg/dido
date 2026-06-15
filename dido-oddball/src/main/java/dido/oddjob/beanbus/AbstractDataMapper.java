package dido.oddjob.beanbus;

import org.oddjob.Resettable;
import org.oddjob.beanbus.Outbound;
import org.oddjob.framework.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base class for Something that Maps Data.
 *
 * @param <F>
 * @param <T>
 */
public class AbstractDataMapper<F, T> implements Consumer<F>, Resettable, Outbound<T>, Service {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataMapper.class);

    private String name;

    private Function<F, T> function;

    private Consumer<? super T> to;

    private final AtomicInteger count = new AtomicInteger(0);

    private final AtomicInteger sent = new AtomicInteger(0);

    @Override
    public void start() {

        logger.info("Starting with mapper {} and sending to {}",
                Objects.requireNonNull(function, "No mapper provided"),
                to);
    }

    @Override
    public void stop() {

        logger.info("Stopping, count {}, sent {}",
                count.get(), sent.get());
    }

    @Override
    public void accept(F from) {

        count.incrementAndGet();

        T transformed = function.apply(from);

        if (transformed != null && to != null) {
            to.accept(transformed);
            sent.incrementAndGet();
        }
    }

    protected void reset() {
        count.set(0);
        sent.set(0);
    }

    @Override
    public boolean softReset() {
        reset();
        return true;
    }

    @Override
    public boolean hardReset() {
        reset();
        return false;
    }

    @Override
    public void setTo(Consumer<? super T> to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFunction(Function<F, T> function) {
        this.function = function;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public AtomicInteger getSent() {
        return sent;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
