package dido.operators.transform;

import java.util.function.Supplier;

/**
 * @oddjob.description Remove the value for a field or index. Participates in an {@link TransformationFactory}.
 *
 * @oddjob.example Removes a value by Field Name. The actual example is
 * a single element of XML half-way down the configuration. First this example
 * sets up the schema and expected schemas. Then it creates 3 items
 * of data. It pipes these through a transformation that removes the Qty
 * field from each item. It collects this data and then finally uses
 * JavaScript again to verify the result.
 * {@oddjob.xml.resource dido/operators/transform/ValueRemoveExample.xml}
 */
public class ValueRemoveFactory implements Supplier<FieldView> {

    /**
     * @oddjob.description The field name.
     * @oddjob.required No
     */
    private String field;

    /**
     * @oddjob.description The index to set the value at.
     * @oddjob.required No
     */
    private int at;


    @Override
    public FieldView get() {
        if (field == null) {
            if (at == 0) {
                throw new IllegalArgumentException("Remove: Field or Index must be provided");
            }
            else {
                return FieldViews.removeAt(at);
            }
        }
        else {
            if (at == 0) {
                return FieldViews.removeNamed(field);
            }
            else {
                throw new IllegalArgumentException("Remove: Field " + field + " and Index " +
                        at + " provided. Only one can be specified.");
            }
        }
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getAt() {
        return at;
    }

    public void setAt(int at) {
        this.at = at;
    }

    @Override
    public String toString() {
        return "ValueRemoveFactory{" +
                "field='" + field + '\'' +
                ", at=" + at +
                '}';
    }
}
