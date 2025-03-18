package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;

/**
 * Serialize an object into {@link DidoData}.
 *
 */
public interface DidoSerializer {

    DataSchema getSchema();
}
