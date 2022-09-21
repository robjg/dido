package dido.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RepeatingDataWrappers {

    public static <F> RepeatingData<F> of(List<? extends IndexedData<F>> list) {
        return new ListWrapper<>(list);
    }

    public static <F> RepeatingData<F> of(IndexedData<F>... data) {

        return new ArrayWrapper<>(data);
    }

    private static class ArrayWrapper<F> extends AbstractRepeatingData<F> {

        private final IndexedData<F>[] array;

        private ArrayWrapper(IndexedData<F>[] array) {
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

    static class ListWrapper<F> extends AbstractRepeatingData<F> {

        private final List<? extends IndexedData<F>> list;

        private ListWrapper(List<? extends IndexedData<F>> list) {
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
