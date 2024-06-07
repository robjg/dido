package dido.operators;

import dido.data.DidoData;
import dido.data.IndexedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class StreamJoinService implements Runnable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(StreamJoinService.class);

    private volatile String name;

    private volatile StreamJoin<String> join;

    private volatile Consumer<? super DidoData> to;

    private volatile ExecutorService executor;

    private volatile Consumer<IndexedData<String>> primaryConsumer;

    private volatile Consumer<IndexedData<String>> secondaryConsumer;

    private volatile int count;

    @Override
    public void run() {

        StreamJoin<String> join = Objects.requireNonNull(this.join, "No Join Specified");
        Consumer<? super DidoData> to = Objects.requireNonNull(this.to, "No destination");

        this.executor = Executors.newSingleThreadExecutor();

        join.setTo(data -> {
            //noinspection NonAtomicOperationOnVolatileField
            ++count;
            to.accept(DidoData.adapt(data));
        });

        primaryConsumer = new Consumer<>() {
            volatile int count;

            @Override
            public void accept(IndexedData<String> indexedData) {
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
            public void accept(IndexedData<String> indexedData) {
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

    public StreamJoin<String> getJoin() {
        return join;
    }

    public void setJoin(StreamJoin<String> join) {
        this.join = join;
    }

    public Consumer<? super DidoData> getTo() {
        return to;
    }

    public void setTo(Consumer<? super DidoData> to) {
        this.to = to;
    }

    public Consumer<IndexedData<String>> getPrimary() {
        return this.primaryConsumer;
    }

    public Consumer<IndexedData<String>> getSecondary() {
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
