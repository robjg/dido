package dido.poi.utils;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import dido.poi.CellInProvider;
import dido.poi.DataRow;
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
                                             Collection<? extends CellInProvider> cellList,
                                             DidoConversionProvider conversionProvider) {

        FieldGetter[] getters = new FieldGetter[schema.lastIndex()];

        CellIn.Capture capture = new CellIn.Capture() {
            @Override
            public void accept(SchemaField schemaField, FieldGetter getter) {
                getters[schemaField.getIndex() - 1] = getter;
            }
        };

        int lastIndex = 0;
        for (CellInProvider cellProvider : cellList) {
            if (cellProvider.getIndex() == 0) {
                ++lastIndex;
            } else {
                lastIndex = cellProvider.getIndex();
            }

            SchemaField schemaField = Objects.requireNonNull(schema.getSchemaFieldAt(lastIndex),
                    "Programmer error: Schema does not match cells at index [" + lastIndex + "]" +
                            ", schema is: " + schema);

            try {
                CellIn cellIn = cellProvider.provideCellIn(lastIndex,
                        schema,
                        conversionProvider);

                cellIn.capture(capture);
            }
            catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to find getter for cell [%d]:%s"
                        ,lastIndex, cellProvider.getName()), e);
            }
        }

        return new DataRowFactory(schema, getters);
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

    class RowDataWrapper extends AbstractData implements DataRow {

        private final RowIn row;

        RowDataWrapper(RowIn row) {
            this.row = row;
        }

        @Override
        public RowIn getRowIn() {
            return row;
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
