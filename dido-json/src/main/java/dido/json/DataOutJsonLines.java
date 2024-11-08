package dido.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.Writer;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class DataOutJsonLines implements DataOutHow<Writer> {

    private final Gson gson;

    private final String lineSeparator;

    DataOutJsonLines(Gson gson, String lineSeparator) {
        this.gson = gson;
        this.lineSeparator = lineSeparator;
    }

    @Override
    public Class<Writer> getOutType() {
        return Writer.class;
    }

    @Override
    public DataOut outTo(Writer writer) {

        try {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);

            return new DataOut() {
                @Override
                public void close() {
                    try {
                        jsonWriter.close();
                    } catch (IOException e) {
                        throw DataException.of(e);
                    }
                }

                @Override
                public void accept(DidoData data) {

                    gson.toJson(data, DidoData.class, jsonWriter);

                    try {
                        jsonWriter.flush();
                        writer.append(lineSeparator);
                        writer.flush();
                    } catch (IOException e) {
                        throw DataException.of("Failed writing line terminator", e);
                    }
                }
            };
        } catch (IOException e) {
            throw DataException.of(e);
        }

    }

    @Override
    public String toString() {
        return "JsonLines";
    }
}
