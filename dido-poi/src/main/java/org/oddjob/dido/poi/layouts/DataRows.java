package org.oddjob.dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import dido.poi.CellOut;
import dido.poi.CellOutProvider;
import dido.poi.utils.DataRowFactory;
import dido.poi.utils.SchemaAndCells;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.poi.*;
import org.oddjob.dido.poi.data.DataCell;
import org.oddjob.dido.poi.data.PoiRowsIn;
import org.oddjob.dido.poi.data.PoiRowsOut;
import org.oddjob.dido.poi.style.CompositeStyleFactory;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleFactoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @oddjob.description Define an area in a spreadsheet sheet for reading
 * and writing rows to.
 *
 * @author rob
 */
public class DataRows implements DataInHow<String, BookInProvider>, DataOutHow<String, BookOutProvider> {

    private static final Logger logger = LoggerFactory.getLogger(DataRows.class);

    /**
     * @oddjob.property
     * @oddjob.description The starting row in the sheet.
     * @oddjob.required No. Defaults to 1.
     */
    private int firstRow = 1;

    /**
     * @oddjob.property
     * @oddjob.description The starting column in the sheet.
     * @oddjob.required No. Defaults to 1.
     */
    private int firstColumn = 1;

    /**
     * @oddjob.property
     * @oddjob.description The last row in the sheet written to by this
     * layout.
     * @oddjob.required Read Only.
     */
    private int lastRow;

    /**
     * @oddjob.property
     * @oddjob.description Should a header row be written (true/false).
     * @oddjob.required No. Defaults to false.
     */
    private boolean withHeader;

    /**
     * @oddjob.property
     * @oddjob.description The style name used in the header row.
     * @oddjob.required No. A default header style will be used.
     */
    private String headingsStyle;

    /**
     * @oddjob.property
     * @oddjob.description If true then automatically set the width of the column to the
     * widest column value when values have finished being written.
     * @oddjob.required No. Defaults to false.
     */
    private boolean autoWidth;

    /**
     * @oddjob.property
     * @oddjob.description If true then automatically set an auto filter on
     * the column.
     * @oddjob.required No. Defaults to false.
     */
    private boolean autoFilter;

    /**
     * @oddjob.property
     * @oddjob.description Set when reading if there is a header row to
     * read headings from. Used to derive a binding type if one is required
     * and there an no child layouts to derive it from.
     * @oddjob.required No. Read only.
     */
    private String[] headings;

    /**
     * @oddjob.property
     * @oddjob.description The name of the sheet to read or write. When
     * reading if a name is given and the sheet doesn't exist in the
     * workbook then no data will be read.
     * @oddjob.required No. If not supplied the next sheet is used.
     */
    private String sheetName;

    /**
     * @oddjob.property
     * @oddjob.description Allow a number of named styles to be set for
     * the book. See {@link org.oddjob.dido.poi.style.StyleBean}.
     * @oddjob.required No.
     */
    private final StyleFactoryRegistry styles = new StyleFactoryRegistry();

    private final List<DataCell<?>> of = new LinkedList<>();

    private DataSchema<String> schema;

    @Override
    public Class<BookInProvider> getInType() {
        return BookInProvider.class;
    }

    @Override
    public Class<BookOutProvider> getOutType() {
        return BookOutProvider.class;
    }

    /**
     * @param index 0 based index.
     * @param child The child, null will remove the child for the given index.
     * @oddjob.property of
     * @oddjob.description The child layouts for the rows. These define
     * the columns created. Typically they will be one of {@link TextCell},
     * {@link NumericCell}, {@link DateCell}, {@link BooleanCell},
     * or {@link NumericFormulaCell}.
     * @oddjob.required No, they can be generated by a binding.
     */
    public <T> void setOf(int index, DataCell<T> child) {

        if (child == null) {
            of.remove(index);
        } else {
            of.add(index, child);
        }
    }


    class MainReader implements DataIn<String> {

        private final RowsIn rowsIn;

        private final DataRowFactory<String> dataRowFactory;

        public MainReader(RowsIn rowsIn, DataRowFactory<String> dataRowFactory) {
            this.rowsIn = rowsIn;
            this.dataRowFactory = dataRowFactory;
        }

        @Override
        public GenericData<String> get() {

            RowIn rowIn = rowsIn.nextRow();
            if (rowIn == null) {
                return null;
            }

            lastRow = rowsIn.getLastRow();

            logger.debug("[" + DataRows.this + "] reading row " +
                    rowsIn.getLastRow());

            return dataRowFactory.wrap(rowIn);
        }

        @Override
        public void close() {

            logger.debug("[" + DataRows.this +
                    "] closed reader at row [" + lastRow +
                    "]");
        }
    }

    @Override
    public DataIn<String> inFrom(BookInProvider bookInProvider) throws Exception {

        BookIn bookIn = bookInProvider.provideBookIn();

        Sheet sheet = bookIn.getSheet(this.sheetName);

        RowsIn rowsIn = new PoiRowsIn(sheet, firstRow, firstColumn);

        if (withHeader) {

            this.headings = rowsIn.headerRow();
            if (headings == null) {
                throw new IllegalStateException("[" + this + "] No rows to provide reader from.");
            } else {
                logger.info("[" + this + "] Read headings " +
                        Arrays.toString(this.headings));
            }
        } else {
            logger.debug("[" + this + "] Providing reader with no headings.");
        }

        SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(this.schema, this.of);

        if (schemaAndCells == null) {
            schemaAndCells = SchemaAndCells.fromRowAndHeadings(rowsIn.peekRow(), headings);
        }

        if (schemaAndCells == null) {
            return DataIn.empty();
        }
        else {
            return new MainReader(rowsIn, DataRowFactory.newInstance(
                    schemaAndCells.getSchema(), schemaAndCells.getDataCells()));
        }
    }

