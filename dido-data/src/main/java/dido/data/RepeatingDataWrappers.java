package dido.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RepeatingDataWrappers {

    public static RepeatingData of(List<DidoData> list) {
        return new ListWrapper(list);
    }

    public static RepeatingData of(DidoData... data) {

        return new ArrayWrapper(data);
    }

    public static <F> GenericRepeatingData<F> ofGeneric(List<? extends IndexedData<F>> list) {
        return new GenericListWrapper<>(list);
    }

    public static <F> GenericRepeatingData<F> ofGeneric(IndexedData<F>... data) {

        return new GenericArrayWrapper<>(data);
    }

    private static class ArrayWrapper extends AbstractRepeatingData {

        private final DidoData[] array;

        private ArrayWrapper(DidoData[] array) {
            this.array = array;
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public DidoData get(int row) {
            return array[row];
        }

        @Override
        public Stream<DidoData> stream() {
            return Arrays.stream(array);
        }

        @Override
        public Iterator<DidoData> iterator() {
            return new Iterator<>() {
                int row;

                @Override
                public boolean hasNext() {
                    return row < array.length;
                }

                @Override
                public DidoData next() {
                    return array[row++];
                }
            };
        }
    }

    static class ListWrapper extends AbstractRepeatingData {

        private final List<DidoData> list;

        private ListWrapper(List<DidoData> list) {
            this.list = Objects.requireNonNull(list);
        }


        @Override
        public int size() {
            return list.size();
        }

        @Override
        public DidoData get(int row) {
            return list.get(row);
        }

        @Override
        public Stream<DidoData> stream() {
            return list.stream();
        }

        @Override
        public Iterator<DidoData> iterator() {
            return list.iterator();
        }
    }

    private static class GenericArrayWrapper<F> extends AbstractGenericRepeatingData<F> {

        private final IndexedData<F>[] array;

        private GenericArrayWrapper(IndexedData<F>[] array) {
            this.array = array;
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public GenericData<F> get(int row) {
            return GenericData.from(array[row]);
        }

        @Override
        public Stream<GenericData<F>> stream() {
            return Arrays.stream(array).map(GenericData::from);
        }

        @Override
        public Iterator<GenericData<F>> iterator() {
            return new Iterator<>() {
                int row;

                @Override
                public boolean hasNext() {
                    return row < array.length;
                }

                @Override
                public GenericData<F> next() {
                    return GenericData.from(array[row++]);
                }
            };
        }
    }

    static class GenericListWrapper<F> extends AbstractGenericRepeatingData<F> {

        private final List<? extends IndexedData<F>> list;

        private GenericListWrapper(List<? extends IndexedData<F>> list) {
            this.list = Objects.requireNonNull(list);
        }


        @Override
        public int size() {
            return list.size();
        }

        @Override
        public GenericData<F> get(int row) {
            return GenericData.from(list.get(row));
        }

        @Override
        public Stream<GenericData<F>> stream() {
            return list.stream().map(GenericData::from);
        }

        @Override
        public Iterator<GenericData<F>> iterator() {
            Iterator<? extends IndexedData<F>> iterator = list.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public GenericData<F> next() {
                    return GenericData.from(iterator.next());
                }
            };
        }
    }

}
