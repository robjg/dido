package dido.pickles;

import dido.data.GenericData;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Something that can consume {@link GenericData} to an {@code OutputStream}.
 *
 * @param <F> The field type of the GenericData.
 */
public interface StreamOut<F> extends DataOutHow<F, OutputStream> {

    DataOut<F> outTo(OutputStream outputStream) throws IOException;
}
