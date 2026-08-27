package dido.how.useful;

import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstract helper class for providing convenience methods for converting various
 * outputs to Appendable.
 */
public abstract class AppendableOutHow implements DataOutHow<Appendable> {

    public DataOut toAppendable(Appendable appendable) {
        return outTo(appendable);
    }

    public DataOut toWriter(Writer writer) {
        return outTo(writer);
    }

    public DataOut toPath(Path path) {
        try {
            return outTo(Files.newBufferedWriter(path));
        } catch (IOException e) {
            throw new DataException(e);
        }
    }

    public DataOut toOutputStream(OutputStream outputStream) {

        return outTo(new OutputStreamWriter(outputStream));
    }

}
