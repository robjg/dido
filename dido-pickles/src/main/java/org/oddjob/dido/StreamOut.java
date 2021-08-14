package org.oddjob.dido;

import dido.data.GenericData;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Something that can consume {@link GenericData} to an {@code OutputStream}.
 *
 * @param <F> The field type of the GenericData.
 */
public interface StreamOut<F> {

    CloseableConsumer<GenericData<F>> consumerFor(OutputStream outputStream) throws IOException;
}
