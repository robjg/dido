package dido.pickles;

import dido.data.GenericData;

import java.io.IOException;
import java.io.InputStream;

/**
 * Something that can provide {@link GenericData} from an {@code InputStream}.
 *
 * @param <F> The field type of the GenericData.
 */
public interface StreamIn<F> extends DataInHow<F, InputStream> {

    DataIn<F> inFrom(InputStream inputStream) throws IOException;
}
