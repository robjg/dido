package dido.sql;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.NamedData;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlDataHowTest {

    static final long SECOND = 1000L;

    static final long MINUTE = 60 * SECOND;

    static final long HOUR = 60 * MINUTE;

    private static final Logger logger = LoggerFactory.getLogger(
            SqlDataHowTest.class);

    @Test
    public void testSimpleWriteRead() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_fruit_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DataOutHow<Connection> outHow
                = SqlDataOutHow.fromSql("insert into fruit (type, quantity) values (?, ?)")
                .make();

        DataInHow<Connection, NamedData> inHow
                = SqlDataInHow.fromSql("select type, quantity from fruit order by type")
                .make();

        logger.info("** Writing **");

        Connection connectionOut = lookup.lookup("vars.connection", Connection.class);

        DataOut writer = outHow.outTo(connectionOut);

        writer.accept(ArrayData.of("apple", 20));
        writer.accept(ArrayData.of("banana", 10));
        writer.accept(ArrayData.of("orange", 102));

        writer.close();

        logger.info("** Reading **");

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<NamedData> reader = inHow.inFrom(connectionIn);

        {
            NamedData fruit = reader.get();
            assertThat(fruit.getString("type"), is("apple"));
            assertThat(fruit.getInt("quantity"), is(20));
        }
        {
            NamedData fruit = reader.get();
            assertThat(fruit.getString("type"), is("banana"));
            assertThat(fruit.getInt("quantity"), is(10));
        }
        {
            NamedData fruit = reader.get();
            assertThat(fruit.getString("type"), is("orange"));
            assertThat(fruit.getInt("quantity"), is(102));
        }

        assertThat(reader.get(), nullValue());

        reader.close();
    }

    @Test
    public void testNumericTypesWriteRead() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_numbers_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DataOutHow<Connection> outHow
                = SqlDataOutHow.fromSql("insert into Numbers " +
                        "(Description, A_TinyInt, A_SmallInt, A_Integer, A_BigInt, A_Numeric, A_Decimal, A_Real, A_Float, A_Double)" +
                        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .make();

        logger.info("** Writing **");

        Connection connectionOut = lookup.lookup("vars.connection", Connection.class);

        DataOut writer = outHow.outTo(connectionOut);

        writer.accept(ArrayData.of("1-Doubles", 42.24, 42.24, 42.24, 42.24, 42.24, 42.24, 42.24, 42.24, 42.24));
        writer.accept(ArrayData.of("2-Ints", 42, 42, 42, 42, 42, 42, 42, 42, 42));
        writer.accept(ArrayData.of("3-Nulls", null, null, null, null, null, null, null, null, null));
        writer.accept(ArrayData.of("4-Strings", "42", "42", "42", "42", "42.24", "42.24", "42.24", "42.24", "42.24"));

        writer.close();

        logger.info("** Reading **");

        DataInHow<Connection, NamedData> inHow
                = SqlDataInHow.fromSql("select A_TinyInt, A_SmallInt, A_Integer, A_BigInt, A_Numeric, A_Decimal, A_Real, A_Float, A_Double " +
                        "from Numbers order by Description")
                .make();

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<NamedData> reader = inHow.inFrom(connectionIn);

        {
            NamedData numbers = reader.get();

            DataSchema schema = numbers.getSchema();

            assertThat(schema.firstIndex(), is(1));
            assertThat(schema.lastIndex(), is(9));
            assertThat(schema.nextIndex(1), is(2));
            assertThat(schema.nextIndex(4), is(5));
            assertThat(schema.nextIndex(9), is(0));

            assertThat(schema.getFieldNames(), contains("A_TINYINT", "A_SMALLINT", "A_INTEGER", "A_BIGINT", "A_NUMERIC", "A_DECIMAL", "A_REAL", "A_FLOAT", "A_DOUBLE"));

            assertThat(schema.getTypeNamed("A_TINYINT"), is(Integer.class));
            assertThat(schema.getTypeNamed("A_SMALLINT"), is(Integer.class));
            assertThat(schema.getTypeNamed("A_INTEGER"), is(Integer.class));
            assertThat(schema.getTypeNamed("A_BIGINT"), is(Long.class));
            assertThat(schema.getTypeNamed("A_NUMERIC"), is(BigDecimal.class));
            assertThat(schema.getTypeNamed("A_DECIMAL"), is(BigDecimal.class));
            assertThat(schema.getTypeNamed("A_REAL"), is(Double.class));
            assertThat(schema.getTypeNamed("A_FLOAT"), is(Double.class));
            assertThat(schema.getTypeNamed("A_DOUBLE"), is(Double.class));

            assertThat(numbers.getByte("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShort("A_TinyInt"), is((short) 42));
            assertThat(numbers.getInt("A_TinyInt"), is(42));
            assertThat(numbers.getLong("A_TinyInt"), is(42L));
            assertThat(numbers.getFloat("A_TinyInt"), is(42.0F));
            assertThat(numbers.getDouble("A_TinyInt"), is(42.0));
            assertThat(numbers.getString("A_TinyInt"), is("42"));
            assertThat(numbers.get("A_TinyInt").getClass(), is(Integer.class));

            assertThat(numbers.getByte("A_SmallInt"), is((byte) 42));
            assertThat(numbers.getShort("A_SmallInt"), is((short) 42));
            assertThat(numbers.getInt("A_SmallInt"), is(42));
            assertThat(numbers.getLong("A_SmallInt"), is(42L));
            assertThat(numbers.getFloat("A_SmallInt"), is(42.0F));
            assertThat(numbers.getDouble("A_SmallInt"), is(42.0));
            assertThat(numbers.getString("A_SmallInt"), is("42"));
            assertThat(numbers.get("A_SmallInt").getClass(), is(Integer.class));

            assertThat(numbers.getByte("A_Integer"), is((byte) 42));
            assertThat(numbers.getShort("A_Integer"), is((short) 42));
            assertThat(numbers.getInt("A_Integer"), is(42));
            assertThat(numbers.getLong("A_Integer"), is(42L));
            assertThat(numbers.getFloat("A_Integer"), is(42.0F));
            assertThat(numbers.getDouble("A_Integer"), is(42.0));
            assertThat(numbers.getString("A_Integer"), is("42"));
            assertThat(numbers.get("A_Integer").getClass(), is(Integer.class));

            assertThat(numbers.getByte("A_BigInt"), is((byte) 42));
            assertThat(numbers.getShort("A_BigInt"), is((short) 42));
            assertThat(numbers.getInt("A_BigInt"), is(42));
            assertThat(numbers.getLong("A_BigInt"), is(42L));
            assertThat(numbers.getFloat("A_BigInt"), is(42.0F));
            assertThat(numbers.getDouble("A_BigInt"), is(42.0));
            assertThat(numbers.getString("A_BigInt"), is("42"));
            assertThat(numbers.get("A_BigInt").getClass(), is(Long.class));

            assertThat(numbers.getByte("A_Numeric"), is((byte) 42));
            assertThat(numbers.getShort("A_Numeric"), is((short) 42));
            assertThat(numbers.getInt("A_Numeric"), is(42));
            assertThat(numbers.getLong("A_Numeric"), is(42L));
            assertThat(numbers.getFloat("A_Numeric"), is(42.24F));
            assertThat(numbers.getDouble("A_Numeric"), is(42.24));
            assertThat(numbers.getString("A_Numeric"), is("42.24"));
            assertThat(numbers.get("A_Numeric").getClass(), is(BigDecimal.class));

            assertThat(numbers.getByte("A_Decimal"), is((byte) 42));
            assertThat(numbers.getShort("A_Decimal"), is((short) 42));
            assertThat(numbers.getInt("A_Decimal"), is(42));
            assertThat(numbers.getLong("A_Decimal"), is(42L));
            assertThat(numbers.getFloat("A_Decimal"), is(42.24F));
            assertThat(numbers.getDouble("A_Decimal"), is(42.24));
            assertThat(numbers.getString("A_Decimal"), is("42.24"));
            assertThat(numbers.get("A_Decimal").getClass(), is(BigDecimal.class));

            assertThat(numbers.getByte("A_Real"), is((byte) 42));
            assertThat(numbers.getShort("A_Real"), is((short) 42));
            assertThat(numbers.getInt("A_Real"), is(42));
            assertThat(numbers.getLong("A_Real"), is(42L));
            assertThat(numbers.getFloat("A_Real"), is(42.24F));
            assertThat(numbers.getDouble("A_Real"), is(42.24));
            assertThat(numbers.getString("A_Real"), is("42.24E0"));
            assertThat(numbers.get("A_Real").getClass(), is(Double.class));

            assertThat(numbers.getByte("A_Float"), is((byte) 42));
            assertThat(numbers.getShort("A_Float"), is((short) 42));
            assertThat(numbers.getInt("A_Float"), is(42));
            assertThat(numbers.getLong("A_Float"), is(42L));
            assertThat(numbers.getFloat("A_Float"), is(42.24F));
            assertThat(numbers.getDouble("A_Float"), is(42.24));
            assertThat(numbers.getString("A_Float"), is("42.24E0"));
            assertThat(numbers.get("A_Float").getClass(), is(Double.class));

            assertThat(numbers.getByte("A_Double"), is((byte) 42));
            assertThat(numbers.getShort("A_Double"), is((short) 42));
            assertThat(numbers.getInt("A_Double"), is(42));
            assertThat(numbers.getLong("A_Double"), is(42L));
            assertThat(numbers.getFloat("A_Double"), is(42.24F));
            assertThat(numbers.getDouble("A_Double"), is(42.24));
            assertThat(numbers.getString("A_Double"), is("42.24E0"));
            assertThat(numbers.get("A_Double").getClass(), is(Double.class));
        }

        {
            NamedData numbers = reader.get();

            assertThat(numbers.getByte("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShort("A_SmallInt"), is((short) 42));
            assertThat(numbers.getInt("A_Integer"), is(42));
            assertThat(numbers.getLong("A_BigInt"), is(42L));
            assertThat(numbers.getDouble("A_Numeric"), is(42.0));
            assertThat(numbers.getDouble("A_Decimal"), is(42.0));
            assertThat(numbers.getDouble("A_Real"), is(42.0));
            assertThat(numbers.getFloat("A_Float"), is(42.0F));
            assertThat(numbers.getDouble("A_Double"), is(42.0));
        }

        {
            NamedData numbers = reader.get();

            assertThat(numbers.has("A_TinyInt"), is(false));
            assertThat(numbers.has("A_SmallInt"), is(false));
            assertThat(numbers.has("A_Integer"), is(false));
            assertThat(numbers.has("A_BigInt"), is(false));
            assertThat(numbers.has("A_Numeric"), is(false));
            assertThat(numbers.has("A_Decimal"), is(false));
            assertThat(numbers.has("A_Real"), is(false));
            assertThat(numbers.has("A_Float"), is(false));
            assertThat(numbers.has("A_Double"), is(false));
        }

        {
            NamedData numbers = reader.get();

            assertThat(numbers.getByte("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShort("A_SmallInt"), is((short) 42));
            assertThat(numbers.getInt("A_Integer"), is(42));
            assertThat(numbers.getLong("A_BigInt"), is(42L));
            assertThat(numbers.getDouble("A_Numeric"), is(42.24));
            assertThat(numbers.getDouble("A_Decimal"), is(42.24));
            assertThat(numbers.getDouble("A_Real"), is(42.24));
            assertThat(numbers.getFloat("A_Float"), is(42.24F));
            assertThat(numbers.getDouble("A_Double"), is(42.24));
        }

        assertThat(reader.get(), nullValue());

        reader.close();
    }

    @Test
    public void testDateTypesWriteRead() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_dates_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DataOutHow<Connection> outHow
                = SqlDataOutHow.fromSql("insert into Dates " +
                        "(Description, A_Date, A_Time, A_Zoned_Time, A_TimeStamp, A_Zoned_TimeStamp)" +
                        " values (?, ?, ?, ?, ?, ?)")
                .make();

        logger.info("** Writing **");

        Connection connectionOut = lookup.lookup("vars.connection", Connection.class);

        DataOut writer = outHow.outTo(connectionOut);

        LocalDate localDate = LocalDate.parse("2008-08-22");
        LocalTime localTime = LocalTime.parse("20:08:08.034900");

        LocalDateTime localDateTime = LocalDateTime.parse("2008-08-08T20:08:08.034900");

        OffsetTime offsetTime = OffsetTime.parse("20:08:08.034900+08:00");

        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2008-08-08T20:08:08.034900+08:00");

        writer.accept(ArrayData.of("1-Dates", localDate, localTime, offsetTime, localDateTime, offsetDateTime));
        writer.accept(ArrayData.of("2-Nulls", null, null, null, null, null));
        writer.accept(ArrayData.of("3-Strings", "2008-08-22", "20:08:08",
                "20:08:08+8:00", "2008-08-08 20:08:08.034900", "2008-08-08 20:08:08.034900+8:00"));

        writer.close();

        logger.info("** Reading **");

        DataInHow<Connection, NamedData> inHow
                = SqlDataInHow.fromSql("select A_Date, A_Time, A_Zoned_Time, A_TimeStamp, A_Zoned_TimeStamp " +
                        "from Dates order by Description")
                .make();

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<NamedData> reader = inHow.inFrom(connectionIn);

        {
            NamedData dates = reader.get();

            DataSchema schema = dates.getSchema();

            assertThat(schema.firstIndex(), is(1));
            assertThat(schema.lastIndex(), is(5));

            assertThat(schema.getTypeNamed("A_DATE"), is(java.sql.Date.class));
            assertThat(schema.getTypeNamed("A_TIME"), is(java.sql.Time.class));
            assertThat(schema.getTypeNamed("A_ZONED_TIME"), is(java.time.OffsetTime.class));
            assertThat(schema.getTypeNamed("A_TIMESTAMP"), is(java.sql.Timestamp.class));
            assertThat(schema.getTypeNamed("A_ZONED_TIMESTAMP"), is(java.time.OffsetDateTime.class));

            assertThat(dates.get("A_Date").getClass(), is(java.sql.Date.class));
            assertThat(Instant.ofEpochMilli(
                            dates.getAs("A_Date", java.sql.Date.class).getTime()),
                    is(localDate.atStartOfDay(ZoneOffset.systemDefault())
                            .toInstant()));
            assertThat(dates.getString("A_Date"), is("2008-08-22"));

            assertThat(dates.get("A_Time").getClass(), is(java.sql.Time.class));
            java.sql.Time aTime = dates.getAs("A_Time", java.sql.Time.class);
            assertThat(dates.getAs("A_Time", java.sql.Time.class).getTime(),
                    is(20 * HOUR +  8 * MINUTE + 8 * SECOND));
            // HSQLDB stores time with zone. This will depend on your default time zone.
//            assertThat(aTime.toString(), is("20:08:08"));
            ZonedDateTime utcDateTime = localTime.truncatedTo(ChronoUnit.SECONDS)
                    .atDate(LocalDate.now()).atZone(ZoneId.of("UTC"));
            assertThat(dates.getString("A_Time"),
                    is(utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime().toString()));

            assertThat(dates.get("A_Zoned_Time").getClass(), is(java.time.OffsetTime.class));
            assertThat(dates.getAs("A_Zoned_Time", java.time.OffsetTime.class),
                    is(offsetTime.truncatedTo(ChronoUnit.SECONDS)));
            assertThat(dates.getString("A_Zoned_Time"), is("20:08:08+8:00"));

            assertThat(dates.get("A_TimeStamp").getClass(), is(java.sql.Timestamp.class));
            assertThat(Instant.ofEpochMilli(dates.getAs("A_TimeStamp", java.sql.Timestamp.class).getTime()),
                    is(localDateTime.truncatedTo(ChronoUnit.MILLIS).atZone(ZoneOffset.systemDefault()).toInstant()));
            assertThat(dates.getString("A_TimeStamp"), is("2008-08-08 20:08:08.034900"));

            assertThat(dates.get("A_Zoned_TimeStamp").getClass(), is(java.time.OffsetDateTime.class));
            assertThat(dates.getAs("A_Zoned_TimeStamp", java.time.OffsetDateTime.class),
                    is(offsetDateTime));
            assertThat(dates.getString("A_Zoned_TimeStamp"), is("2008-08-08 20:08:08.034900+8:00"));
        }

        {
            NamedData dates = reader.get();

            assertThat(dates.has("A_Date"), is(false));
            assertThat(dates.has("A_Time"), is(false));
            assertThat(dates.has("A_Zoned_Time"), is(false));
            assertThat(dates.has("A_TimeStamp"), is(false));
            assertThat(dates.has("A_Zoned_TimeStamp"), is(false));
        }

        {
            NamedData dates = reader.get();

            assertThat(Instant.ofEpochMilli(
                            dates.getAs("A_Date", java.sql.Date.class).getTime()),
                    is(localDate.atStartOfDay(ZoneOffset.systemDefault())
                            .toInstant()));

            assertThat(Instant.ofEpochMilli(
                            dates.getAs("A_Time", java.sql.Time.class).getTime()),
                    is(localTime.truncatedTo(ChronoUnit.SECONDS).atDate(LocalDate.ofEpochDay(0))
                            .atZone(ZoneOffset.systemDefault()).toInstant()));

            assertThat(dates.getAs("A_Zoned_Time", java.time.OffsetTime.class),
                    is(offsetTime.truncatedTo(ChronoUnit.SECONDS)));

            assertThat(Instant.ofEpochMilli(dates.getAs("A_TimeStamp", java.sql.Timestamp.class).getTime()),
                    is(localDateTime.truncatedTo(ChronoUnit.MILLIS).atZone(ZoneOffset.systemDefault()).toInstant()));

            assertThat(dates.getAs("A_Zoned_TimeStamp", java.time.OffsetDateTime.class),
                    is(offsetDateTime));
        }

        assertThat(reader.get(), nullValue());

        reader.close();
    }


    @Test
    void dateAssumptions() {

        LocalTime localTime = LocalTime.parse("20:09:37");

        ZonedDateTime zonedDateTime = localTime.atDate(LocalDate.now()).atZone(ZoneId.of("UTC"));

        assertThat(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime().toString(), is("21:09:37"));
    }
}
