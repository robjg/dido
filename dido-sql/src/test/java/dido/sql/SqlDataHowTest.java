package dido.sql;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaBuilder;
import dido.how.*;
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

        DataInHow<Connection, DidoData> inHow
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

        DataIn<DidoData> reader = inHow.inFrom(connectionIn);

        {
            DidoData fruit = reader.get();
            assertThat(fruit.getStringNamed("type"), is("apple"));
            assertThat(fruit.getIntNamed("quantity"), is(20));
        }
        {
            DidoData fruit = reader.get();
            assertThat(fruit.getStringNamed("type"), is("banana"));
            assertThat(fruit.getIntNamed("quantity"), is(10));
        }
        {
            DidoData fruit = reader.get();
            assertThat(fruit.getStringNamed("type"), is("orange"));
            assertThat(fruit.getIntNamed("quantity"), is(102));
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

        DataInHow<Connection, DidoData> inHow
                = SqlDataInHow.fromSql("select A_TinyInt, A_SmallInt, A_Integer, A_BigInt, A_Numeric, A_Decimal, A_Real, A_Float, A_Double " +
                        "from Numbers order by Description")
                .make();

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<DidoData> reader = inHow.inFrom(connectionIn);

        {
            DidoData numbers = reader.get();

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

            assertThat(numbers.getByteNamed("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_TinyInt"), is((short) 42));
            assertThat(numbers.getIntNamed("A_TinyInt"), is(42));
            assertThat(numbers.getLongNamed("A_TinyInt"), is(42L));
            assertThat(numbers.getFloatNamed("A_TinyInt"), is(42.0F));
            assertThat(numbers.getDoubleNamed("A_TinyInt"), is(42.0));
            assertThat(numbers.getStringNamed("A_TinyInt"), is("42"));
            assertThat(numbers.getNamed("A_TinyInt"), is(42));
            assertThat(numbers.getNamed("A_TINYINT").getClass(), is(Integer.class));

            assertThat(numbers.getByteNamed("A_SmallInt"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_SmallInt"), is((short) 42));
            assertThat(numbers.getIntNamed("A_SmallInt"), is(42));
            assertThat(numbers.getLongNamed("A_SmallInt"), is(42L));
            assertThat(numbers.getFloatNamed("A_SmallInt"), is(42.0F));
            assertThat(numbers.getDoubleNamed("A_SmallInt"), is(42.0));
            assertThat(numbers.getStringNamed("A_SmallInt"), is("42"));
            assertThat(numbers.getNamed("A_SMALLINT"), is(42));
            assertThat(numbers.getNamed("A_SmallInt").getClass(), is(Integer.class));

            assertThat(numbers.getByteNamed("A_Integer"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Integer"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Integer"), is(42));
            assertThat(numbers.getLongNamed("A_Integer"), is(42L));
            assertThat(numbers.getFloatNamed("A_Integer"), is(42.0F));
            assertThat(numbers.getDoubleNamed("A_Integer"), is(42.0));
            assertThat(numbers.getStringNamed("A_Integer"), is("42"));
            assertThat(numbers.getNamed("A_Integer").getClass(), is(Integer.class));

            assertThat(numbers.getByteNamed("A_BigInt"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_BigInt"), is((short) 42));
            assertThat(numbers.getIntNamed("A_BigInt"), is(42));
            assertThat(numbers.getLongNamed("A_BigInt"), is(42L));
            assertThat(numbers.getFloatNamed("A_BigInt"), is(42.0F));
            assertThat(numbers.getDoubleNamed("A_BigInt"), is(42.0));
            assertThat(numbers.getStringNamed("A_BigInt"), is("42"));
            assertThat(numbers.getNamed("A_BigInt").getClass(), is(Long.class));

            assertThat(numbers.getByteNamed("A_Numeric"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Numeric"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Numeric"), is(42));
            assertThat(numbers.getLongNamed("A_Numeric"), is(42L));
            assertThat(numbers.getFloatNamed("A_Numeric"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Numeric"), is(42.24));
            assertThat(numbers.getStringNamed("A_Numeric"), is("42.24"));
            assertThat(numbers.getNamed("A_Numeric").getClass(), is(BigDecimal.class));

            assertThat(numbers.getByteNamed("A_Decimal"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Decimal"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Decimal"), is(42));
            assertThat(numbers.getLongNamed("A_Decimal"), is(42L));
            assertThat(numbers.getFloatNamed("A_Decimal"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Decimal"), is(42.24));
            assertThat(numbers.getStringNamed("A_Decimal"), is("42.24"));
            assertThat(numbers.getNamed("A_Decimal").getClass(), is(BigDecimal.class));

            assertThat(numbers.getByteNamed("A_Real"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Real"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Real"), is(42));
            assertThat(numbers.getLongNamed("A_Real"), is(42L));
            assertThat(numbers.getFloatNamed("A_Real"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Real"), is(42.24));
            assertThat(numbers.getStringNamed("A_Real"), is("42.24E0"));
            assertThat(numbers.getNamed("A_Real").getClass(), is(Double.class));

            assertThat(numbers.getByteNamed("A_Float"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Float"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Float"), is(42));
            assertThat(numbers.getLongNamed("A_Float"), is(42L));
            assertThat(numbers.getFloatNamed("A_Float"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Float"), is(42.24));
            assertThat(numbers.getStringNamed("A_Float"), is("42.24E0"));
            assertThat(numbers.getNamed("A_Float").getClass(), is(Double.class));

            assertThat(numbers.getByteNamed("A_Double"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_Double"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Double"), is(42));
            assertThat(numbers.getLongNamed("A_Double"), is(42L));
            assertThat(numbers.getFloatNamed("A_Double"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Double"), is(42.24));
            assertThat(numbers.getStringNamed("A_Double"), is("42.24E0"));
            assertThat(numbers.getNamed("A_Double").getClass(), is(Double.class));
        }

        {
            DidoData numbers = reader.get();

            assertThat(numbers.getByteNamed("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_SmallInt"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Integer"), is(42));
            assertThat(numbers.getLongNamed("A_BigInt"), is(42L));
            assertThat(numbers.getDoubleNamed("A_Numeric"), is(42.0));
            assertThat(numbers.getDoubleNamed("A_Decimal"), is(42.0));
            assertThat(numbers.getDoubleNamed("A_Real"), is(42.0));
            assertThat(numbers.getFloatNamed("A_Float"), is(42.0F));
            assertThat(numbers.getDoubleNamed("A_Double"), is(42.0));
        }

        {
            DidoData numbers = reader.get();

            assertThat(numbers.hasNamed("A_TinyInt"), is(false));
            assertThat(numbers.hasNamed("A_SmallInt"), is(false));
            assertThat(numbers.hasNamed("A_Integer"), is(false));
            assertThat(numbers.hasNamed("A_BigInt"), is(false));
            assertThat(numbers.hasNamed("A_Numeric"), is(false));
            assertThat(numbers.hasNamed("A_Decimal"), is(false));
            assertThat(numbers.hasNamed("A_Real"), is(false));
            assertThat(numbers.hasNamed("A_Float"), is(false));
            assertThat(numbers.hasNamed("A_Double"), is(false));
        }

        {
            DidoData numbers = reader.get();

            assertThat(numbers.getByteNamed("A_TinyInt"), is((byte) 42));
            assertThat(numbers.getShortNamed("A_SmallInt"), is((short) 42));
            assertThat(numbers.getIntNamed("A_Integer"), is(42));
            assertThat(numbers.getLongNamed("A_BigInt"), is(42L));
            assertThat(numbers.getDoubleNamed("A_Numeric"), is(42.24));
            assertThat(numbers.getDoubleNamed("A_Decimal"), is(42.24));
            assertThat(numbers.getDoubleNamed("A_Real"), is(42.24));
            assertThat(numbers.getFloatNamed("A_Float"), is(42.24F));
            assertThat(numbers.getDoubleNamed("A_Double"), is(42.24));
        }

        assertThat(reader.get(), nullValue());

        reader.close();
    }

    @Test
    public void testNumericTypesReadWithAlternativeSchema() throws Exception {

        String config = Objects.requireNonNull(getClass().getResource(
                "create_numbers_table.xml")).getFile();

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(config));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        DataSchema overrideSchema = SchemaBuilder.newInstance()
                .addNamed("A_TinyInt", Short.class)
                .addNamed("A_SmallInt", Byte.class)
                .addNamed("A_Integer", Long.class)
                .addNamed("A_BigInt", Integer.class)
                .addNamed("A_Numeric", Double.class)
                .addNamed("A_Decimal", Double.class)
                .addNamed("A_Real", Float.class)
                .addNamed("A_Float", Float.class)
                .addNamed("A_Double", Float.class)
                .build();

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

        DataInHow<Connection, DidoData> inHow
                = SqlDataInHow.fromSql("select A_TinyInt, A_SmallInt, A_Integer, A_BigInt, A_Numeric, A_Decimal, A_Real, A_Float, A_Double " +
                        "from Numbers order by Description")
                .schema(overrideSchema)
                .make();

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<DidoData> reader = inHow.inFrom(connectionIn);

        {
            DidoData numbers = reader.get();

            DataSchema schema = numbers.getSchema();

            assertThat(schema, is(overrideSchema));

            assertThat(numbers.getNamed("A_TINYINT"), is((short) 42));
            assertThat(numbers.getNamed("A_TinyInt").getClass(), is(Short.class));

            assertThat(numbers.getNamed("A_SMALLINT"), is((byte) 42));
            assertThat(numbers.getNamed("A_SmallInt").getClass(), is(Byte.class));

            assertThat(numbers.getNamed("A_INTEGER"), is(42L));
            assertThat(numbers.getNamed("A_Integer").getClass(), is(Long.class));

            assertThat(numbers.getNamed("A_BIGINT"), is(42));
            assertThat(numbers.getNamed("A_BigInt").getClass(), is(Integer.class));

            assertThat(numbers.getNamed("A_Numeric"), is(42.24));
            assertThat(numbers.getNamed("A_Numeric").getClass(), is(Double.class));

            assertThat(numbers.getNamed("A_Decimal"), is(42.24));
            assertThat(numbers.getNamed("A_Decimal").getClass(), is(Double.class));


            try {
                assertThat(numbers.getNamed("A_Real"), is(42.24F));
                assertThat("Expected to fail", false);
            }
            catch(FieldAccessException e) {
                // incompatible data type in conversion
            }

            try {
                // This is wierd - HSQL can't convert a float column to a float.
                assertThat(numbers.getNamed("A_Float"), is(42.24F));
                assertThat("Expected to fail", false);
            }
            catch(FieldAccessException e) {
                // incompatible data type in conversion.
            }

            try {
                // ditto a double column to a float.
                assertThat(numbers.getNamed("A_Double"), is(42.24F));
                assertThat("Expected to fail", false);
            }
            catch(FieldAccessException e) {
                // incompatible data type in conversion.
            }
        }

        {
            DidoData numbers = reader.get();


            assertThat(numbers.getNamed("A_TinyInt"), is((short) 42));
            assertThat(numbers.getNamed("A_SmallInt"), is((byte) 42));
            assertThat(numbers.getNamed("A_Integer"), is(42L));
            assertThat(numbers.getNamed("A_BigInt"), is(42));
            assertThat(numbers.getNamed("A_Numeric"), is(42.0));
            assertThat(numbers.getNamed("A_Decimal"), is(42.0));

//            assertThat(numbers.get("A_Real"), is(42.0));
//            assertThat(numbers.get("A_Float"), is(42.0F));
//            assertThat(numbers.get("A_Double"), is(42.0));
        }

        {
            DidoData numbers = reader.get();

            assertThat(numbers.hasNamed("A_TinyInt"), is(false));
            assertThat(numbers.hasNamed("A_SmallInt"), is(false));
            assertThat(numbers.hasNamed("A_Integer"), is(false));
            assertThat(numbers.hasNamed("A_BigInt"), is(false));
            assertThat(numbers.hasNamed("A_Numeric"), is(false));
            assertThat(numbers.hasNamed("A_Decimal"), is(false));
            assertThat(numbers.hasNamed("A_Real"), is(false));
            assertThat(numbers.hasNamed("A_Float"), is(false));
            assertThat(numbers.hasNamed("A_Double"), is(false));
        }

        {
            DidoData numbers = reader.get();

            assertThat(numbers.getNamed("A_TinyInt"), is((short) 42));
            assertThat(numbers.getNamed("A_SmallInt"), is((byte) 42));
            assertThat(numbers.getNamed("A_Integer"), is(42L));
            assertThat(numbers.getNamed("A_BigInt"), is(42));
            assertThat(numbers.getNamed("A_Numeric"), is(42.24));
            assertThat(numbers.getNamed("A_Decimal"), is(42.24));
//            assertThat(numbers.get("A_Real"), is(42.24));
//            assertThat(numbers.get("A_Float"), is(42.24F));
//            assertThat(numbers.get("A_Double"), is(42.24));
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

        DataInHow<Connection, DidoData> inHow
                = SqlDataInHow.fromSql("select A_Date, A_Time, A_Zoned_Time, A_TimeStamp, A_Zoned_TimeStamp " +
                        "from Dates order by Description")
                .make();

        Connection connectionIn = lookup.lookup("vars.connection", Connection.class);

        DataIn<DidoData> reader = inHow.inFrom(connectionIn);

        {
            DidoData dates = reader.get();

            DataSchema schema = dates.getSchema();

            assertThat(schema.firstIndex(), is(1));
            assertThat(schema.lastIndex(), is(5));

            assertThat(schema.getTypeNamed("A_DATE"), is(java.sql.Date.class));
            assertThat(schema.getTypeNamed("A_TIME"), is(java.sql.Time.class));
            assertThat(schema.getTypeNamed("A_ZONED_TIME"), is(java.time.OffsetTime.class));
            assertThat(schema.getTypeNamed("A_TIMESTAMP"), is(java.sql.Timestamp.class));
            assertThat(schema.getTypeNamed("A_ZONED_TIMESTAMP"), is(java.time.OffsetDateTime.class));

            assertThat(dates.getNamed("A_Date").getClass(), is(java.sql.Date.class));
            assertThat(Instant.ofEpochMilli(
                            ((java.sql.Date) dates.getNamed("A_Date")).getTime()),
                    is(localDate.atStartOfDay(ZoneOffset.systemDefault())
                            .toInstant()));
            assertThat(dates.getStringNamed("A_Date"), is("2008-08-22"));

            assertThat(dates.getNamed("A_Time").getClass(), is(java.sql.Time.class));
            java.sql.Time aTime = (java.sql.Time) dates.getNamed("A_Time");
            assertThat(aTime.getTime(),
                    is(20 * HOUR + 8 * MINUTE + 8 * SECOND));
            // HSQLDB stores time with zone. This will depend on your default time zone.
//            assertThat(aTime.toString(), is("20:08:08"));
            ZonedDateTime utcDateTime = localTime.truncatedTo(ChronoUnit.SECONDS)
                    .atDate(LocalDate.now()).atZone(ZoneId.of("UTC"));
            assertThat(dates.getStringNamed("A_Time"),
                    is(utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime().toString()));

            assertThat(dates.getNamed("A_Zoned_Time").getClass(), is(java.time.OffsetTime.class));
            assertThat(dates.getNamed("A_Zoned_Time"),
                    is(offsetTime.truncatedTo(ChronoUnit.SECONDS)));
            assertThat(dates.getStringNamed("A_Zoned_Time"), is("20:08:08+8:00"));

            assertThat(dates.getNamed("A_TimeStamp").getClass(), is(java.sql.Timestamp.class));
            assertThat(Instant.ofEpochMilli(((java.sql.Timestamp) dates.getNamed("A_TimeStamp")).getTime()),
                    is(localDateTime.truncatedTo(ChronoUnit.MILLIS).atZone(ZoneOffset.systemDefault()).toInstant()));
            assertThat(dates.getStringNamed("A_TimeStamp"), is("2008-08-08 20:08:08.034900"));

            assertThat(dates.getNamed("A_Zoned_TimeStamp").getClass(), is(java.time.OffsetDateTime.class));
            assertThat(dates.getNamed("A_Zoned_TimeStamp"),
                    is(offsetDateTime));
            assertThat(dates.getStringNamed("A_Zoned_TimeStamp"), is("2008-08-08 20:08:08.034900+8:00"));
        }

        {
            DidoData dates = reader.get();

            assertThat(dates.hasNamed("A_Date"), is(false));
            assertThat(dates.hasNamed("A_Time"), is(false));
            assertThat(dates.hasNamed("A_Zoned_Time"), is(false));
            assertThat(dates.hasNamed("A_TimeStamp"), is(false));
            assertThat(dates.hasNamed("A_Zoned_TimeStamp"), is(false));
        }

        {
            DidoData dates = reader.get();

            assertThat(Instant.ofEpochMilli(((java.sql.Date)
                            dates.getNamed("A_Date")).getTime()),
                    is(localDate.atStartOfDay(ZoneOffset.systemDefault())
                            .toInstant()));

            assertThat(Instant.ofEpochMilli(((java.sql.Time)
                            dates.getNamed("A_Time")).getTime()),
                    is(localTime.truncatedTo(ChronoUnit.SECONDS).atDate(LocalDate.ofEpochDay(0))
                            .atZone(ZoneOffset.systemDefault()).toInstant()));

            assertThat(dates.getNamed("A_Zoned_Time"),
                    is(offsetTime.truncatedTo(ChronoUnit.SECONDS)));

            assertThat(Instant.ofEpochMilli(((java.sql.Timestamp) dates.getNamed("A_TimeStamp")).getTime()),
                    is(localDateTime.truncatedTo(ChronoUnit.MILLIS).atZone(ZoneOffset.systemDefault()).toInstant()));

            assertThat(dates.getNamed("A_Zoned_TimeStamp"),
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