    class MainWriter implements DataOut<String> {

        private final RowsOut rowsOut;

        private final Collection<CellOut<?>> cellOuts;

        private final AutoCloseable closeable;

        public MainWriter(RowsOut rowsOut,
                          Collection<CellOut<?>> cellOuts,
                          AutoCloseable closeable) {
            this.rowsOut = rowsOut;
            this.cellOuts = cellOuts;
            this.closeable = closeable;
        }

        @Override
        public void accept(GenericData<String> data) {

            rowsOut.nextRow();

            logger.trace("[" + DataRows.this + "] writing row " +
                    rowsOut.getLastRow());

            for (CellOut<?> dataCell : cellOuts) {
                writeCell(dataCell, data);
            }

            lastRow = rowsOut.getLastRow();
        }

        <T> void writeCell(CellOut<T> dataSetter, GenericData<String> data) {

            RowOut cell = rowsOut.getRowOut();

            dataSetter.setValue(cell, data);
        }

        @Override
        public void close() throws Exception {

            if (autoFilter) {
                rowsOut.autoFilter();
            }

            if (autoWidth) {
                rowsOut.autoWidth();
            }

            closeable.close();

            logger.debug("[" + DataRows.this +
                    "] closed writer at row [" + lastRow +
                    "]");
        }
    }

    @Override
    public DataOut<String> outTo(BookOutProvider bookOutProvider) throws Exception {

        BookOut bookOut = bookOutProvider.provideBookOut();

        Sheet sheet = bookOut.getOrCreateSheet(this.sheetName);

        PoiRowsOut rowsOut = new PoiRowsOut(sheet,
                bookOut.createStyles(new CompositeStyleFactory(new DefaultStyleProivderFactory(), styles)),
                firstRow,
                firstColumn);

        logger.debug("Creating writer for [" + rowsOut + "]");

        SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(this.schema, this.of);

        WriterFactory writerFactory = new WriterFactory(rowsOut, bookOut);

        if (schemaAndCells == null) {
            return new UnknownWriter(writerFactory);
        }
        else {
            return writerFactory.create(schemaAndCells);
        }
    }

    class UnknownWriter implements DataOut<String> {

        private final WriterFactory writerFactory;

        private DataOut<String> delegate;

        UnknownWriter(WriterFactory writerFactory) {
            this.writerFactory = writerFactory;
        }

        @Override
        public void accept(GenericData<String> data) {

            if (delegate == null) {
                SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(data.getSchema(), null);
                delegate = writerFactory.create(schemaAndCells);
            }
            delegate.accept(data);
        }

        @Override
        public void close() throws Exception {
            if (delegate != null) {
                delegate.close();
            }
        }
    }

    class WriterFactory {

        private final RowsOut rowsOut;
        private final AutoCloseable closeable;

        WriterFactory(RowsOut rowsOut, AutoCloseable closeable) {
            this.rowsOut = rowsOut;
            this.closeable = closeable;
        }

        DataOut<String> create(SchemaAndCells schemaAndCells) {

            List<CellOut<?>> cellOuts = new ArrayList<>(schemaAndCells.getDataCells().size());
            int lastIndex = 0;
            for (CellOutProvider<?> cellProvider : schemaAndCells.getDataCells()) {

                if (cellProvider.getIndex() == 0) {
                    ++lastIndex;
                } else {
                    lastIndex = cellProvider.getIndex();
                }

                cellOuts.add(cellProvider.provideCellOut(lastIndex));
            }

            if (withHeader) {
                HeaderRowOut headerRowOut = rowsOut.headerRow(headingsStyle);

                cellOuts.forEach(cs -> cs.writeHeader(headerRowOut));

            }

            logger.debug("[" + this + "] initialised at [" +
                    firstRow + ", " + firstColumn + "]");

            return new MainWriter(rowsOut, cellOuts, closeable);
        }
    }


    public boolean isWithHeader() {
        return withHeader;
    }

    public void setWithHeader(boolean withHeading) {
        this.withHeader = withHeading;
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
    }

    public String getHeadingsStyle() {
        return headingsStyle;
    }

    public void setHeadingsStyle(String headingsStyle) {
        this.headingsStyle = headingsStyle;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int startRow) {
        this.firstRow = startRow;
    }

    public int getFirstColumn() {
        return firstColumn;
    }

    public void setFirstColumn(int startColumn) {
        this.firstColumn = startColumn;
    }

    public boolean isAutoFilter() {
        return autoFilter;
    }

    public void setAutoFilter(boolean autoFilter) {
        this.autoFilter = autoFilter;
    }

    public int getLastRow() {
        return lastRow;
    }

    public String[] getHeadings() {
        return headings;
    }

    /**
     * Getter for sheet name.
     *
     * @return
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * Setter for sheet name.
     *
     * @param sheetName
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * Setter for mapped styles.
     *
     * @param styleName The name of the styles.
     * @param styleBean The style bean. If null then the style of the
     *                  given name will be removed.
     */
    public void setStyles(String styleName, StyleBean styleBean) {
        if (styleBean == null) {
            styles.removeStyle(styleName);
        } else {
            styles.registerStyle(styleName, styleBean);
        }
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        String name = sheetName;
        return getClass().getSimpleName() +
                (name == null ? "" : " " + name);
    }
}
