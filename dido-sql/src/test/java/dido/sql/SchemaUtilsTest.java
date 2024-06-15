package dido.sql;

import dido.data.GenericDataSchema;
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

        GenericDataSchema schema = SchemaUtils.schemaFrom(metaData, ClassLoader.getPlatformClassLoader());

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));

        assertThat(schema.getIndexNamed("Fruit"), is(1));
        assertThat(schema.getFieldAt(1), is("Fruit"));
        assertThat(schema.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema.getTypeAt(1), is(String.class));

        assertThat(schema.getIndexNamed("Quantity"), is(2));
        assertThat(schema.getFieldAt(2), is("Quantity"));
        assertThat(schema.getTypeNamed("Quantity"), is(int.class));
        assertThat(schema.getTypeAt(2), is(int.class));

        assertThat(schema.getIndexNamed("Price"), is(3));
        assertThat(schema.getFieldAt(3), is("Price"));
        assertThat(schema.getTypeNamed("Price"), is(double.class));
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

            GenericDataSchema<String> schema = SchemaUtils.schemaFrom(rs.getMetaData());

//            System.out.println(schema);
//            {[1:BIT]=[B, [2:TINYINT]=java.lang.Integer, [3:SMALLINT]=java.lang.Integer, [4:INTEGER]=java.lang.Integer, [5:BIGINT]=java.lang.Long, [6:FLOAT]=java.lang.Double, [7:REAL]=java.lang.Double, [8:DOUBLE]=java.lang.Double, [9:NUMERIC]=java.math.BigDecimal, [10:DECIMAL]=java.math.BigDecimal, [11:CHAR]=java.lang.String, [12:VARCHAR]=java.lang.String, [13:LONGVARCHAR]=java.lang.String, [14:DATE]=java.sql.Date, [15:TIME]=java.sql.Time, [16:TIMESTAMP]=java.sql.Timestamp, [17:BINARY]=[B, [18:VARBINARY]=[B, [19:OTHER]=java.lang.Object, [20:BLOB]=java.sql.Blob, [21:CLOB]=java.sql.Clob, [22:BOOLEAN]=java.lang.Boolean, [23:TIME_WITH_TIMEZONE]=java.time.OffsetTime, [24:TIMESTAMP_WITH_TIMEZONE]=java.time.OffsetDateTime}

            assertThat(schema.getTypeOf("BIT"), is(byte[].class));
            assertThat(schema.getTypeOf("TINYINT"), is(java.lang.Integer.class));
            assertThat(schema.getTypeOf("SMALLINT"), is(java.lang.Integer.class));
            assertThat(schema.getTypeOf("INTEGER"), is(java.lang.Integer.class));
            assertThat(schema.getTypeOf("BIGINT"), is(java.lang.Long.class));
            assertThat(schema.getTypeOf("FLOAT"), is(java.lang.Double.class));
            assertThat(schema.getTypeOf("REAL"), is(java.lang.Double.class));
            assertThat(schema.getTypeOf("DOUBLE"), is(java.lang.Double.class));
            assertThat(schema.getTypeOf("NUMERIC"), is(java.math.BigDecimal.class));
            assertThat(schema.getTypeOf("DECIMAL"), is(java.math.BigDecimal.class));
            assertThat(schema.getTypeOf("CHAR"), is(java.lang.String.class));
            assertThat(schema.getTypeOf("VARCHAR"), is(java.lang.String.class));
            assertThat(schema.getTypeOf("LONGVARCHAR"), is(java.lang.String.class));
            assertThat(schema.getTypeOf("DATE"), is(java.sql.Date.class));
            assertThat(schema.getTypeOf("TIME"), is(java.sql.Time.class));
            assertThat(schema.getTypeOf("TIMESTAMP"), is(java.sql.Timestamp.class));
            assertThat(schema.getTypeOf("BINARY"), is(byte[].class));
            assertThat(schema.getTypeOf("VARBINARY"), is(byte[].class));
            assertThat(schema.getTypeOf("OTHER"), is(java.lang.Object.class));
            assertThat(schema.getTypeOf("BLOB"), is(java.sql.Blob.class));
            assertThat(schema.getTypeOf("CLOB"), is(java.sql.Clob.class));
            assertThat(schema.getTypeOf("BOOLEAN"), is(java.lang.Boolean.class));
            assertThat(schema.getTypeOf("TIME_WITH_TIMEZONE"), is(java.time.OffsetTime.class));
            assertThat(schema.getTypeOf("TIMESTAMP_WITH_TIMEZONE"), is(java.time.OffsetDateTime.class));
        }

        oddjob.destroy();
    }

}