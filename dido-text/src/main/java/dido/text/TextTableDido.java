package dido.text;

import dido.data.generic.GenericDataSchema;
import dido.how.DataOutHow;
import dido.how.StreamHows;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;

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

    /**
     * @oddjob.description A string code for which borders to show. Until this is documented here, you'll need
     * to look at the source code in Maven to understand the format.
     * @oddjob.required No.
     */
    private ShownBorders shownBorders;

    /**
     * @oddjob.description A string code for which borders to show. Until this is documented here, you'll need
     * to look at the source code in Maven to understand the format.
     * @oddjob.required No.
     */
    private BorderStyle borderStyle;

    public DataOutHow<OutputStream> toOut() {
        DataOutTextTable dataOut = DataOutTextTable.with()
                .schema(this.schema)
                .shownBorders(this.shownBorders)
                .borderStyle(this.borderStyle)
                .make();

        return StreamHows.fromWriterHow(dataOut);
    }

    public GenericDataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(GenericDataSchema<String> schema) {
        this.schema = schema;
    }

    public ShownBorders getShownBorders() {
        return shownBorders;
    }

    public void setShownBorders(ShownBorders shownBorders) {
        this.shownBorders = shownBorders;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
