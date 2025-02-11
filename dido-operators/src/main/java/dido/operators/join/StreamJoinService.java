package dido.operators.join;

import dido.data.DidoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @oddjob.description A service that Joins two sources of {@link DidoData} into a single
 * destination.
 */
public class StreamJoinService implements Runnable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(StreamJoinService.class);

    /**
     * @oddjob.description The name of the component.
     * @oddjob.required No.
     */
    private volatile String name;

    /**
     * @oddjob.description The join operation.
     * @oddjob.required Yes.
     */
    private volatile StreamJoin join;

    /**
     * @oddjob.description The destination.
     * @oddjob.required No, set automatically by BeanBus.
     */
    private volatile Consumer<? super DidoData> to;

    private volatile ExecutorService executor;

    /**
     * @oddjob.property primary
     * @oddjob.description The primary source of data.
     * @oddjob.required Read Only.
     */
    private volatile Consumer<DidoData> primaryConsumer;

    /**
     * @oddjob.property secondary
     * @oddjob.description The secondary source of data.
     * @oddjob.required Read Only.
     */
    private volatile Consumer<DidoData> secondaryConsumer;

    /**
     * @oddjob.description The number of items sent to the destination.
     * @oddjob.required Read Only.
     */
    private volatile int count;

    @Override
    public void run() {

        StreamJoin join = Objects.requireNonNull(this.join, "No Join Specified");
        Consumer<? super DidoData> to = Objects.requireNonNull(this.to, "No destination");

        this.executor = Executors.newSingleThreadExecutor();

        join.setTo(data -> {
            //noinspection NonAtomicOperationOnVolatileField
            ++count;
            to.accept(data);
        });

        primaryConsumer = new Consumer<>() {
            volatile int count;

            @Override
            public void accept(DidoData indexedData) {
                //noinspection NonAtomicOperationOnVolatileField
                ++count;

                executor.execute(() -> join.getPrimary().accept(indexedData));
            }

            @Override
            public String toString() {
                return "Primary Consumer, count=" + count;
            }
        };

        secondaryConsumer = new Consumer<>() {
            volatile int count;

            @Override
            public void accept(DidoData indexedData) {
                //noinspection NonAtomicOperationOnVolatileField
                ++count;

                executor.execute(() -> join.getSecondary().accept(indexedData));
            }

            @Override
            public String toString() {
                return "Secondary Consumer, count=" + count;
            }
        };
    }

    @Override
    public void close() throws InterruptedException {

        logger.info("Shutting down with consumers [{}] and [{}]", primaryConsumer, secondaryConsumer);

        this.executor.shutdown();
        this.executor.awaitTermination(15, TimeUnit.SECONDS);

        logger.info("Shutdown complete, final count={}", count);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StreamJoin getJoin() {
        return join;
    }

    public void setJoin(StreamJoin join) {
        this.join = join;
    }

    public Consumer<? super DidoData> getTo() {
        return to;
    }

    public void setTo(Consumer<? super DidoData> to) {
        this.to = to;
    }

    public Consumer<DidoData> getPrimary() {
        return this.primaryConsumer;
    }

    public Consumer<DidoData> getSecondary() {
        return this.secondaryConsumer;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
