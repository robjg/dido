package dido.poi.utils;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaField;
import dido.data.useful.AbstractFieldGetter;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import dido.poi.RowIn;
import dido.poi.data.DataCell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DataRowFactoryTest {

    static DataCell textCell(int index) {

        DataCell cell = Mockito.mock(DataCell.class);
        when(cell.getType()).then(invocation -> String.class);
        when(cell.getIndex()).thenReturn(index);
        doAnswer(invocation -> (CellIn) capture -> {
            SchemaField schemaField = ((DataSchema) invocation.getArgument(1))
                    .getSchemaFieldAt(invocation.getArgument(0));
            capture.accept(schemaField,
                    new AbstractFieldGetter() {
                        @Override
                        public Object get(DidoData data) {
                            return "Foo";
                        }
                    });
        })
                .when(cell).provideCellIn(eq(index), any(DataSchema.class), any(DidoConversionProvider.class));
        return cell;
    }

    static DataCell numberCell(int index) {

        DataCell cell = Mockito.mock(DataCell.class);
        when(cell.getType()).then(invocation -> Double.class);
        when(cell.getIndex()).thenReturn(index);
        doAnswer(invocation -> (CellIn) capture -> {
            SchemaField schemaField = ((DataSchema) invocation.getArgument(1))
                    .getSchemaFieldAt(invocation.getArgument(0));
            capture.accept(schemaField,
                    new AbstractFieldGetter() {
                        @Override
                        public Object get(DidoData data) {
                            return 42.0;
                        }
                    });
        })
                .when(cell).provideCellIn(eq(index), any(DataSchema.class), any(DidoConversionProvider.class));
        return cell;
    }

    @Test
    void testWithSingleCell() {

        DataCell cell = textCell(1);

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.fromCells(Collections.singletonList(cell));

        DataRowFactory test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells(),
                DefaultConversionProvider.defaultInstance());

        RowIn rowIn = mock(RowIn.class);

        DidoData result = test.wrap(rowIn);

        assertThat(result.getStringAt(1), is("Foo"));
    }

    @Test
    void testWithTwoCellsNoIndexes() {

        DataCell cell1 = textCell(1);
        DataCell cell2 = numberCell(2);

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.fromCells(Arrays.asList(cell1, cell2));

        DataRowFactory test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells(),
                DefaultConversionProvider.defaultInstance());

        RowIn rowIn = mock(RowIn.class);

        DidoData result = test.wrap(rowIn);

        assertThat(result.getStringAt(1), is("Foo"));
        assertThat(result.getDoubleAt(2), is(42.0));
    }

    @Test
    void testWithDisparateIndexes() {

        DataCell cell3 = textCell(3);

        DataCell cell7 = numberCell(7);

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.fromCells(Arrays.asList(cell3, cell7));

        DataRowFactory test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells(),
                DefaultConversionProvider.defaultInstance());

        RowIn rowIn = mock(RowIn.class);

        DidoData result = test.wrap(rowIn);

        assertThat(result.getStringAt(3), is("Foo"));
        assertThat(result.getDoubleAt(7), is(42.0));
    }

}