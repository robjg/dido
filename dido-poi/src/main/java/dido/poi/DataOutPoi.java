package dido.poi;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.ReadSchema;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.how.util.IoUtil;
import dido.poi.data.DataCell;
import dido.poi.data.PoiRowsOut;
import dido.poi.data.PoiWorkbook;
import dido.poi.layouts.DataCellFactory;
import dido.poi.style.CompositeStyleFactory;
import dido.poi.style.DefaultStyleProivderFactory;
import dido.poi.style.StyleProvider;
import dido.poi.style.StyleProviderFactory;
import dido.poi.utils.SchemaAndCells;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

public class DataOutPoi implements DataOutHow<BookOutProvider> {

    private static final Logger logger = LoggerFactory.getLogger(DataInPoi.class);

    /**
     * The starting row in the sheet. Defaults to 1.
     */
    private final int firstRow;

    /**
     * The starting column in the sheet. Defaults to 1.
     */
    private final int firstColumn;

    /**
     * Is there a header row. Defaults to false.
     */
    private final boolean withHeader;

    /**
     * The style name used in the header row.
     */
    private final String headingsStyle;

    /**
     * The name of the sheet to read from. If the sheet doesn't exist in the
     * workbook then no data will be read.
     */
    private final String sheetName;

    /**
     * If true then automatically set the width of the column to the
     * widest column value when values have finished being written.
     */
    private final boolean autoWidth;

    /**
     * If true then automatically set an auto filter on
     * the column.
     */
    private final boolean autoFilter;

    /**
     * Provides conversions.
     */
    private final DidoConversionProvider conversionProvider;

    /**
     * Allow a number of named styles to be set.
     * @oddjob.required No.
     */
    private final StyleProviderFactory styles;

    private final List<DataCell> columns;

    private final DataSchema schema;

    private final IntConsumer lastRow;

    private DataOutPoi(Settings settings) {
        this.firstRow = Math.max(settings.firstRow, 1);
        this.firstColumn = Math.max(settings.firstColumn, 1);
        this.sheetName = settings.sheetName;
        this.schema = settings.schema;
        this.withHeader = settings.withHeader;
        this.headingsStyle = settings.headerStyle;
        this.autoFilter = settings.autoFilter;
        this.autoWidth = settings.autoWidth;
        this.styles = settings.styles;
        this.conversionProvider = settings.converter;
        this.columns = settings.columns == null || settings.columns.isEmpty()
                ? null : new ArrayList<>(settings.columns);
        this.lastRow = Objects.requireNonNullElse(settings.lastRow, ignored -> {});
    }

    public static class Settings {

        /**
         * The starting row in the sheet. Defaults to 1.
         */
        private int firstRow = 1;

        /**
         * The starting column in the sheet. Defaults to 1.
         */
        private int firstColumn = 1;

        /**
         * Is there a header row. Defaults to false.
         */
        private boolean withHeader;

        /**
         * The style name used in the header row.
         */
        private String headerStyle;

        /**
         * The name of the sheet to read from. If the sheet doesn't exist in the
         * workbook then no data will be read.
         */
        private String sheetName;

        /**
         * If true then automatically set the width of the column to the
         * widest column value when values have finished being written.
         */
        private boolean autoWidth;

        /**
         * If true then automatically set an auto filter on
         * the column.
         */
        private boolean autoFilter;

        private DataSchema schema;

        private DidoConversionProvider converter;

        private StyleProviderFactory styles;

        private Collection<? extends DataCell> columns;

        private IntConsumer lastRow;

        public Settings sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Settings firstRow(int firstRow) {
            this.firstRow = firstRow;
            return this;
        }

