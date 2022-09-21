package dido.data;

import java.util.List;
import java.util.stream.Stream;

public interface RepeatingData<F> extends Iterable<GenericData<F>> {

    int size();

    GenericData<F> get(int row);

    Stream<GenericData<F>> stream();

    static <F> RepeatingData<F> of(List<? extends IndexedData<F>> list) {
        return RepeatingDataWrappers.of(list);
    }

    @SafeVarargs
    static <F> RepeatingData<F> of(IndexedData<F>... data) {
        return RepeatingDataWrappers.of(data);
    }

}
