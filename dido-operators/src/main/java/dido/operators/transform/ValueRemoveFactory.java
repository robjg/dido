package dido.operators.transform;

import dido.data.ReadSchema;

import java.util.function.Supplier;

/**
 * @oddjob.description Remove the value for a field or index. Participates in an {@link TransformationFactory}.
 *
 * @oddjob.example Removes a value by Field Name.
 * {@oddjob.xml.resource dido/operators/transform/ValueRemoveExample.xml}
 */
public class ValueRemoveFactory implements Supplier<FieldWrite> {

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
    public FieldWrite get() {
        return new RemoveTransformerFactory(this);
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

    static class RemoveTransformerFactory implements FieldWrite {

        private final String field;

        private final int at;

        RemoveTransformerFactory(ValueRemoveFactory config) {
            this.field = config.field;
            this.at = config.at;
        }

        @Override
        public Prepare prepare(ReadSchema fromSchema,
                               SchemaSetter schemaSetter) {

            FieldView fieldView;

            if (field == null) {
                fieldView = FieldViews.removeAt(at);
            }
            else {
                if (at != 0) {
                    throw new IllegalArgumentException("Remove: Field " + field + " and Index " +
                            at + " provided. Only one can be specified.");
                }
                fieldView = FieldViews.removeNamed(field);
            }

            return fieldView.asFieldWrite().prepare(fromSchema, schemaSetter);
        }
    }

    @Override
    public String toString() {
        return "ValueRemoveFactory{" +
                "field='" + field + '\'' +
                ", at=" + at +
                '}';
    }
}