        public Settings firstColumn(int firstColumn) {
            this.firstColumn = firstColumn;
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings header(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

        public Settings headerStyle(String headerStyle) {
            this.headerStyle = headerStyle;
            return this;
        }

        public Settings autoFilter(boolean autoFilter) {
            this.autoFilter = autoFilter;
            return this;
        }

        public Settings autoWidth(boolean autoWidth) {
            this.autoWidth = autoWidth;
            return this;
        }

        public Settings converter(DidoConversionProvider converter) {
            this.converter = converter;
            return this;
        }

        public Settings styles(StyleProviderFactory styles) {
            this.styles = styles;
            return this;
        }

        public Settings columns(Collection<? extends DataCell> columns) {
            this.columns = columns;
            return this;
        }

        public Settings lastRow(IntConsumer lastRow) {
            this.lastRow = lastRow;
            return this;
        }

        public DataOut toAppendable(Appendable appendable) {
            return toWriter(IoUtil.writerFromAppendable(appendable));
        }

        public DataOut toWriter(Writer writer) {
            try {
                return toOutputStream(WriterOutputStream.builder().setWriter(writer).get());
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataOut toPath(Path path) {
            try {
                return toOutputStream(Files.newOutputStream(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataOut toOutputStream(OutputStream outputStream) {

            PoiWorkbook workbook = new PoiWorkbook();
            workbook.setOutput(outputStream);
            return toWorkbook(workbook);
        }

        public DataOut toWorkbook(BookOutProvider workbook) {

            return make().outTo(workbook);
        }

        public DataOutPoi make() {
            return new DataOutPoi(this);
        }

    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<BookOutProvider> getOutType() {
        return BookOutProvider.class;
    }

    @Override
    public DataOut outTo(BookOutProvider outTo) {

        BookOut bookOut = outTo.provideBookOut();

        Sheet sheet = bookOut.getOrCreateSheet(this.sheetName);

        StyleProvider styleProvider = bookOut.createStyles(
                this.styles == null
                ? new DefaultStyleProivderFactory()
                : new CompositeStyleFactory(styles, new DefaultStyleProivderFactory()));

        PoiRowsOut rowsOut = new PoiRowsOut(sheet,
                styleProvider,
                firstRow,
                firstColumn);

        logger.debug("Creating writer for [{}]", rowsOut);

        WriterFactory writerFactory = new WriterFactory(rowsOut, bookOut::close);

        if (this.schema == null) {
            if (this.columns == null) {
                return new UnknownWriter(writerFactory,
                        SchemaAndCells.withCellFactory(new DataCellFactory()));
            } else {
                return new UnknownWriter(writerFactory,
                        SchemaAndCells.withCells(this.columns));
            }
        } else {
            if (this.columns == null) {
                return writerFactory.create(SchemaAndCells.withCellFactory(
                        new DataCellFactory()).fromSchema(this.schema));
            } else {
                return writerFactory.create(SchemaAndCells.withCells(
                        this.columns).fromSchema(this.schema));
            }
        }
    }

    class MainWriter implements DataOut {

        private final RowsOut rowsOut;

        private final Collection<CellOut> cellOuts;

        private final Runnable closeHandler;

        public MainWriter(RowsOut rowsOut,
                          Collection<CellOut> cellOuts,
                          Runnable closeHandler) {
            this.rowsOut = rowsOut;
            this.cellOuts = cellOuts;
            this.closeHandler = closeHandler;
        }

        @Override
        public void accept(DidoData data) {

            rowsOut.nextRow();

            logger.trace("[{}] writing row {}", this, rowsOut.getLastRow());

            RowOut rowOut = rowsOut.getRowOut();

            for (CellOut dataCell : cellOuts) {
                dataCell.setValue(rowOut, data);
            }
        }

        @Override
        public void close() {

            if (autoFilter) {
                rowsOut.autoFilter();
            }

            if (autoWidth) {
                rowsOut.autoWidth();
            }

            closeHandler.run();

            int lastRow = rowsOut.getLastRow();
            DataOutPoi.this.lastRow.accept(lastRow);

            logger.debug("[{}] closed writer at row [{}]", this, lastRow);
        }
    }

    class UnknownWriter implements DataOut {

        private final WriterFactory writerFactory;

        private final SchemaAndCells.Factory<dido.poi.data.DataCell> cellFactory;

        private DataOut delegate;

        UnknownWriter(WriterFactory writerFactory,
                      SchemaAndCells.Factory<dido.poi.data.DataCell> cellFactory) {
            this.writerFactory = writerFactory;
            this.cellFactory = cellFactory;
        }

        @Override
        public void accept(DidoData data) {

            if (delegate == null) {
                SchemaAndCells<dido.poi.data.DataCell> schemaAndCells = cellFactory.fromSchema(data.getSchema());
                delegate = writerFactory.create(schemaAndCells);
            }
            delegate.accept(data);
        }

        @Override
        public void close() {
            if (delegate == null)  {
                delegate = writerFactory.create(cellFactory.noData());
            }

            delegate.close();
        }
    }

    class WriterFactory {

        private final RowsOut rowsOut;
        private final Runnable closeHandler;

        WriterFactory(RowsOut rowsOut, Runnable closeHandler) {
            this.rowsOut = rowsOut;
            this.closeHandler = closeHandler;
        }

        DataOut create(SchemaAndCells<? extends CellOutProvider> schemaAndCells) {

            DidoConversionProvider conversionProvider = Objects.requireNonNullElseGet(
                    DataOutPoi.this.conversionProvider, DefaultConversionProvider::defaultInstance);

            ReadSchema schema = ReadSchema.from(schemaAndCells.getSchema());
            List<CellOut> cellOuts = new ArrayList<>(schemaAndCells.getDataCells().size());
            int lastIndex = 0;
            for (CellOutProvider cellProvider : schemaAndCells.getDataCells()) {

                if (cellProvider.getIndex() == 0) {
                    ++lastIndex;
                } else {
                    lastIndex = cellProvider.getIndex();
                }


                cellOuts.add(cellProvider.provideCellOut(
                        schema,
                        lastIndex,
                        conversionProvider));
            }

            if (withHeader) {
                HeaderRowOut headerRowOut = rowsOut.headerRow(headingsStyle);

                cellOuts.forEach(cs -> cs.writeHeader(headerRowOut));
            }

            logger.debug("[{}] initialised at [{}, {}]", this, firstRow, firstColumn);

            return new MainWriter(rowsOut, cellOuts, closeHandler);
        }
    }

}
