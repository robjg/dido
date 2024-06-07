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
public class TextTableDido {

    /**
     * @oddjob.description The schema to use when writing out the schema will be used to limit the number
     * of fields written.
     * @oddjob.required No.
     */
    private volatile DataSchema<String> schema;

    public DataOutHow<OutputStream> toOut() {
        return TextTableOut.ofOptions()
                .schema(this.schema)
                .create();
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
