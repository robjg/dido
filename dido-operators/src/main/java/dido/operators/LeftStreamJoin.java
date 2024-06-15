package dido.operators;

import dido.data.Concatenator;
import dido.data.DidoData;
import dido.data.SubData;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class LeftStreamJoin implements StreamJoin {


    private final Consumer<DidoData> primary = new PrimaryConsumer();

    private final Consumer<DidoData> secondary = new SecondaryConsumer();

    private final Map<DidoData, SingleJoin> data = new ConcurrentHashMap<>();

    private final Function<DidoData, DidoData> primaryIndices;

    private final Function<DidoData, DidoData> foreignIndices;

    private final Function<DidoData, DidoData> secondaryIndices;

    private volatile Consumer<? super DidoData> to;

    private final Concatenator.Factory concatenator = Concatenator.withSettings()
            .skipDuplicates(true).factory();

    private LeftStreamJoin(With with) {
        this.primaryIndices = with.primaryIndices();
        this.foreignIndices = with.foreignIndices();
        this.secondaryIndices = with.secondaryIndices();
    }

    public static class With {

        private int[] primaryIndices;

        private String[] primaryFields;

        private int[] foreignIndices;

        private String[] foreignFields;

        private int[] secondaryIndices;

        private String[] secondaryFields;

        public With primaryIndices(int... primaryIndices) {
            this.primaryIndices = primaryIndices;
            return this;
        }

        public With foreignIndices(int... foreignIndices) {
            this.foreignIndices = foreignIndices;
            return this;
        }

        public With secondaryIndices(int... secondaryIndices) {
            this.secondaryIndices = secondaryIndices;
            return this;
        }

        public With primaryFields(String... primaryFields) {
            this.primaryFields = primaryFields;
            return this;
        }

        public With foreignFields(String... foreignFields) {
            this.foreignFields = foreignFields;
            return this;
        }

        public With secondaryFields(String... secondaryFields) {
            this.secondaryFields = secondaryFields;
            return this;
        }

        private Function<DidoData, DidoData> primaryIndices() {
            if (primaryIndices == null) {
                return SubData.ofFields(Objects.requireNonNull(primaryFields));
            }
            else {
                return SubData.ofIndices(primaryIndices);
            }
        }

        private Function<DidoData, DidoData> foreignIndices() {
            if (foreignIndices == null) {
                return SubData.ofFields(Objects.requireNonNull(foreignFields));
            }
            else {
                return SubData.ofIndices(foreignIndices);
            }
        }

        private Function<DidoData, DidoData> secondaryIndices() {
            if (secondaryIndices == null) {
                return SubData.ofFields(Objects.requireNonNull(secondaryFields));
            }
            else {
                return SubData.ofIndices(secondaryIndices);
            }
        }

        public StreamJoin make() {
            return new LeftStreamJoin(this);
        }
    }

    public static <F> With with() {
        return new With();
    }

    @Override
    public Consumer<DidoData> getPrimary() {
        return primary;
    }

    @Override
    public Consumer<DidoData> getSecondary() {
        return secondary;
    }

    @Override
    public void setTo(Consumer<? super DidoData> to) {
        this.to = to;
    }

    class PrimaryConsumer implements Consumer<DidoData> {

        @Override
        public void accept(DidoData primaryData) {

            DidoData keyOfPrimary = primaryIndices.apply(primaryData);

            DidoData foreignKey = foreignIndices.apply(primaryData);

            SingleJoin singleJoin = data.computeIfAbsent(foreignKey, k -> new SingleJoin());

            singleJoin.primaries.mappedByKey.put(keyOfPrimary, primaryData);
            // ok so long as this is always called on the same thread.
            if (singleJoin.secondary != null) {
                to.accept(concatenator.concat(primaryData, singleJoin.secondary));
            }
        }
    }

    class SecondaryConsumer implements Consumer<DidoData> {

        @Override
        public void accept(DidoData secondaryData) {

            DidoData keyOfSecondary = secondaryIndices.apply(secondaryData);

            SingleJoin singleJoin = data.computeIfAbsent(keyOfSecondary, k -> new SingleJoin());

            singleJoin.secondary = secondaryData;

            singleJoin.primaries.mappedByKey.values()
                    .forEach(primary -> to.accept(concatenator.concat(primary, secondaryData)));
        }
    }

    static class Primaries {

        private final Map<DidoData, DidoData> mappedByKey = new ConcurrentHashMap<>();
    }

    static class SingleJoin {

        private final Primaries primaries = new Primaries();

        private volatile DidoData secondary;
    }
}
