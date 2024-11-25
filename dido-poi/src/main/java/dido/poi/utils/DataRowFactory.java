package dido.poi.utils;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractFieldGetter;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import dido.poi.CellInProvider;
import dido.poi.RowIn;

import java.util.Collection;
import java.util.Objects;

/**
 * Create {@link DidoData} by wrapping an {@link RowIn}.
 */
public class DataRowFactory {

    private final ReadSchema schema;

    private final FieldGetter[] getters;

    private DataRowFactory(DataSchema schema, FieldGetter[] getters) {
        this.schema = ReadSchema.from(Objects.requireNonNull(schema),
                new CellReadStrategy());
        this.getters = getters;
    }

    public static DataRowFactory newInstance(DataSchema schema,
                                             Collection<? extends CellInProvider<?>> cellList,
                                             DidoConversionProvider conversionProvider) {

        FieldGetter[] cells = new FieldGetter[schema.lastIndex()];

        int lastIndex = 0;
        for (CellInProvider<?> cellProvider : cellList) {
            if (cellProvider.getIndex() == 0) {
                ++lastIndex;
            } else {
                lastIndex = cellProvider.getIndex();
            }

            cells[lastIndex - 1] = new CellGetter(
                    cellProvider.provideCellIn(lastIndex, conversionProvider));
        }

        return new DataRowFactory(schema, cells);
    }

    static class CellGetter extends AbstractFieldGetter {

        private final CellIn<?> cellIn;

        CellGetter(CellIn<?> cellIn) {
            this.cellIn = cellIn;
        }

        @Override
        public Object get(DidoData data) {
            return cellIn.getValue(((RowDataWrapper) data).row);
        }
    }

    class CellReadStrategy implements ReadStrategy {

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                FieldGetter getter = getters[index - 1];
                if (getter == null) {
                    throw new NoSuchFieldException(index, schema);
                }
                return getter;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, schema);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, schema);
            }
            return getFieldGetterAt(index);
        }
    }

    public DidoData wrap(RowIn row) {
        return new RowDataWrapper(row);
    }

    class RowDataWrapper extends AbstractData {

        private final RowIn row;

        RowDataWrapper(RowIn row) {
            this.row = row;
        }

        @Override
        public ReadSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return schema.getFieldGetterAt(index)
                    .get(this);
        }

        @Override
        public boolean hasIndex(int index) {
            return getters[index - 1] != null;
        }
    }
}
