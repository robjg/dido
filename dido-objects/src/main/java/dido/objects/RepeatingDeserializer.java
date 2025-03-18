package dido.objects;

import dido.data.RepeatingData;

public interface RepeatingDeserializer<T> extends DidoDeserializer {

    T deserialize(RepeatingData data);

}
