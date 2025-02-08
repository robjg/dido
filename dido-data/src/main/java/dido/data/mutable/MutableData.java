package dido.data.mutable;

import dido.data.DidoData;
import dido.data.WritableData;

/**
 * Data that may be changed. The schema may or may not be fixed in {@code MutableData}. If the
 * schema isn't fixed it is probably {@link MalleableData}.
 *
 */
public interface MutableData extends DidoData, WritableData {

}
