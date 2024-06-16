package dido.data.generic;

import dido.data.RepeatingDataWrappers;

import java.util.List;
import java.util.stream.Stream;

public interface GenericRepeatingData<F> extends Iterable<GenericData<F>> {

    int size();

    GenericData<F> get(int row);

    Stream<GenericData<F>> stream();

    static <F> GenericRepeatingData<F> of(List<? extends GenericData<F>> list) {
        return RepeatingDataWrappers.ofGeneric(list);
    }

    @SafeVarargs
    static <F> GenericRepeatingData<F> of(GenericData<F>... data) {
        return RepeatingDataWrappers.ofGeneric(data);
    }

}
