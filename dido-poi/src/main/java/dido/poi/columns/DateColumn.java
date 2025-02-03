package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.data.util.TypeUtil;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Define a date column.
 */
public class DateColumn extends AbstractColumn {

    public static final String DEFAULT_DATE_STYLE = "date";

    public static final Type TYPE = LocalDateTime.class;

    protected DateColumn(Settings settings) {
        super(settings);
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        @Override
        protected Settings self() {
            return this;
        }

        public DateColumn make() {
            return new DateColumn(this);
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
        return CellType.NUMERIC;
    }

    @Override
    protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {
        Class<?> type = TypeUtil.classOf(schemaField.getType());

        if (type.isAssignableFrom(LocalDateTime.class)) {
            return new LocalDateTimeGetter(schemaField);
        } else if (type.isAssignableFrom(Date.class)) {
            return new DateGetter(
                    schemaField);
        } else if (type.isAssignableFrom(LocalDate.class)) {
            return new LocalDateGetter(schemaField);
        } else if (type.isAssignableFrom(LocalTime.class)) {
            return new LocalTimeGetter(schemaField);
        } else {
            return new CellGetterWithConversion<>(
                    new LocalDateTimeGetter(schemaField),
                    RequiringConversion.with(conversionProvider).from(LocalDateTime.class).to(type));
        }
    }

    static class DateGetter extends AbstractCellGetter {

        DateGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getCell(data).getDateCellValue();
        }
    }

    static class LocalDateTimeGetter extends AbstractCellGetter {

        LocalDateTimeGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getCell(data).getLocalDateTimeCellValue();
        }
    }

    static class LocalDateGetter extends AbstractCellGetter {

        LocalDateGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getCell(data).getLocalDateTimeCellValue().toLocalDate();
        }
    }

    static class LocalTimeGetter extends AbstractCellGetter {

        LocalTimeGetter(SchemaField schemaField) {
            super(schemaField);
        }

        @Override
        public Object get(DidoData data) {
            return getCell(data).getLocalDateTimeCellValue().toLocalTime();
        }
    }

    @Override
    protected Injector injectorFor(SchemaField schemaField,
                                   FieldGetter getter,
                                   DidoConversionProvider conversionProvider) {

        Type fromType = schemaField.getType();

        BiConsumer<Cell, Object> setter;
        if (fromType == LocalDateTime.class) {
            setter = (cell, value) -> cell.setCellValue((LocalDateTime) value);
        } else if (fromType == LocalDate.class) {
            setter = (cell, value) -> cell.setCellValue((LocalDate) value);
        } else if (TypeUtil.isAssignableFrom(Date.class, fromType)) {
            setter = (cell, value) -> cell.setCellValue((Date) value);
        } else {
            Function<Object, LocalDateTime> conversion = RequiringConversion.with(conversionProvider)
                    .from(fromType).to(LocalDateTime.class);
            setter = (cell, value) -> cell.setCellValue(conversion.apply(value));
        }
        return (cell, data) -> {

            Object value = getter.get(data);

            if (value == null) {
                cell.setBlank();
            } else {
                setter.accept(cell, value);
            }
        };
    }

    @Override
    public String getDefaultStyle() {
        return DEFAULT_DATE_STYLE;
    }
}
