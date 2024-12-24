package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.conversion.DidoConversionProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 */
public class BlankColumn extends AbstractColumn {

    protected BlankColumn(Settings settings) {
        super(settings);
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        @Override
        protected Settings self() {
            return this;
        }

        public BlankColumn make() {
            return new BlankColumn(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

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
