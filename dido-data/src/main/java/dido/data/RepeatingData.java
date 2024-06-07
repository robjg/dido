package dido.data;

import java.util.List;
import java.util.stream.Stream;

public interface RepeatingData extends Iterable<DidoData> {

    int size();

    DidoData get(int row);

    Stream<DidoData> stream();

    static RepeatingData of(List<DidoData> list) {
        return RepeatingDataWrappers.of(list);
    }

    static RepeatingData of(DidoData... data) {
        return RepeatingDataWrappers.of(data);
    }

}
