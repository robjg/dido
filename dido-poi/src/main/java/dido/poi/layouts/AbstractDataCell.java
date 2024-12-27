package dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.ReadSchema;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import dido.poi.CellOut;
import dido.poi.columns.AbstractColumn;
import dido.poi.data.DataCell;
import dido.poi.data.PoiWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared implementation base class for cells.
 *
 * @author rob
 */
abstract public class AbstractDataCell implements DataCell {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataCell.class);

    /**
     * @oddjob.property
     * @oddjob.description The name of this layout. The name is the main
     * identification of a layout node. It is commonly used by bindings to
     * associate with the property name of a Java Object.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.property
     * @oddjob.description The name of the style to use. The style will have
     * been defined with the {@link PoiWorkbook} definition.
     * @oddjob.required No.
     */
    private String style;

    /**
     * @oddjob.property
     * @oddjob.description The 1 based column index of this layout.
     * @oddjob.required Read only.
     */
    private int index;

    /**
     * @oddjob.property
     * @oddjob.description The Excel reference of the last row of this
     * column that has been written.
     * @oddjob.required Read only.
     */
    private String reference;


    /**
     * @oddjob.property cellType
     * @oddjob.description The Excel type of this column.
     * @oddjob.required Read only.
     */
    abstract public CellType getCellType();


    /**
     * Setter for name.
     *
     * @param name The optional name which will be the field name.
     */
    public void setName(String name) {
        settings().name(name);
    }

    public String getName() {
        return settings().name();
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
     * @return The type that must be of Class&lt;T&gt;
     * @oddjob.property type
     * @oddjob.description The Java type of the column.
     * @oddjob.required Read only.
     */
    abstract public Class<?> getType();

    protected abstract AbstractColumn.BaseSettings<?> settings();

    @Override
    public CellIn provideCellIn(int columnIndex, DataSchema schema, DidoConversionProvider conversionProvider) {

        return settings()
                .make()
                .provideCellIn(columnIndex, schema, conversionProvider);
    }

    @Override
    public CellOut provideCellOut(ReadSchema schema, int index, DidoConversionProvider conversionProvider) {

        return settings()
                .make()
                .provideCellOut(schema, index, conversionProvider);
    }

    public String getReference() {
        return reference;
    }

    public int getIndex() {
        return settings().index();
    }

    public void setIndex(int column) {
        settings().index(column);
    }

    public String getStyle() {
        return settings().style();
    }

    public void setStyle(String style) {
        settings().style(style);
    }

    public String toString() {
        if (name == null) {
            return getClass().getSimpleName();
        } else {
            return getClass().getSimpleName() + ", name=" + name;
        }
    }
}
