package dido.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;

import java.io.IOException;
import java.io.Writer;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class DataOutJsonLines implements DataOut {

    private final Writer writer;

    private final DidoJsonWriter didoJsonWriter;

    private final Gson gson;

    private final String lineSeparator;

    public DataOutJsonLines(Writer writer,
                            DidoJsonWriter didoJsonWriter,
                            Gson gson,
                            String lineSeparator) {
        this.writer = writer;
        this.didoJsonWriter = didoJsonWriter;
        this.gson = gson;
        this.lineSeparator = lineSeparator;
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw DataException.of(e);
        }
    }

    @Override
    public void accept(DidoData data) {

        try {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            didoJsonWriter.write(data, jsonWriter);
            jsonWriter.flush();
            writer.append(lineSeparator);
            writer.flush();
        } catch (IOException e) {
            throw new DataException("Failed writing data " + data, e);
        }
    }

    @Override
    public String toString() {
        return "JsonLines";
    }
}
