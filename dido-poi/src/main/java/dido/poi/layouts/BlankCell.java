package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.conversion.DidoConversionProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author rob
 * @oddjob.description Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 */
public class BlankCell extends AbstractDataCell {

    @Override
    public Class<Void> getType() {
        return Void.class;
    }

    @Override
    public CellType getCellType() {
        return CellType.BLANK;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {
        return new NullGetter(schemaField);
    }

    static class NullGetter extends AbstractCellGetter {


        NullGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return null;
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField, FieldGetter getter, DidoConversionProvider conversionProvider) {
        return new Injector() {
            @Override
            public void insertValueInto(Cell cell, DidoData data) {
                cell.setBlank();
            }
        };
    }

}
