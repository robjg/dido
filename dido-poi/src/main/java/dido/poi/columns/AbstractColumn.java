package dido.poi.columns;

import dido.data.*;
import dido.data.useful.AbstractFieldGetter;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.*;
import dido.poi.data.DataCell;
import dido.poi.data.PoiWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Shared implementation base class for columns.
 *
 * @author rob
 */
abstract public class AbstractColumn implements DataCell {

    /**
     * The name of this column.
     */
    private final String name;

    /**
     *The name of the style to use when this column is written out. The style will have
     * been defined with the {@link PoiWorkbook} definition.
     */
    private final String style;

    /**
     * The 1 based column index of this layout.
     */
    private final int index;

    /**
     * The Excel reference of the last row of this
     * column that has been written.
     */
    private String reference;

    protected AbstractColumn(BaseSettings<?> settings) {
        this.name = settings.name;
        this.style = settings.style;
        this.index = settings.index;
    }

    public abstract static class BaseSettings<B extends BaseSettings<B>>  {

        private String name;

        private String style;

        private int index;

        public B name(String name) {
            this.name = name;
            return self();
        }


        public String name() {
            return name;
        }

        public B style(String style) {
            this.style = style;
            return self();
        }

        public String style() {
            return style;
        }

        public B index(int index) {
            this.index = index;
            return self();
        }

        public int index() {
            return index;
        }

        protected abstract B self();

        public abstract AbstractColumn make();
    }

    @Override
    public CellIn provideCellIn(int columnIndex,
                                DataSchema schema,
                                DidoConversionProvider conversionProvider) {

        SchemaField schemaField = Objects.requireNonNull(schema.getSchemaFieldAt(columnIndex),
                "Programmer error: Schema does not match cells at index [" + columnIndex + "]" +
                        ", schema is: " + schema);

        FieldGetter fieldGetter = getterFor(schemaField, conversionProvider);

        return capture -> capture.accept(schemaField, fieldGetter);
    }

    abstract protected FieldGetter getterFor(SchemaField schemaField,
                                             DidoConversionProvider conversionProvider);

    protected abstract static class AbstractCellGetter extends AbstractFieldGetter {

        protected final SchemaField schemaField;

        protected final int index;

        AbstractCellGetter(SchemaField schemaField) {
            this.schemaField = schemaField;
            this.index = schemaField.getIndex();
        }

        protected Cell getCell(DidoData data) {
            return ((DataRow) data).getRowIn().getCell(index);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " for " + schemaField;
        }
    }

    static class CellGetterWithConversion<T, R>  extends AbstractFieldGetter {

        private final AbstractCellGetter getter;

        private final Function<T, R> conversion;

        CellGetterWithConversion(AbstractCellGetter getter,
                                 Function<T, R> conversion) {
            this.getter = getter;
            this.conversion = conversion;
        }

        @Override
        public Object get(DidoData data) {
            try {
                //noinspection unchecked
                return conversion.apply((T) getter.get(data));
            }
            catch (RuntimeException e) {
                throw DataException.of(String.format("Failed to get value for %s", this), e);
            }
        }

        @Override
        public String toString() {
            return getter.toString() + " with conversion";
        }
    }


    @Override
    public CellOut provideCellOut(ReadSchema schema,
                                  int index,
                                  DidoConversionProvider conversionProvider) {

        String header = Objects.requireNonNullElse(getName(), "Unnamed");
        String cellName = getName();

        SchemaField schemaField;
        FieldGetter fieldGetter;
        if (getCellType() == CellType.FORMULA) {
            schemaField = null;
            fieldGetter = null;
        } else if (cellName != null && schema.hasNamed(cellName)) {
            schemaField = schema.getSchemaFieldNamed(cellName);
            fieldGetter = schema.getFieldGetterNamed(cellName);
        } else {
            schemaField = schema.getSchemaFieldAt(index);
            fieldGetter = schema.getFieldGetterAt(index);
        }

        Injector injector = injectorFor(schemaField, fieldGetter, conversionProvider);

        return new CellOut() {

            @Override
            public void writeHeader(HeaderRowOut headerRowOut) {
                headerRowOut.setHeader(index, header);
            }

            @Override
            public void setValue(RowOut rowOut, DidoData data) {

                Cell cell = rowOut.getCell(index, getCellType());

                String style = Optional.ofNullable(getStyle()).orElse(getDefaultStyle());

                if (style != null) {
                    CellStyle cellStyle = rowOut.styleFor(style);

                    if (cellStyle == null) {
                        throw new IllegalArgumentException("No style available of name [" +
                                style + "] from cell [" + this + "]");
                    }

                    cell.setCellStyle(cellStyle);
                }

                injector.insertValueInto(cell, data);

                reference = cell.getAddress().formatAsR1C1String();
            }
        };
    }

    protected abstract Injector injectorFor(SchemaField schemaField,
                                            FieldGetter getter,
                                            DidoConversionProvider conversionProvider);

    protected interface Injector {

        /**
         * Write a value into the cell.
         *
         * @param cell The Excel Cell.
         * @param data The data. May be null.
         */
        void insertValueInto(Cell cell, DidoData data);
    }


    /**
     * @oddjob.property cellType
     * @oddjob.description The Excel type of this column.
     * @oddjob.required Read only.
     */
    abstract public CellType getCellType();


    public String getName() {
        return name;
    }

    /**
     * @return The default style.
     * @oddjob.property defaultStyle
     * @oddjob.description The default style for the cell. This
     * maybe overridden at the {@link PoiWorkbook} level.
     * @oddjob.required Read only.
     */
    public String getDefaultStyle() {
        return null;
    }

    /**
     * The Java type of the column.
     */
    abstract public Class<?> getType();

    public String getReference() {
        return reference;
    }

    public int getIndex() {
        return index;
    }

    public String getStyle() {
        return style;
    }

    public String toString() {
        if (name == null) {
            return getClass().getSimpleName();
        } else {
            return getClass().getSimpleName() + ", name=" + name;
        }
    }
}
