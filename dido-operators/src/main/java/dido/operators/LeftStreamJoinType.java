package dido.operators;

import java.util.Arrays;
import java.util.function.Supplier;

public class LeftStreamJoinType implements Supplier<StreamJoin<String>> {

    private int[] primaryIndices;

    private String[] primaryFields;

    private int[] foreignIndices;

    private String[] foreignFields;

    private int[] secondaryIndices;

    private String[] secondaryFields;

    @Override
    public StreamJoin<String> get() {

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
