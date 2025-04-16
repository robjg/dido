package dido.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Provides a {@link JsonWriterWrapper} from a {@code Writer}.
 *
 * @see DataOutJson
 */
public interface JsonWriterWrapperProvider {

    JsonWriterWrapper writerFor(Writer writer) throws IOException;
}
