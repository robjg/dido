package dido.operators;

import dido.data.GenericData;
import dido.data.IndexedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class StreamJoinService<F> implements Runnable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(StreamJoinService.class);

    private volatile String name;

    private volatile StreamJoin<F> join;

    private volatile Consumer<? super GenericData<F>> to;

    private volatile ExecutorService executor;

    private volatile Consumer<IndexedData<F>> primaryConsumer;

    private volatile Consumer<IndexedData<F>> secondaryConsumer;

    private volatile int count;

    @Override
    public void run() {

        StreamJoin<F> join = Objects.requireNonNull(this.join, "No Join Specified");
        Consumer<? super GenericData<F>> to = Objects.requireNonNull(this.to, "No destination");

        this.executor = Executors.newSingleThreadExecutor();

        join.setTo(data -> {
            //noinspection NonAtomicOperationOnVolatileField
            ++count;
            to.accept(data);
        });

        primaryConsumer = new Consumer<>() {
            volatile int count;

            @Override
            public void accept(IndexedData<F> indexedData) {
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
            public void accept(IndexedData<F> indexedData) {
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

    public StreamJoin<F> getJoin() {
        return join;
    }

    public void setJoin(StreamJoin<F> join) {
        this.join = join;
    }

    public Consumer<? super GenericData<F>> getTo() {
        return to;
    }

    public void setTo(Consumer<? super GenericData<F>> to) {
        this.to = to;
    }

    public Consumer<IndexedData<F>> getPrimary() {
        return this.primaryConsumer;
    }

    public Consumer<IndexedData<F>> getSecondary() {
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
