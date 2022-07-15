package dido.data;

public abstract class AbstractDataSchema<F> implements DataSchema<F> {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSchema) {
            return DataSchema.equals(this, (DataSchema<?>) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return DataSchema.hashCode(this);
    }

    @Override
    public String toString() {
        return DataSchema.toString(this);
    }

}
