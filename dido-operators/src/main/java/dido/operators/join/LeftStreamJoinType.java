package dido.operators.join;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @oddjob.description A join operation that performs a full left join.
 *
 * @oddjob.example Join based on indices.
 * {@oddjob.xml.resource dido/operators/join/LeftJoinExample.xml}
 *
 * @oddjob.example Join based on field names.
 * {@oddjob.xml.resource dido/operators/join/LeftJoinExample2.xml}
 *
 * @oddjob.example A more complicated Join.
 * {@oddjob.xml.resource dido/operators/join/LeftJoinMultiKeyExample.xml}
 */
public class LeftStreamJoinType implements Supplier<StreamJoin> {

    /**
     * @oddjob.description The indices of the fields that are the key of the primary data.
     * @oddjob.required Either this or Primary Names are required.
     */
    private int[] primaryIndices;

    /**
     * @oddjob.description The names of the fields that are the key of the primary data.
     * @oddjob.required Either this or Primary Indices are required.
     */
    private String[] primaryFields;

    /**
     * @oddjob.description The indices of the fields that form the foreign key in the primary data. The data in
     * these fields must match that in the Secondary key fields by value and type for a match.
     * @oddjob.required Either this or Foreign Fields are required.
     */
    private int[] foreignIndices;

    /**
     * @oddjob.description The names of the fields that form the foreign key in the primary data. The data in
     * these fields must match that in the Secondary key fields by value and type for a match.
     * @oddjob.required Either this or Foreign Indices are required.
     */
    private String[] foreignFields;

    /**
     * @oddjob.description The indices of the fields that form the key in the secondary data. The data in
     * these fields must match that in the Foreign fields by value and type for a match.
     * @oddjob.required Either this or Secondary Fields are required.
     */
    private int[] secondaryIndices;

    /**
     * @oddjob.description The names of the fields that form the key in the secondary data. The data in
     * these fields must match that in the Foreign fields by value and type for a match.
     * @oddjob.required Either this or Secondary Indices are required.
     */
    private String[] secondaryFields;

    @Override
    public StreamJoin get() {

        return LeftStreamJoin.<String>with()
                .primaryIndices(primaryIndices)
                .primaryFields(primaryFields)
                .foreignIndices(foreignIndices)
                .foreignFields(foreignFields)
                .secondaryIndices(secondaryIndices)
                .secondaryFields(secondaryFields)
                .make();
    }

    public int[] getPrimaryIndices() {
        return primaryIndices;
    }

    public void setPrimaryIndices(int[] primaryIndices) {
        this.primaryIndices = primaryIndices;
    }

    public int[] getForeignIndices() {
        return foreignIndices;
    }

    public void setForeignIndices(int[] foreignIndices) {
        this.foreignIndices = foreignIndices;
    }

    public int[] getSecondaryIndices() {
        return secondaryIndices;
    }

    public void setSecondaryIndices(int[] secondaryIndices) {
        this.secondaryIndices = secondaryIndices;
    }

    public String[] getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(String[] primaryFields) {
        this.primaryFields = primaryFields;
    }

    public String[] getForeignFields() {
        return foreignFields;
    }

    public void setForeignFields(String[] foreignFields) {
        this.foreignFields = foreignFields;
    }

    public String[] getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(String[] secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    @Override
    public String toString() {
        return "LeftStreamJoin: primaries=" + Arrays.toString(primaryIndices)
                + "foreign=" + Arrays.toString(foreignIndices)
                + "secondaries=" + Arrays.toString(secondaryIndices);
    }
}
