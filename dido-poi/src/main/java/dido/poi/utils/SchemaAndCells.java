package dido.poi.utils;

import dido.data.*;
import dido.poi.CellProvider;
import dido.poi.CellProviderFactory;
import dido.poi.RowIn;
import dido.poi.data.DataCell;
import org.apache.poi.ss.usermodel.Cell;

import java.util.*;

/**
 * Provides a mapping between {@link DataCell}s and a {@link DataSchema}. It creates one from the other.
 * Mechanics are slightly different for reading and writing.
 * For reading a schema can be derived from the cell definition or the data.
 * For writing the schema either defines the cells or is used only to provide types for the cells.
 */
public class SchemaAndCells<P extends CellProvider> {

    private final DataSchema schema;

    private final Collection<? extends P> dataCells;

    private SchemaAndCells(DataSchema schema,
                           Collection<? extends P> dataCells) {
        this.schema = schema;
        this.dataCells = dataCells;
    }

    /**
     * For writing we need be driven by the schema of the data.
     * So we use this until we know the schema.
     */
    public interface Factory<P extends CellProvider> {

        SchemaAndCells<P> fromSchema(DataSchema schema);

        SchemaAndCells<P> noData();
    }

    /**
     * For when we don't have any cell definitions. They are derived from the schema,
     * or from the actual spreadsheet data.
     *
     * @param <P> The type of CellProvider.
     */
    public static class WithCellFactory<P extends CellProvider> implements Factory<P> {

        private final CellProviderFactory<P> cellProviderFactory;

        WithCellFactory(CellProviderFactory<P> cellProviderFactory) {
            this.cellProviderFactory = Objects.requireNonNull(cellProviderFactory);
        }

        @Override
        public SchemaAndCells<P> fromSchema(DataSchema schema) {
            return new SchemaAndCells<>(schema, morphInto(schema));
        }

        @Override
        public SchemaAndCells<P> noData() {
            return new SchemaAndCells<>(DataSchema.emptySchema(), List.of());
        }

        public SchemaAndCells<P> fromRowAndHeadings(RowIn rowIn, String[] headings) {
            return fromRowAndHeadings(rowIn, headings, null);
        }

        public SchemaAndCells<P> fromRowAndHeadings(RowIn rowIn,
                                                        String[] headings,
                                                        DataSchema partialSchema) {

            if (rowIn == null) {
                return null;
            }

            if (partialSchema == null) {
                partialSchema = DataSchema.emptySchema();
            }

            SchemaFactory schemaFactory = DataSchemaFactory.newInstance();

            List<P> cells = new LinkedList<>();

            for (int index = 1; headings == null || index <= headings.length; ++index) {

                String heading = headings == null ? null : headings[index - 1];

                Cell cell = rowIn.getCell(index);
                if (cell == null) {
                    break;
                }

                // If we have a heading, we look by heading otherwise we use index.
                SchemaField schemaField = null;
                if (heading == null) {
                    schemaField = partialSchema.getSchemaFieldAt(index);
                }
                else {
                    schemaField = partialSchema.getSchemaFieldNamed(heading);
                    if (schemaField != null) {
                        schemaField = schemaField.mapToIndex(index);
                    }
                }

                P dataCell = cellProviderFactory.cellProviderFor(index, heading, cell);

                if (schemaField == null) {
                    schemaField = SchemaField.of(index, dataCell.getName(), dataCell.getType());
                }

                schemaFactory.addSchemaField(schemaField);

                cells.add(dataCell);
            }

            return new SchemaAndCells<>(schemaFactory.toSchema(), cells);
        }

        protected List<P> morphInto(DataSchema schema) {

            List<P> cells = new ArrayList<>(schema.lastIndex());

            for (SchemaField schemaField : schema.getSchemaFields()) {

                cells.add(createCell(schemaField));
            }

            return cells;
        }

        protected P createCell(SchemaField schemaField) {

            int index = schemaField.getIndex();
            String name = schemaField.getName();
            Class<?> type = schemaField.getType();

            return cellProviderFactory.cellProviderFor(index, name, type);
        }
    }

    public static class WithCells<P extends CellProvider> implements Factory<P> {

        private final Collection<P> cellProviders;

        public WithCells(Collection<P> cellProviders) {
            this.cellProviders = cellProviders == null
                    ? List.of() : cellProviders;
        }

        @Override
        public SchemaAndCells<P> fromSchema(DataSchema schema) {
            return new SchemaAndCells<>(schema, cellProviders);
        }

        @Override
        public SchemaAndCells<P> noData() {
            return new SchemaAndCells<>(morphOf(cellProviders), cellProviders);
        }
    }

    public static <P extends CellProvider> WithCellFactory<P>
    withCellFactory(CellProviderFactory<P>  cellProviderFactory) {
        return new WithCellFactory<>(cellProviderFactory);
    }

    /**
     * Used when we have Cells but don't know the schema yet.
     * @param cellProviders The collection of cells.
     * @return The intermediate step.
     * @param <P> The type of provider.
     */
    public static <P extends CellProvider> Factory<P> withCells(Collection<P> cellProviders) {
        return new WithCells<>(cellProviders);
    }

    /**
     * Finds the schema based just on the cells. Used by data in only, for data out we must take into
     * account the schema.
     *
     * @param dataCells The cell provider.
     * @return Schema and Cells.
     * @param <P> The type of provider.
     */
    public static <P extends CellProvider> SchemaAndCells<P> fromCells(Collection<? extends P> dataCells) {
        dataCells = dataCells == null ? List.of() : dataCells;
        return new SchemaAndCells<>(morphOf(dataCells), dataCells);
    }

    static protected DataSchema morphOf(Collection<? extends CellProvider> cells) {

        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        for (CellProvider child : cells) {

            String name = child.getName();

            Class<?> type = child.getType();

            schemaBuilder.addNamedAt(child.getIndex(), name, type);
        }

        return schemaBuilder.build();
    }

    public DataSchema getSchema() {
        return schema;
    }

    public Collection<P> getDataCells() {
        return List.copyOf(dataCells);
    }
}
