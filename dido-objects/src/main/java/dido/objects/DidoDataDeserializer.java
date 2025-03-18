package dido.objects;

import dido.data.DidoData;

/**
 * Deserialize {@link DidoData} into an Object.
 *
 * @param <T> The type of the object.
 */
public interface DidoDataDeserializer<T> extends DidoDeserializer {

    T deserialize(DidoData data);

}
