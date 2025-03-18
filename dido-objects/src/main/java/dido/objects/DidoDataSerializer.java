package dido.objects;

import dido.data.DidoData;

/**
 * Serialize an object into {@link DidoData}.
 *
 */
public interface DidoDataSerializer extends DidoSerializer {

    DidoData serialize(Object src);
}
