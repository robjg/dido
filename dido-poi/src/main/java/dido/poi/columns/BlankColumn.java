package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.conversion.DidoConversionProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

/**
 * Create a column cells that are blank. Provided for
 * completeness - not sure of the use case for this.
 */
public class BlankColumn extends AbstractColumn {

    public static final Class<Void> TYPE = void.class;

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
    public Type getType() {
        return TYPE;
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
