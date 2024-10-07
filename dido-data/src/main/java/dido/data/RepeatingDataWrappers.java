package dido.data;

import dido.data.useful.AbstractRepeatingData;

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

}
