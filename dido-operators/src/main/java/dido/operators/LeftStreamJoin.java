package dido.operators;

import dido.data.Concatenator;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.data.SubData;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class LeftStreamJoin<F> implements StreamJoin<F> {


    private final Consumer<IndexedData<F>> primary = new PrimaryConsumer();

    private final Consumer<IndexedData<F>> secondary = new SecondaryConsumer();

    private final Map<IndexedData<F>, SingleJoin<F>> data = new ConcurrentHashMap<>();

    private final Function<IndexedData<F>, GenericData<F>> primaryIndices;

    private final Function<IndexedData<F>, GenericData<F>> foreignIndices;

    private final Function<IndexedData<F>, GenericData<F>> secondaryIndices;

    private volatile Consumer<? super GenericData<F>> to;

    private LeftStreamJoin(With<F> with) {
        this.primaryIndices = SubData.ofIndices(Objects.requireNonNull(with.primaryIndices));
        this.foreignIndices = SubData.ofIndices(Objects.requireNonNull(with.foreignIndices));
        this.secondaryIndices = SubData.ofIndices(Objects.requireNonNull(with.secondaryIndices));
    }

    public static class With<F> {

        private int[] primaryIndices;

        private int[] foreignIndices;

        private int[] secondaryIndices;

        public With<F> setPrimaryIndices(int... primaryIndices) {
            this.primaryIndices = primaryIndices;
            return this;
        }

        public With<F> setForeignIndices(int... foreignIndices) {
            this.foreignIndices = foreignIndices;
            return this;
        }

        public With<F> setSecondaryIndices(int... secondaryIndices) {
            this.secondaryIndices = secondaryIndices;
            return this;
        }

        public StreamJoin<F> make() {
            return new LeftStreamJoin<>(this);
        }
    }

    public static <F> With<F> with() {
        return new With<>();
    }

    @Override
    public Consumer<IndexedData<F>> getPrimary() {
        return primary;
    }

    @Override
    public Consumer<IndexedData<F>> getSecondary() {
        return secondary;
    }

    @Override
    public void setTo(Consumer<? super GenericData<F>> to) {
        this.to = to;
    }

    class PrimaryConsumer implements Consumer<IndexedData<F>> {

        @Override
        public void accept(IndexedData<F> primaryData) {

            IndexedData<F> keyOfPrimary = primaryIndices.apply(primaryData);

            IndexedData<F> foreignKey = foreignIndices.apply(primaryData);

            SingleJoin<F> singleJoin = data.computeIfAbsent(foreignKey, k -> new SingleJoin<>());

            singleJoin.primaries.mappedByKey.put(keyOfPrimary, primaryData);
            // ok so long as this is always called on the same thread.
            if (singleJoin.secondary != null) {
                to.accept(Concatenator.of(primaryData, singleJoin.secondary));
            }
        }
    }

    class SecondaryConsumer implements Consumer<IndexedData<F>> {

        @Override
        public void accept(IndexedData<F> secondaryData) {

            IndexedData<F> keyOfSecondary = secondaryIndices.apply(secondaryData);

            SingleJoin<F> singleJoin = data.computeIfAbsent(keyOfSecondary, k -> new SingleJoin<>());

            singleJoin.secondary = secondaryData;

            singleJoin.primaries.mappedByKey.values()
                    .forEach(primary -> to.accept(Concatenator.of(primary, secondaryData)));
        }
    }

    static class Primaries<F> {

        private final Map<IndexedData<F>, IndexedData<F>> mappedByKey = new ConcurrentHashMap<>();
    }

    static class SingleJoin<F> {

        private final Primaries<F> primaries = new Primaries<>();

        private volatile IndexedData<F> secondary;
    }
}
