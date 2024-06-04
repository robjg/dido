package dido.text;

import dido.data.DataSchema;
import dido.how.DataOutHow;

import java.io.OutputStream;

/**
 * @oddjob.description Creates an Out that write data to a text table.
 *
 * @oddjob.example To a Text Table.
 * {@oddjob.xml.resource config/ToTableExample.xml}
 *
 */
public class TextTableDido<F> {

    /**
     * @oddjob.description The schema to use when writing out the schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private volatile DataSchema<F> schema;

    public DataOutHow<F, OutputStream> toOut() {
        return TextTableOut.<F>ofOptions()
                .schema(this.schema)
                .create();
    }

    public DataSchema<F> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<F> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
