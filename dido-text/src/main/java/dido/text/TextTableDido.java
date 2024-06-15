package dido.text;

import dido.data.GenericDataSchema;
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
    private volatile GenericDataSchema<String> schema;

    public DataOutHow<OutputStream> toOut() {
        return TextTableOut.ofOptions()
                .schema(this.schema)
                .create();
    }

    public GenericDataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(GenericDataSchema<String> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
