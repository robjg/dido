package dido.poi;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.SchemaListener;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.data.DataCell;
import dido.poi.data.PoiRowsIn;
import dido.poi.data.PoiWorkbook;
import dido.poi.layouts.DataCellFactory;
import dido.poi.utils.DataRowFactory;
import dido.poi.utils.SchemaAndCells;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * How to read Excel Data In.
 */
public class DataInPoi implements DataInHow<BookInProvider> {

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
     * The name of the sheet to read from. If the sheet doesn't exist in the
     * workbook then no data will be read.
     */
    private final String sheetName;

    /**
     * Provides conversions.
     */
    private final DidoConversionProvider conversionProvider;

    private final DataSchema schema;

    private final boolean partialSchema;

    private final List<DataCell> columns;

    private final SchemaListener schemaListener;

    private DataInPoi(Settings settings) {
        this.firstRow = Math.max(settings.firstRow, 1);
        this.firstColumn = Math.max(settings.firstColumn, 1);
        this.sheetName = settings.sheetName;
        this.schema = settings.schema;
        this.partialSchema = settings.partialSchema;
        this.withHeader = settings.withHeader;
        this.conversionProvider = settings.conversionProvider;
        this.columns = settings.columns == null || settings.columns.isEmpty()
                ? null : new ArrayList<>(settings.columns);
        this.schemaListener = settings.schemaListener;
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
         * The name of the sheet to read from. If the sheet doesn't exist in the
         * workbook then no data will be read.
         */
        private String sheetName;

        private DataSchema schema;

        private boolean partialSchema;

        private DidoConversionProvider conversionProvider;

        private Collection<? extends DataCell> columns;

        private SchemaListener schemaListener;

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

        public Settings header(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings partialSchema(boolean partialSchema) {
            this.partialSchema = partialSchema;
            return this;
        }

        public Settings partialSchema(DataSchema schema) {
            this.schema = schema;
            this.partialSchema = true;
            return this;
        }

        public Settings conversionProvider(DidoConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
            return this;
        }

        public Settings columns(Collection<? extends DataCell> columns) {
            this.columns = columns;
            return this;
        }

        public Settings schemaListener(SchemaListener schemaListener) {
            this.schemaListener = schemaListener;
            return this;
        }

        public DataIn fromPath(Path path) {
            try {
                return fromInputStream(Files.newInputStream(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataIn fromInputStream(InputStream inputStream) {
            PoiWorkbook workbook = new PoiWorkbook();
            workbook.setInput(inputStream);
            return make().inFrom(workbook);
        }

        public DataIn fromReader(Reader reader) {

            try {
                return fromInputStream(ReaderInputStream.builder().setReader(reader).get());
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataInPoi make() {

            return new DataInPoi(this);
        }
    }

    public static DataIn fromPath(Path path) {

        return with().fromPath(path);
    }

    public static DataIn fromReader(Reader reader) {

        return with().fromReader(reader);
    }

    public static DataIn fromInputStream(InputStream inputStream) {

        return with().fromInputStream(inputStream);
    }


    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<BookInProvider> getInType() {
        return BookInProvider.class;
    }

    class MainReader implements DataIn {

        private final RowsIn rowsIn;

        private final DataRowFactory dataRowFactory;

        int lastRow;

        public MainReader(RowsIn rowsIn, DataRowFactory dataRowFactory) {
            this.rowsIn = rowsIn;
            this.dataRowFactory = dataRowFactory;
        }

        @Override
        public Iterator<DidoData> iterator() {

            return new Iterator<>() {

                RowIn rowIn = rowsIn.nextRow();

                @Override
                public boolean hasNext() {
                    return rowIn != null;
                }

                @Override
                public DidoData next() {
                    lastRow = rowsIn.getLastRow();

                    logger.debug("[{}] reading row {}", DataInPoi.this, rowsIn.getLastRow());

                    DidoData data = dataRowFactory.wrap(rowIn);

                    rowIn = rowsIn.nextRow();

                    return data;
                }
            };
        }

        @Override
        public void close() {

            logger.debug("[{}] closed reader at row [{}]", DataInPoi.this, lastRow);
        }
    }

    @Override
    public DataIn inFrom(BookInProvider bookInProvider) {

        BookIn bookIn = bookInProvider.provideBookIn();

        Sheet sheet = bookIn.getSheet(this.sheetName);

        RowsIn rowsIn = new PoiRowsIn(sheet, firstRow, firstColumn);

        String[] headings = null;

        if (withHeader) {

            headings = rowsIn.headerRow();
            if (headings == null) {
                throw new IllegalStateException("[" + this + "] No rows to provide reader from.");
            } else {
                logger.info("[{}] Read headings {}", this, Arrays.toString(headings));
            }
        } else {
            logger.debug("[{}] Providing reader with no headings.", this);
        }

        SchemaAndCells<DataCell> schemaAndCells;

        CellProviderFactory<DataCell> cellProviderFactory = new DataCellFactory();

        if (this.columns == null) {
            // We must get schema from the data
            if (this.schema == null || partialSchema) {
                schemaAndCells = SchemaAndCells.withCellFactory(cellProviderFactory)
                        .fromRowAndHeadings(rowsIn.peekRow(), headings,
                                this.schema);
            }
            else {
                // We get the columns from the schema
                schemaAndCells = SchemaAndCells.withCellFactory(cellProviderFactory)
                        .fromSchema(this.schema);
            }

        } else {
            if (this.schema == null || partialSchema) {
                schemaAndCells = SchemaAndCells.fromCells(this.columns, this.schema);
            }
            else {
                schemaAndCells = SchemaAndCells.bothKnown(this.schema, this.columns);
            }
        }

        if (schemaAndCells == null) {
            // No schema, no cells and no data.
            return DataIn.of();
        } else {
            if (schemaListener != null) {
                schemaListener.schemaAvailable(schemaAndCells.getSchema());
            }
            return new MainReader(rowsIn, DataRowFactory.newInstance(
                    schemaAndCells.getSchema(), schemaAndCells.getDataCells(),
                    Objects.requireNonNullElseGet(conversionProvider,
                            DefaultConversionProvider::defaultInstance)));
        }
    }


    @Override
    public String toString() {
        String name = sheetName;
        return getClass().getSimpleName() +
                (name == null ? "" : " " + name);
    }
}
