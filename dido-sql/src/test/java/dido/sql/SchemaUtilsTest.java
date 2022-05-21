package dido.sql;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import java.io.File;
import java.sql.*;
import java.util.Objects;

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

    @Test
    void testSqlTypes() throws ArooaConversionException, SQLException, ClassNotFoundException {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_all_types_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();
        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        try (Connection connection = lookup.lookup("vars.connection", Connection.class);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from All_Types")) {

            DataSchema<String> schema = SchemaUtils.schemaFrom(rs.getMetaData());

//            System.out.println(schema);
//            {[1:BIT]=[B, [2:TINYINT]=java.lang.Integer, [3:SMALLINT]=java.lang.Integer, [4:INTEGER]=java.lang.Integer, [5:BIGINT]=java.lang.Long, [6:FLOAT]=java.lang.Double, [7:REAL]=java.lang.Double, [8:DOUBLE]=java.lang.Double, [9:NUMERIC]=java.math.BigDecimal, [10:DECIMAL]=java.math.BigDecimal, [11:CHAR]=java.lang.String, [12:VARCHAR]=java.lang.String, [13:LONGVARCHAR]=java.lang.String, [14:DATE]=java.sql.Date, [15:TIME]=java.sql.Time, [16:TIMESTAMP]=java.sql.Timestamp, [17:BINARY]=[B, [18:VARBINARY]=[B, [19:OTHER]=java.lang.Object, [20:BLOB]=java.sql.Blob, [21:CLOB]=java.sql.Clob, [22:BOOLEAN]=java.lang.Boolean, [23:TIME_WITH_TIMEZONE]=java.time.OffsetTime, [24:TIMESTAMP_WITH_TIMEZONE]=java.time.OffsetDateTime}

            assertThat(schema.getType("BIT"), is(byte[].class));
            assertThat(schema.getType("TINYINT"), is(java.lang.Integer.class));
            assertThat(schema.getType("SMALLINT"), is(java.lang.Integer.class));
            assertThat(schema.getType("INTEGER"), is(java.lang.Integer.class));
            assertThat(schema.getType("BIGINT"), is(java.lang.Long.class));
            assertThat(schema.getType("FLOAT"), is(java.lang.Double.class));
            assertThat(schema.getType("REAL"), is(java.lang.Double.class));
            assertThat(schema.getType("DOUBLE"), is(java.lang.Double.class));
            assertThat(schema.getType("NUMERIC"), is(java.math.BigDecimal.class));
            assertThat(schema.getType("DECIMAL"), is(java.math.BigDecimal.class));
            assertThat(schema.getType("CHAR"), is(java.lang.String.class));
            assertThat(schema.getType("VARCHAR"), is(java.lang.String.class));
            assertThat(schema.getType("LONGVARCHAR"), is(java.lang.String.class));
            assertThat(schema.getType("DATE"), is(java.sql.Date.class));
            assertThat(schema.getType("TIME"), is(java.sql.Time.class));
            assertThat(schema.getType("TIMESTAMP"), is(java.sql.Timestamp.class));
            assertThat(schema.getType("BINARY"), is(byte[].class));
            assertThat(schema.getType("VARBINARY"), is(byte[].class));
            assertThat(schema.getType("OTHER"), is(java.lang.Object.class));
            assertThat(schema.getType("BLOB"), is(java.sql.Blob.class));
            assertThat(schema.getType("CLOB"), is(java.sql.Clob.class));
            assertThat(schema.getType("BOOLEAN"), is(java.lang.Boolean.class));
            assertThat(schema.getType("TIME_WITH_TIMEZONE"), is(java.time.OffsetTime.class));
            assertThat(schema.getType("TIMESTAMP_WITH_TIMEZONE"), is(java.time.OffsetDateTime.class));
        }

        oddjob.destroy();
    }

}