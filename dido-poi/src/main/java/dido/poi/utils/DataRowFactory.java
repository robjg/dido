package dido.poi.utils;

import dido.data.AbstractNamedData;
import dido.data.DataSchema;
import dido.data.NamedData;
import dido.data.ReadableSchema;
import dido.poi.CellIn;
import dido.poi.CellInProvider;
import dido.poi.RowIn;

import java.util.Collection;
import java.util.Objects;


public class DataRowFactory {

    private final ReadableSchema schema;

    private final CellIn<?>[] cells;

    private DataRowFactory(ReadableSchema schema, CellIn<?>[] cells) {
        this.schema = Objects.requireNonNull(schema);
        this.cells = cells;
    }

    public static DataRowFactory newInstance(DataSchema schema,
                                             Collection<? extends CellInProvider<?>> cellList) {

        CellIn<?>[] cells = new CellIn<?>[schema.lastIndex()];

        int lastIndex = 0;
        for (CellInProvider<?> cellProvider : cellList) {
            if (cellProvider.getIndex() == 0) {
                ++lastIndex;
            }
            else {
                lastIndex = cellProvider.getIndex();
            }

            cells[lastIndex - 1] = cellProvider.provideCellIn(lastIndex);
        }

        return new DataRowFactory(ReadableSchema.readableSchemaFrom(schema), cells);
    }


    public NamedData wrap(RowIn row) {
        return new AbstractNamedData() {
            @Override
            public ReadableSchema getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                CellIn<?> dataCell = cells[index - 1];
                if (dataCell == null) {
                    return null;
                }

                return dataCell.getValue(row);
            }

            @Override
            public boolean hasIndex(int index) {
                return cells[index - 1] != null;
            }
        };
    }
}
