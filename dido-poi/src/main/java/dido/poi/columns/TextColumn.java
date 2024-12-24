package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.function.Function;

/**
 * Define a text column.
 *
 * @author rob
 */
public class TextColumn extends AbstractColumn {

    protected TextColumn(Settings settings) {
        super(settings);
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        @Override
        protected Settings self() {
            return this;
        }

        public TextColumn make() {
            return new TextColumn(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public CellType getCellType() {
        return CellType.STRING;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

        Class<?> type = schemaField.getType();

        if (type.isAssignableFrom(String.class)) {
            return new TextCellGetter(schemaField);
        } else {
            return new TextCellGetterWithConversion<>(
                    schemaField,
                    RequiringConversion.with(conversionProvider).from(String.class).to(type));
        }
    }

    static class TextCellGetter extends AbstractCellGetter {

        TextCellGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getString(data);
        }

        @Override
        public String getString(DidoData data) {
            return getCell(data).getStringCellValue();
        }
    }

    static class TextCellGetterWithConversion<R> extends TextCellGetter {

        private final Function<String, R> conversion;

        TextCellGetterWithConversion(SchemaField schemaField,
                                     Function<String, R> conversion) {
            super(schemaField);
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            try {
                return conversion.apply(super.getString(data));
            } catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get value for %s", this), e);
            }
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField, FieldGetter getter, DidoConversionProvider conversionProvider) {

        Class<?> fromType = schemaField.getType();

        Function<Object, String> conversion;

        if (String.class.isAssignableFrom(fromType)) {
            conversion = null;
        } else {
            //noinspection unchecked
            conversion = (Function<Object, String>) conversionProvider.conversionFor(fromType, String.class);
        }

        return new Injector() {
            @Override
            public void insertValueInto(Cell cell, DidoData data) {

                if (!getter.has(data)) {
                    cell.setBlank();
                    return;
                }

                String value;
                if (conversion == null) {
                    value = (getter.getString(data));
                } else {
                    value = conversion.apply(getter.get(data));
                }

                cell.setCellValue(value);
            }
        };
    }
}
