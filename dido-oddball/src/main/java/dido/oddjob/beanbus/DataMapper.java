package dido.oddjob.beanbus;

import dido.data.DataSchema;
import dido.data.schema.HasSchema;
import dido.data.schema.SchemaAware;
import dido.how.RefinableFunction;
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
 * @oddjob.description Provide a BeanBus component that will work specifically
 * with Schema Aware Functions to initialise them and propagate any resultant
 * schema to the next component if it is Schema Aware.
 * <p>
 * This is functionally equivalent to {@code bus:map} but providing access to schemas
 * may be beneficial as mappers don't need to wait for data for a schema to initialise
 * with.
 *
 * @oddjob.example From and To lines of text.
 * {@oddjob.xml.resource dido/oddjob/beanbus/MapInOut.xml}

 * @param <F> The type of data being mapped from.
 * @param <T> The type of data being mapped to.
 */
public class DataMapper<F, T>
        implements Consumer<F>, Resettable, Outbound<T>, Service, SchemaAware {

    private static final Logger logger = LoggerFactory.getLogger(DataMapper.class);

    private String name;

    private Function<F, T> function;

    private Consumer<? super T> to;

    private final AtomicInteger count = new AtomicInteger(0);

    private final AtomicInteger sent = new AtomicInteger(0);

    private final SchemaTracker schemaTracker = new SchemaTracker();

    private volatile DataSchema schema;

    @Override
    public void setSchema(DataSchema schema) {
        schemaTracker.setSchema(schema);
    }

    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public void start() {

        Objects.requireNonNull(this.function, "No mapper provided");

        logger.info("Starting with mapper {} and sending to {}",
                function, to);

        schemaTracker.onSchema(
                schema -> {

                    logger.info("Setting schema to {}", schema);

                    if (function instanceof RefinableFunction<F, T> refinableOutHow) {
                        this.function = refinableOutHow.forSchema(schema);
                    }

                    if (function instanceof HasSchema hasSchema) {
                        this.schema = hasSchema.getSchema();

                        if (this.to instanceof SchemaAware schemaAware) {
                            schemaAware.setSchema(this.schema);
                        }
                    }
                });
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
        schema = null;
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
