package dido.operators;

import java.util.Arrays;
import java.util.function.Supplier;

public class LeftStreamJoinType<F> implements Supplier<StreamJoin<F>> {

    private int[] primaryIndices;

    private F[] primaryFields;

    private int[] foreignIndices;

    private F[] foreignFields;

    private int[] secondaryIndices;

    private F[] secondaryFields;

    @Override
    public StreamJoin<F> get() {

        return LeftStreamJoin.<F>with()
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

    public F[] getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(F[] primaryFields) {
        this.primaryFields = primaryFields;
    }

    public F[] getForeignFields() {
        return foreignFields;
    }

    public void setForeignFields(F[] foreignFields) {
        this.foreignFields = foreignFields;
    }

    public F[] getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(F[] secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    @Override
    public String toString() {
        return "LeftStreamJoin: primaries=" + Arrays.toString(primaryIndices)
                + "foreign=" + Arrays.toString(foreignIndices)
                + "secondaries=" + Arrays.toString(secondaryIndices);
    }
}
