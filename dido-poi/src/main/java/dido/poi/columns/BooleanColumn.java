package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Define a column of Boolean cells.
 */
public class BooleanColumn extends AbstractColumn {

    public static final Type TYPE = boolean.class;

    protected BooleanColumn(Settings settings) {
        super(settings);
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        @Override
        protected Settings self() {
            return this;
        }

        public BooleanColumn make() {
            return new BooleanColumn(this);
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
        return CellType.BOOLEAN;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

        Type type = schemaField.getType();

        if (type == boolean.class) {
            return new BooleanCellGetter(schemaField);
        } else {
            return new BooleanCellGetterWithConversion<>(schemaField,
                    RequiringConversion.with(conversionProvider)
                            .<Boolean>from(Boolean.class)
                            .to(type));
        }
    }

    static class BooleanCellGetter extends AbstractCellGetter {

        BooleanCellGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getBoolean(data);
        }

        @Override
        public boolean getBoolean(DidoData data) {
            return getCell(data).getBooleanCellValue();
        }
    }

    static class BooleanCellGetterWithConversion<R> extends BooleanCellGetter {

        private final Function<Boolean, R> conversion;

        BooleanCellGetterWithConversion(SchemaField schemaField,
                                        Function<Boolean, R> conversion) {
            super(schemaField);
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            try {
                return conversion.apply(super.getBoolean(data));
            } catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get value for %s", this), e);
            }
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField, FieldGetter getter, DidoConversionProvider conversionProvider) {

        Type fromType = schemaField.getType();

        Function<Object, Boolean> conversion;

        if (fromType == boolean.class) {
            conversion = null;
        } else {
            conversion = conversionProvider.conversionFor(
                    fromType, boolean.class);
        }

        return (cell, data) -> {

            if (!getter.has(data)) {
                cell.setBlank();
                return;
            }

            boolean value;
            if (conversion == null) {
                value = (getter.getBoolean(data));
            } else {
                value = conversion.apply(getter.get(data));
            }

            cell.setCellValue(value);
        };
    }
}
