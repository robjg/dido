package dido.data;

import java.util.List;
import java.util.stream.Stream;

public interface GenericRepeatingData<F> extends Iterable<GenericData<F>> {

    int size();

    GenericData<F> get(int row);

    Stream<GenericData<F>> stream();

    static <F> GenericRepeatingData<F> of(List<? extends IndexedData<F>> list) {
        return RepeatingDataWrappers.ofGeneric(list);
    }

    @SafeVarargs
    static <F> GenericRepeatingData<F> of(IndexedData<F>... data) {
        return RepeatingDataWrappers.ofGeneric(data);
    }

}
