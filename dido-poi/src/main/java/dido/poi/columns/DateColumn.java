package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.data.util.TypeUtil;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.CellType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Define a date column.
 */
public class DateColumn extends AbstractColumn {

    public static final String DEFAULT_DATE_STYLE = "date";

    static Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class, LocalDateTime.class, LocalDate.class);

    private final Class<?> type;

    protected DateColumn(Settings settings) {
        super(settings);
        this.type = settings.type();
    }

    public static class Settings extends AbstractColumn.BaseSettings<Settings> {

        private Class<?> type;

        @Override
        protected Settings self() {
            return this;
        }

        public Settings type(Class<?> type) {
            if (SUPPORTED_TYPES.contains(type)) {
                this.type = type;
            } else {
                throw new IllegalArgumentException("For a Date Cell only supported types are" + SUPPORTED_TYPES);
            }
            return this;
        }

        public Class<?> type() {
            return  Objects.requireNonNullElse(this.type, LocalDateTime.class);
        }

        public DateColumn make() {
            return new DateColumn(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<?> getType() {
        return type;
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

        Class<?> fromType = TypeUtil.classOf(schemaField.getType());
        Class<?> toType = getType();

        Function<?, ?> conversion;
        if (toType.isAssignableFrom(fromType)) {
            conversion = Function.identity();
        } else {
            conversion = conversionProvider.conversionFor(fromType, toType);
        }

        return (cell, data) -> {

            Object value = getter.get(data);

            if (value == null) {
                cell.setBlank();
                return;
            }

            //noinspection unchecked
            value = ((Function<Object, Object>) conversion).apply(value);

            if (value instanceof LocalDateTime) {
                cell.setCellValue((LocalDateTime) value);
            } else if (value instanceof LocalDate) {
                cell.setCellValue((LocalDate) value);
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else {
                throw DataException.of("Can't extract Date from " + value + " of " + value.getClass());
            }

        };
    }

    @Override
    public String getDefaultStyle() {
        return DEFAULT_DATE_STYLE;
    }

}
