package dido.oddjob.util;

import dido.data.DidoData;

/**
 * @oddjob.description Creates an In or Out for Lines of Text. The {@link DidoData} is created or expected to have
 * a field with the give field name or the name 'Line'.
 */
public class LinesBeanBase {

    /**
     * @oddjob.property
     * @oddjob.description The name of the field for the data.
     * @oddjob.required No. defaults to Line.
     */
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "LinesDido{" +
                "fieldName='" + fieldName + '\'' +
                '}';
    }
}
