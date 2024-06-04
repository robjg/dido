package dido.poi.utils;

import dido.data.GenericData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import dido.poi.RowIn;
import dido.poi.data.DataCell;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataRowFactoryTest {

    @Test
    void testWithSingleCell() {

        DataCell<String> cell = Mockito.mock(DataCell.class);
        when(cell.getType()).then(invocation -> String.class);
        when(cell.provideCellIn(1)).thenReturn(row -> "Foo");

        SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(null, Collections.singletonList(cell));

        DataRowFactory<String> test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells());

        RowIn rowIn = mock(RowIn.class);

        GenericData<String> result = test.wrap(rowIn);

        assertThat(result.getStringAt(1), is("Foo"));
    }

    @Test
    void testWithTwoCellsNoIndexes() {

        DataCell<String> cell1 = Mockito.mock(DataCell.class);
        when(cell1.getType()).then(invocation -> String.class);
        when(cell1.provideCellIn(1)).thenReturn(row -> "Foo");

        DataCell<Double> cell2 = Mockito.mock(DataCell.class);
        when(cell2.getType()).then(invocation -> Double.class);
        when(cell2.provideCellIn(2)).thenReturn(row -> 42.0);

        SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(null, Arrays.asList(cell1, cell2));

        DataRowFactory<String> test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells());

        RowIn rowIn = mock(RowIn.class);

        GenericData<String> result = test.wrap(rowIn);

        assertThat(result.getStringAt(1), is("Foo"));
        assertThat(result.getDoubleAt(2), is(42.0));
    }

    @Test
    void testWithDisparateIndexes() {

        DataCell<String> cell3 = Mockito.mock(DataCell.class);
        when(cell3.getType()).then(invocation -> String.class);
        when(cell3.getIndex()).thenReturn(3);
        when(cell3.provideCellIn(3)).thenReturn(row -> "Foo");

        DataCell<Double> cell7 = Mockito.mock(DataCell.class);
        when(cell7.getType()).then(invocation -> Double.class);
        when(cell7.getIndex()).thenReturn(7);
        when(cell7.provideCellIn(7)).thenReturn(row -> 42.0);

        SchemaAndCells schemaAndCells = SchemaAndCells.fromSchemaOrCells(null, Arrays.asList(cell3, cell7));

        DataRowFactory<String> test = DataRowFactory.newInstance(
                schemaAndCells.getSchema(), schemaAndCells.getDataCells());

        RowIn rowIn = mock(RowIn.class);

        GenericData<String> result = test.wrap(rowIn);

        assertThat(result.getStringAt(3), is("Foo"));
        assertThat(result.getDoubleAt(7), is(42.0));
    }

}