package dido.objects;

import dido.data.RepeatingData;

public interface RepeatingSerializer extends DidoSerializer {

    RepeatingData serialize(Object src);
}
