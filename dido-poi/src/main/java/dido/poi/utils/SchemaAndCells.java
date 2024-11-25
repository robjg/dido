package dido.poi.utils;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaField;
import dido.how.util.Primitives;
import dido.poi.RowIn;
import dido.poi.data.DataCell;
import dido.poi.layouts.*;
import org.apache.poi.ss.usermodel.Cell;

import java.util.*;

/**
 * Provides a mapping between {@link DataCell}s and a {@link DataSchema}. It creates one from the other
 * and so either may be provided in the factory method {@link #fromSchemaOrCells(DataSchema, Collection)}.
 */
public class SchemaAndCells {

    /**
     * Taken from {@link org.apache.poi.ss.usermodel.BuiltinFormats}
     */
    private static final Set<Short> DATE_FORMATS = Set.of(
            (short) 0xe, // "m/ d/ yy"
            (short) 0xf, // "d-mmm-yy"
            (short) 0x10, // "d-mmm"
            (short) 0x11, // "mmm-yy"
            (short) 0x12, // "h:mm AM/ PM"
            (short) 0x13, // "h:mm:ss AM/ PM"
            (short) 0x14, // "h:mm"
            (short) 0x15, // "h:mm:ss",
            (short) 0x16 // "m/ d/ yy h:mm"
    );

    private final DataSchema schema;

    private final Collection<? extends DataCell<?>> dataCells;

    private SchemaAndCells(DataSchema schema, Collection<? extends DataCell<?>> dataCells) {
        this.schema = schema;
        this.dataCells = dataCells;
    }

    public static SchemaAndCells fromCells(Collection<? extends DataCell<?>> dataCells) {
        return new SchemaAndCells(morphOf(dataCells), dataCells);
    }

    public static SchemaAndCells fromSchema(DataSchema schema) {
        return new SchemaAndCells(schema, morphInto(schema));
    }

    public static SchemaAndCells fromSchemaOrCells(DataSchema schema, Collection<? extends DataCell<?>> dataCells) {

        if (schema == null) {
            if (dataCells == null || dataCells.isEmpty()) {
                return null;
            } else {
                return fromCells(dataCells);
            }
        } else {
            if (dataCells == null || dataCells.isEmpty()) {
                return fromSchema(schema);
            } else {
                throw new IllegalStateException("Only Schema or Cells should be provided");
            }
        }
    }

    public static SchemaAndCells fromRowAndHeadings(RowIn rowIn, String[] headings) {
        return fromRowAndHeadings(rowIn, headings, null);
    }

    public static SchemaAndCells fromRowAndHeadings(RowIn rowIn,
                                                    String[] headings,
                                                    DataSchema partialSchema) {

        if (rowIn == null) {
            return null;
        }

        if (partialSchema == null) {
            partialSchema = DataSchema.emptySchema();
        }

        List<AbstractDataCell<?>> cells = new LinkedList<>();

        for (int index = 1; headings == null || index <= headings.length; ++index) {

            String heading = headings == null ? null : headings[index - 1];

            Cell cell = rowIn.getCell(index);
            if (cell == null) {
                break;
            }
            AbstractDataCell<?> dataCell;

            // See if our partial schema has a definition for this column either by
            // column name or index.
            SchemaField schemaField = null;
            if (heading != null) {
                schemaField = partialSchema.getSchemaFieldNamed(heading);
            }
            if (schemaField == null) {
                schemaField = partialSchema.getSchemaFieldAt(index);
            } else {
                schemaField = schemaField.mapToIndex(index);
            }

            if (schemaField == null) {

                switch (cell.getCellType()) {
                    case STRING:
                    case ERROR:
                        dataCell = new TextCell();
                        break;
                    case BOOLEAN:
                        dataCell = new BooleanCell();
                        break;
                    case NUMERIC:
                        if (DATE_FORMATS.contains(cell.getCellStyle().getDataFormat())) {
                            dataCell = new DateCell();
                        } else {
                            dataCell = new NumericCell<>();
                        }
                        break;
                    case FORMULA:
                        dataCell = new NumericFormulaCell();
                        break;
                    case BLANK:
                    default:
                        dataCell = new BlankCell();
                        break;
                }

                dataCell.setName(heading);
            } else {

                dataCell = createCell(schemaField);
            }

            cells.add(dataCell);
        }

        return SchemaAndCells.fromSchemaOrCells(null, cells);
    }

    static protected List<DataCell<?>> morphInto(DataSchema schema) {

        List<DataCell<?>> cells = new ArrayList<>(schema.lastIndex());

        for (SchemaField schemaField : schema.getSchemaFields()) {

            AbstractDataCell<?> cell = createCell(schemaField);

            cells.add(cell);
        }

        return cells;
    }

    @SuppressWarnings("unchecked")
    static <T> AbstractDataCell<T> createCell(SchemaField schemaField) {

        Class<?> type = Primitives.wrap(schemaField.getType());

        AbstractDataCell<?> cell;

        if (Number.class.isAssignableFrom(type)) {
            cell = createNumericCell((Class<? extends Number>) type);
        } else if (Boolean.class == type) {
            cell = new BooleanCell();
        } else if (Date.class.isAssignableFrom(type)) {
            cell = new DateCell();
        } else {
            cell = new TextCell();
        }

        cell.setName(schemaField.getName());
        cell.setIndex(schemaField.getIndex());

        return (AbstractDataCell<T>) cell;
    }

    static <T extends Number> AbstractDataCell<T> createNumericCell(Class<T> type) {
        NumericCell<T> numericCell = new NumericCell<>();
        numericCell.setType(type);
        return numericCell;
    }


    static protected DataSchema morphOf(Collection<? extends DataCell<?>> cells) {

        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        for (DataCell<?> child : cells) {

            String name = child.getName();

            Class<?> type = child.getType();

            schemaBuilder.addNamedAt(child.getIndex(), name, type);
        }

        return schemaBuilder.build();
    }

    public DataSchema getSchema() {
        return schema;
    }

    public Collection<? extends DataCell<?>> getDataCells() {
        return dataCells;
    }
}
