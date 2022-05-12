package dido.data;

public interface SchemaField<F> {

    enum Is {

        SIMPLE() {
            @Override
            boolean isNested() {
                return false;
            }

            @Override
            boolean isRepeating() {
                return false;
            }
        },

        NESTED() {
            @Override
            boolean isNested() {
                return true;
            }

            @Override
            boolean isRepeating() {
                return false;
            }
        },

        REPEATING() {
            @Override
            boolean isNested() {
                return true;
            }

            @Override
            boolean isRepeating() {
                return false;
            }
        };

        abstract boolean isNested();

        abstract boolean isRepeating();
    }

    int getIndex();

    Class<?> getType();

    Is getIs();

    F getField();

    <N> DataSchema<N> getNestedSchema();


    static <F> SchemaField<F> of(int index, Class<?> type) {
        return SchemaFields.of(index, type);
    }

    static <F> SchemaField<F> of(int index, Class<?> type, F field) {
        return SchemaFields.of(index, field, type);
    }

    static <F, N> SchemaField<F> ofNested(int index, DataSchema<N> nested) {
        return SchemaFields.ofNested(index, nested);
    }

    static <F, N> SchemaField<F> ofNested(int index, F field, DataSchema<N> nested) {
        return SchemaFields.ofNested(index, field, nested);
    }

    static <F> SchemaField<F> ofRepeating(int index, DataSchema<F> nested) {
        return SchemaFields.ofRepeating(index, nested);
    }

    static <F> SchemaField<F> ofRepeating(int index, F field, DataSchema<F> nested) {
        return SchemaFields.ofRepeating(index, field, nested);
    }
}
