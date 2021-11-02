package dido.sql;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemaUtilsTest {

    @Test
    void testSchemaCreation() throws SQLException, ClassNotFoundException {

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(3);

        when(metaData.getColumnClassName(1)).thenReturn(String.class.getName());
        when(metaData.getColumnClassName(2)).thenReturn(int.class.getName());
        when(metaData.getColumnClassName(3)).thenReturn(double.class.getName());

        when(metaData.getColumnName(1)).thenReturn("Fruit");
        when(metaData.getColumnName(2)).thenReturn("Quantity");
        when(metaData.getColumnName(3)).thenReturn("Price");

        DataSchema schema = SchemaUtils.schemaFrom(metaData, ClassLoader.getPlatformClassLoader());

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));

        assertThat(schema.getIndex("Fruit"), is(1));
        assertThat(schema.getFieldAt(1), is("Fruit"));
        assertThat(schema.getType("Fruit"), is(String.class));
        assertThat(schema.getTypeAt(1), is(String.class));

        assertThat(schema.getIndex("Quantity"), is(2));
        assertThat(schema.getFieldAt(2), is("Quantity"));
        assertThat(schema.getType("Quantity"), is(int.class));
        assertThat(schema.getTypeAt(2), is(int.class));

        assertThat(schema.getIndex("Price"), is(3));
        assertThat(schema.getFieldAt(3), is("Price"));
        assertThat(schema.getType("Price"), is(double.class));
        assertThat(schema.getTypeAt(3), is(double.class));
    }

}