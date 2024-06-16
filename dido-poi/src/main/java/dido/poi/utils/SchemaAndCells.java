package dido.poi.utils;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.how.util.Primitives;
import dido.poi.RowIn;
import dido.poi.data.DataCell;
import dido.poi.layouts.*;
import dido.poi.style.DefaultStyleProivderFactory;
import org.apache.poi.ss.usermodel.Cell;

import java.util.*;

public class SchemaAndCells {

    private final DataSchema schema;

    private final Collection<? extends DataCell<?>> dataCells;

    private SchemaAndCells(DataSchema schema, Collection<? extends DataCell<?>> dataCells) {
        this.schema = schema;
        this.dataCells = dataCells;
    }

    public static SchemaAndCells fromSchemaOrCells(DataSchema schema, Collection<? extends DataCell<?>> dataCells) {

        if (schema == null) {
            if (dataCells == null || dataCells.isEmpty()) {
                return null;
            }
            else {
                return new SchemaAndCells(morphOf(dataCells), dataCells);
            }
        }
        else {
            if (dataCells == null || dataCells.isEmpty()) {
                return new SchemaAndCells(schema, morphInto(schema));
            }
            else {
                throw new IllegalStateException("Only Schema or Cells should be provided");
            }
        }
    }

    public static SchemaAndCells fromRowAndHeadings(RowIn rowIn, String[] headings) {

        if (rowIn == null) {
            return null;
        }

        List<AbstractDataCell<?>> cells = new LinkedList<>();

        for (int i = 0; headings == null || i < headings.length; ++i) {

            Cell cell = rowIn.getCell(i + 1);
            if (cell == null) {
                break;
            }
            AbstractDataCell<?> dataCell;

            switch (cell.getCellType()) {
                case STRING:
                case ERROR:
                    dataCell = new TextCell();
                    break;
                case BOOLEAN:
                    dataCell = new BooleanCell();
                    break;
                case NUMERIC:
                    if (DefaultStyleProivderFactory.DATE_FORMAT.equals(
                            cell.getCellStyle().getDataFormatString())) {
                        dataCell = new DateCell();
                    }
                    else {
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
            if (headings != null) {
                dataCell.setName(headings[i]);
            }

            cells.add(dataCell);
        }

        return SchemaAndCells.fromSchemaOrCells(null, cells);
    }

    static protected List<DataCell<?>> morphInto(DataSchema schema) {

        List<DataCell<?>> cells = new ArrayList<>(schema.lastIndex());

        int i = 0;
        for (String property : schema.getFieldNames()) {

            Class<?> propertyType = Primitives.wrap(schema.getTypeNamed(property));

            AbstractDataCell<?> cell = createCell(propertyType);

            cell.setName(property);

            cells.add(cell);
        }

        return cells;
    }

    @SuppressWarnings("unchecked")
    static <T> AbstractDataCell<T> createCell(Class<T> type) {

        if (Number.class.isAssignableFrom(type)) {
            return (AbstractDataCell<T>) createNumericCell((Class<Number>) type);
        }
        else if (Boolean.class == type) {
            return (AbstractDataCell<T>) new BooleanCell();
        }
        else if (Date.class.isAssignableFrom(type)) {
            return (AbstractDataCell<T>) new DateCell();
        }
        else {
            return (AbstractDataCell<T>) new TextCell();
        }

    }

    static <T extends Number> AbstractDataCell<T> createNumericCell(Class<T> type) {
        NumericCell<T> numericCell = new NumericCell<>();
        numericCell.setType(type);
        return new NumericCell<>();
    }


    static protected DataSchema morphOf(Collection<? extends DataCell<?>> cells) {

        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        for (DataCell<?> child : cells) {

            String name = child.getName();

            Class<?> type = child.getType();

            schemaBuilder.addFieldAt(child.getIndex(), name, type);
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
