package dido.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;

import java.util.function.Supplier;

/**
 * @oddjob.description A wrapper around Apache's
 * <a href="https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html">CSVFormat</a>
 *
 */
public class CsvFormatType implements Supplier<CSVFormat> {

    public enum CSVFormatEnum {

        DEFAULT(CSVFormat.DEFAULT),
        EXCEL(CSVFormat.EXCEL),
        INFORMIX_UNLOAD(CSVFormat.INFORMIX_UNLOAD),
        INFORMIX_UNLOAD_CSV(CSVFormat.INFORMIX_UNLOAD_CSV),
        MONGODB_CSV(CSVFormat.MONGODB_CSV),
        MONGODB_TSV(CSVFormat.MONGODB_TSV),
        MYSQL(CSVFormat.MYSQL),
        ORACLE(CSVFormat.ORACLE),
        POSTGRESQL_CSV(CSVFormat.POSTGRESQL_CSV),
        POSTGRESQL_TEXT(CSVFormat.POSTGRESQL_TEXT),
        RFC4180(CSVFormat.RFC4180),
        TDF(CSVFormat.TDF)
        ;

        private final CSVFormat format;

        CSVFormatEnum(CSVFormat format) {
            this.format = format;
        }
    }

    private CSVFormatEnum formatFrom;

    private DuplicateHeaderMode duplicateHeaderMode;
    private boolean allowMissingColumnNames;
    private Character commentMarker;
    private String delimiter;
    private Character escapeCharacter;
    private String[] headerComments;
    private boolean ignoreEmptyLines;
    private boolean ignoreHeaderCase;
    private boolean ignoreSurroundingSpaces;
    private String nullString;
    private Character quoteCharacter;
    private QuoteMode quoteMode;
    private String recordSeparator;
    private boolean lenientEof;
    private boolean trailingData;
    private boolean trailingDelimiter;
    private boolean trim;
    private long maxRows;

    @Override
    public CSVFormat get() {
        CSVFormat format = formatFrom == null ? CSVFormat.DEFAULT : formatFrom.format;

        CSVFormat.Builder builder = format.builder();

        if (duplicateHeaderMode != null) {
            builder.setDuplicateHeaderMode(duplicateHeaderMode);
        }

        if (allowMissingColumnNames) {
            builder.setAllowMissingColumnNames(true);
        }

        if (commentMarker != null) {
            builder.setCommentMarker(commentMarker);
        }

        if (delimiter != null) {
            builder.setDelimiter(delimiter);
        }

        if (escapeCharacter != null) {
            builder.setEscape(escapeCharacter);
        }

        if (headerComments != null) {
            builder.setHeaderComments(headerComments);
        }

        if (ignoreEmptyLines) {
            builder.setIgnoreEmptyLines(true);
        }

        if (ignoreHeaderCase) {
           builder.setIgnoreHeaderCase(true);
        }

        if (ignoreSurroundingSpaces) {
            builder.setIgnoreSurroundingSpaces(true);
        }

        if (nullString != null) {
            builder.setNullString(nullString);
        }

        if (quoteCharacter != null) {
            builder.setQuote(quoteCharacter);
        }

        if (quoteMode != null) {
            builder.setQuoteMode(quoteMode);
        }

        if (recordSeparator != null) {
            builder.setRecordSeparator(recordSeparator);
        }

        if (lenientEof) {
            builder.setLenientEof(true);
        }

        if (trailingData) {
            builder.setTrailingData(true);
        }

        if (trailingDelimiter) {
            builder.setTrailingDelimiter(true);
        }

        if (trim) {
            builder.setTrim(true);
        }

        if (maxRows > 0) {
            builder.setMaxRows(maxRows);
        }

        return builder.get();
    }

    public void setFormatFrom(CSVFormatEnum formatFrom) {
        this.formatFrom = formatFrom;
    }

    public void setDuplicateHeaderMode(DuplicateHeaderMode duplicateHeaderMode) {
        this.duplicateHeaderMode = duplicateHeaderMode;
    }

    public void setAllowMissingColumnNames(boolean allowMissingColumnNames) {
        this.allowMissingColumnNames = allowMissingColumnNames;
    }

    public void setCommentMarker(Character commentMarker) {
        this.commentMarker = commentMarker;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setEscapeCharacter(Character escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public void setHeaderComments(String[] headerComments) {
        this.headerComments = headerComments;
    }

    public void setIgnoreEmptyLines(boolean ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    public void setIgnoreHeaderCase(boolean ignoreHeaderCase) {
        this.ignoreHeaderCase = ignoreHeaderCase;
    }

    public void setIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public void setQuoteCharacter(Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    public void setQuoteMode(QuoteMode quoteMode) {
        this.quoteMode = quoteMode;
    }

    public void setRecordSeparator(String recordSeparator) {
        this.recordSeparator = recordSeparator;
    }

    public void setLenientEof(boolean lenientEof) {
        this.lenientEof = lenientEof;
    }

    public void setTrailingData(boolean trailingData) {
        this.trailingData = trailingData;
    }

    public void setTrailingDelimiter(boolean trailingDelimiter) {
        this.trailingDelimiter = trailingDelimiter;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setMaxRows(long maxRows) {
        this.maxRows = maxRows;
    }
}
