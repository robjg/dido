package dido.operators;

import java.util.Arrays;
import java.util.function.Supplier;

public class LeftStreamJoinType<F> implements Supplier<StreamJoin<F>> {

    private int[] primaryIndices;

    private int[] foreignIndices;

    private int[] secondaryIndices;

    @Override
    public StreamJoin<F> get() {

        return LeftStreamJoin.<F>with()
                .setPrimaryIndices(primaryIndices)
                .setForeignIndices(foreignIndices)
                .setSecondaryIndices(secondaryIndices)
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

    @Override
    public String toString() {
        return "LeftStreamJoin: primaries=" + Arrays.toString(primaryIndices)
                + "foreign=" + Arrays.toString(foreignIndices)
                + "secondaries=" + Arrays.toString(secondaryIndices);
    }
}
