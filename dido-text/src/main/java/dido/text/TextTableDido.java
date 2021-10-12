package dido.text;

import dido.data.DataSchema;
import dido.pickles.DataOut;
import dido.pickles.DataOutHow;

import java.io.OutputStream;

public class TextTableDido<F> implements DataOutHow<F, OutputStream> {

    private DataSchema<F> schema;

    public DataSchema<F> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<F> schema) {
        this.schema = schema;
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<F> outTo(OutputStream dataOut) throws Exception {
        return TextTableOut.<F>ofOptions()
                .schema(this.schema)
                .create()
                .outTo(dataOut);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
