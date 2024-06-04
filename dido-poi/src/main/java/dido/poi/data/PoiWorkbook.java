package dido.poi.data;

import dido.poi.BookInProvider;
import dido.poi.BookOutProvider;
import dido.poi.layouts.DataRows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import dido.poi.BookIn;
import dido.poi.BookOut;
import dido.poi.style.StyleProvider;
import dido.poi.style.StyleProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author rob
 * @oddjob.description A source or sink of data that is a Microsoft
 * Excel Spreadsheet.
 * <p>
 * @see DataRows
 */
public class PoiWorkbook implements BookInProvider, BookOutProvider {

    private static final Logger logger = LoggerFactory.getLogger(PoiWorkbook.class);

    /**
     * The workbook created or read.
     */
    private Workbook workbook;

    /**
     * @oddjob.property
     * @oddjob.description An input type (i.e. file) that is an Excel
     * Workbook.
     * @oddjob.required For reading yes but optional for writing.
     */
    private InputStream input;

    /**
     * @oddjob.property
     * @oddjob.description An output type (i.e. file) that is an Excel
     * Workbook.
     * @oddjob.required For writing yes, ignored for reading.
     */
    private OutputStream output;

    /**
     * @oddjob.property
     * @oddjob.description The version of Excel to create. EXCEL97 or
     * EXCEL2007.
     * @oddjob.required No. Default to EXCEL2007.
     */
    private SpreadsheetVersion version;


    public Workbook getWorkbook() {
        return workbook;
    }

    @Override
    public BookIn provideBookIn() throws IOException {

        return new PoiBookIn();
    }

    @Override
    public BookOut provideBookOut() throws IOException {

        return new RootPoiBookOut();
    }

    class PoiBookIn implements BookIn {

        private int sheet = 0;

        public PoiBookIn() throws IOException {

            if (input != null) {

                    workbook = WorkbookFactory.create(input);
                    logger.info("Read workbook from [" + input + "]");
                    input.close();
            }

            if (workbook == null) {
                throw new NullPointerException("No Input For Workbook.");
            }

        }

        @Override
        public Sheet getSheet(String sheetName) {
            if (sheetName == null) {
                if (workbook.getNumberOfSheets() > 0) {
                    return workbook.getSheetAt(0);
                } else {
                    return null;
                }
            } else {
                return workbook.getSheet(sheetName);
            }
        }

        @Override
        public Sheet nextSheet() {
            return workbook.getSheetAt(sheet++);
        }
    }

    class RootPoiBookOut implements BookOut {

        public RootPoiBookOut() throws IOException {

            if (input != null) {

                    workbook = WorkbookFactory.create(input);
                    input.close();
                logger.info("Read workbook from [" + input + "]");
            } else {
                logger.info("Created empty workbook.");

                if (version == null) {
                    version = SpreadsheetVersion.EXCEL2007;
                }

                switch (version) {
                    case EXCEL97:
                        workbook = new HSSFWorkbook();
                        break;
                    case EXCEL2007:
                        workbook = new XSSFWorkbook();
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported Version " + version);
                }
            }
        }

        @Override
        public Sheet getOrCreateSheet(String name) {
            if (name == null) {
                if (workbook.getNumberOfSheets() > 0) {
                    return workbook.getSheetAt(0);
                } else {
                    return workbook.createSheet();
                }
            } else {
                Sheet sheet = workbook.getSheet(name);
                if (sheet == null) {
                    return workbook.createSheet(name);
                } else {
                    return sheet;
                }
            }
        }

        @Override
        public void close() throws IOException {

            if (output != null) {

                    workbook.write(output);
                    output.close();

                logger.info("Wrote workbook of " + workbook.getNumberOfSheets() +
                        " sheet(s)");
            }
        }

        @Override
        public StyleProvider createStyles(StyleProviderFactory styleProviderFactory) {
            return styleProviderFactory.providerFor(workbook);
        }
    }


    public void setInput(InputStream input) {
        this.input = input;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public SpreadsheetVersion getVersion() {
        return version;
    }

    public void setVersion(SpreadsheetVersion version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
