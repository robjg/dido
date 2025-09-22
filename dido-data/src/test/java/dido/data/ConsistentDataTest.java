package dido.data;

import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import dido.data.immutable.NonBoxedData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ConsistentDataTest {

    @Test
    void arrayData() {

        DidoData data = ArrayData.of(true, (byte) 8, 'B', (short) 24, 42, 84L, 24.24F, 42.42);

        allGetters(data);
    }

    @Test
    void mapData() {

        DidoData data = MapData.of(
                "f_1", true,
                "f_2", (byte) 8,
                "f_3", 'B',
                "f_4", (short) 24,
                "f_5", 42,
                "f_6", 84L,
                "f_7", 24.24F,
                "f_8", 42.42);

        allGetters(data);
    }

    @Test
    void nonBoxedData() {

        DidoData data = NonBoxedData.of(true, (byte) 8, 'B', (short) 24, 42, 84L, 24.24F, 42.42);

        allGetters(data);
    }

    void allGetters(DidoData data) {

        assertThat(data.getAt(1), is(true));
        assertThat(data.getBooleanAt(1), is(true));
        assertThat(data.getStringAt(1), is( "true"));

        assertThat(data.getAt(2), is((byte) 8));
        assertThat(data.getByteAt(2), is((byte) 8));
        assertThat(data.getShortAt(2), is((short) 8));
        assertThat(data.getIntAt(2), is(8));
        assertThat(data.getLongAt(2), is( 8L));
        assertThat(data.getFloatAt(2), is( 8.0F));
        assertThat(data.getDoubleAt(2), is( 8.0));
        assertThat(data.getStringAt(2), is( "8"));

        assertThat(data.getAt(3), is('B'));
        assertThat(data.getCharAt(3), is('B'));
        assertThat(data.getStringAt(3), is( "B"));

        assertThat(data.getAt(4), is((short) 24));
        assertThat(data.getByteAt(4), is((byte) 24));
        assertThat(data.getShortAt(4), is((short) 24));
        assertThat(data.getIntAt(4), is(24));
        assertThat(data.getLongAt(4), is( 24L));
        assertThat(data.getFloatAt(4), is( 24.0F));
        assertThat(data.getDoubleAt(4), is( 24.0));
        assertThat(data.getStringAt(4), is( "24"));

        assertThat(data.getAt(5), is(42));
        assertThat(data.getByteAt(5), is((byte) 42));
        assertThat(data.getShortAt(5), is((short) 42));
        assertThat(data.getIntAt(5), is(42));
        assertThat(data.getLongAt(5), is( 42L));
        assertThat(data.getFloatAt(5), is( 42.0F));
        assertThat(data.getDoubleAt(5), is( 42.0));
        assertThat(data.getStringAt(5), is( "42"));

        assertThat(data.getAt(6), is(84L));
        assertThat(data.getByteAt(6), is((byte) 84));
        assertThat(data.getShortAt(6), is((short) 84));
        assertThat(data.getIntAt(6), is(84));
        assertThat(data.getLongAt(6), is( 84L));
        assertThat(data.getFloatAt(6), is( 84.0F));
        assertThat(data.getDoubleAt(6), is( 84.0));
        assertThat(data.getStringAt(6), is( "84"));

        assertThat(data.getAt(7), is(24.24F));
        assertThat(data.getByteAt(7), is((byte) 24));
        assertThat(data.getShortAt(7), is((short) 24));
        assertThat(data.getIntAt(7), is(24));
        assertThat(data.getLongAt(7), is( 24L));
        assertThat(Math.abs(data.getFloatAt(7) - 24.24F), lessThan(0.001F));
        assertThat(data.getDoubleAt(7), closeTo(24.24, 0.001));
        assertThat(data.getStringAt(7), is( "24.24"));

        assertThat(data.getAt(8), is(42.42));
        assertThat(data.getByteAt(8), is((byte) 42));
        assertThat(data.getShortAt(8), is((short) 42));
        assertThat(data.getIntAt(8), is(42));
        assertThat(data.getLongAt(8), is( 42L));
        assertThat(data.getFloatAt(8), is( 42.42F));
        assertThat(data.getDoubleAt(8), is( 42.42));
        assertThat(data.getStringAt(8), is( "42.42"));

        assertThat(data.getNamed("f_1"), is(true));
        assertThat(data.getBooleanNamed("f_1"), is(true));
        assertThat(data.getStringNamed("f_1"), is( "true"));

        assertThat(data.getNamed("f_2"), is((byte) 8));
        assertThat(data.getByteNamed("f_2"), is((byte) 8));
        assertThat(data.getShortNamed("f_2"), is((short) 8));
        assertThat(data.getIntNamed("f_2"), is(8));
        assertThat(data.getLongNamed("f_2"), is( 8L));
        assertThat(data.getFloatNamed("f_2"), is( 8.0F));
        assertThat(data.getDoubleNamed("f_2"), is( 8.0));
        assertThat(data.getStringNamed("f_2"), is( "8"));

        assertThat(data.getNamed("f_3"), is('B'));
        assertThat(data.getCharNamed("f_3"), is('B'));
        assertThat(data.getStringNamed("f_3"), is( "B"));

        assertThat(data.getNamed("f_4"), is((short) 24));
        assertThat(data.getByteNamed("f_4"), is((byte) 24));
        assertThat(data.getShortNamed("f_4"), is((short) 24));
        assertThat(data.getIntNamed("f_4"), is(24));
        assertThat(data.getLongNamed("f_4"), is( 24L));
        assertThat(data.getFloatNamed("f_4"), is( 24.0F));
        assertThat(data.getDoubleNamed("f_4"), is( 24.0));
        assertThat(data.getStringNamed("f_4"), is( "24"));

        assertThat(data.getNamed("f_5"), is(42));
        assertThat(data.getByteNamed("f_5"), is((byte) 42));
        assertThat(data.getShortNamed("f_5"), is((short) 42));
        assertThat(data.getIntNamed("f_5"), is(42));
        assertThat(data.getLongNamed("f_5"), is( 42L));
        assertThat(data.getFloatNamed("f_5"), is( 42.0F));
        assertThat(data.getDoubleNamed("f_5"), is( 42.0));
        assertThat(data.getStringNamed("f_5"), is( "42"));

        assertThat(data.getNamed("f_6"), is(84L));
        assertThat(data.getByteNamed("f_6"), is((byte) 84));
        assertThat(data.getShortNamed("f_6"), is((short) 84));
        assertThat(data.getIntNamed("f_6"), is(84));
        assertThat(data.getLongNamed("f_6"), is( 84L));
        assertThat(data.getFloatNamed("f_6"), is( 84.0F));
        assertThat(data.getDoubleNamed("f_6"), is( 84.0));
        assertThat(data.getStringNamed("f_6"), is( "84"));

        assertThat(data.getNamed("f_7"), is(24.24F));
        assertThat(data.getByteNamed("f_7"), is((byte) 24));
        assertThat(data.getShortNamed("f_7"), is((short) 24));
        assertThat(data.getIntNamed("f_7"), is(24));
        assertThat(data.getLongNamed("f_7"), is( 24L));
        assertThat(Math.abs(data.getFloatNamed("f_7") - 24.24F), lessThan(0.001F));
        assertThat(data.getDoubleNamed("f_7"), closeTo(24.24, 0.001));
        assertThat(data.getStringNamed("f_7"), is( "24.24"));

        assertThat(data.getNamed("f_8"), is(42.42));
        assertThat(data.getByteNamed("f_8"), is((byte) 42));
        assertThat(data.getShortNamed("f_8"), is((short) 42));
        assertThat(data.getIntNamed("f_8"), is(42));
        assertThat(data.getLongNamed("f_8"), is( 42L));
        assertThat(data.getFloatNamed("f_8"), is( 42.42F));
        assertThat(data.getDoubleNamed("f_8"), is( 42.42));
        assertThat(data.getStringNamed("f_8"), is( "42.42"));

        ReadSchema readSchema = ReadSchema.from(data.getSchema());

        FieldGetter g1 = readSchema.getFieldGetterAt(1);

        assertThat(g1.get(data), is(true));
        assertThat(g1.getBoolean(data), is(true));
        assertThat(g1.getString(data), is( "true"));

        FieldGetter g2 = readSchema.getFieldGetterAt(2);

        assertThat(g2.get(data), is((byte) 8));
        assertThat(g2.getByte(data), is((byte) 8));
        assertThat(g2.getShort(data), is((short) 8));
        assertThat(g2.getInt(data), is(8));
        assertThat(g2.getLong(data), is( 8L));
        assertThat(g2.getFloat(data), is( 8.0F));
        assertThat(g2.getDouble(data), is( 8.0));
        assertThat(g2.getString(data), is( "8"));

        FieldGetter g3 = readSchema.getFieldGetterAt(3);

        assertThat(g3.get(data), is('B'));
        assertThat(g3.getChar(data), is('B'));
        assertThat(g3.getString(data), is( "B"));

        FieldGetter g4 = readSchema.getFieldGetterAt(4);

        assertThat(g4.get(data), is((short) 24));
        assertThat(g4.getByte(data), is((byte) 24));
        assertThat(g4.getShort(data), is((short) 24));
        assertThat(g4.getInt(data), is(24));
        assertThat(g4.getLong(data), is( 24L));
        assertThat(g4.getFloat(data), is( 24.0F));
        assertThat(g4.getDouble(data), is( 24.0));
        assertThat(g4.getString(data), is( "24"));

        FieldGetter g5 = readSchema.getFieldGetterAt(5);

        assertThat(g5.get(data), is(42));
        assertThat(g5.getByte(data), is((byte) 42));
        assertThat(g5.getShort(data), is((short) 42));
        assertThat(g5.getInt(data), is(42));
        assertThat(g5.getLong(data), is( 42L));
        assertThat(g5.getFloat(data), is( 42.0F));
        assertThat(g5.getDouble(data), is( 42.0));
        assertThat(g5.getString(data), is( "42"));

        FieldGetter g6 = readSchema.getFieldGetterAt(6);

        assertThat(g6.get(data), is(84L));
        assertThat(g6.getByte(data), is((byte) 84));
        assertThat(g6.getShort(data), is((short) 84));
        assertThat(g6.getInt(data), is(84));
        assertThat(g6.getLong(data), is( 84L));
        assertThat(g6.getFloat(data), is( 84.0F));
        assertThat(g6.getDouble(data), is( 84.0));
        assertThat(g6.getString(data), is( "84"));

        FieldGetter g7 = readSchema.getFieldGetterAt(7);

        assertThat(g7.get(data), is(24.24F));
        assertThat(g7.getByte(data), is((byte) 24));
        assertThat(g7.getShort(data), is((short) 24));
        assertThat(g7.getInt(data), is(24));
        assertThat(g7.getLong(data), is( 24L));
        assertThat(Math.abs(g7.getFloat(data) - 24.24F), lessThan(0.001F));
        assertThat(g7.getDouble(data), closeTo(24.24, 0.001));
        assertThat(g7.getString(data), is( "24.24"));

        FieldGetter g8 = readSchema.getFieldGetterAt(8);

        assertThat(g8.get(data), is(42.42));
        assertThat(g8.getByte(data), is((byte) 42));
        assertThat(g8.getShort(data), is((short) 42));
        assertThat(g8.getInt(data), is(42));
        assertThat(g8.getLong(data), is( 42L));
        assertThat(g8.getFloat(data), is( 42.42F));
        assertThat(g8.getDouble(data), is( 42.42));
        assertThat(g8.getString(data), is( "42.42"));

        FieldGetter gf1 = readSchema.getFieldGetterNamed("f_1");

        assertThat(gf1.get(data), is(true));
        assertThat(gf1.getBoolean(data), is(true));
        assertThat(gf1.getString(data), is( "true"));

        FieldGetter gf2 = readSchema.getFieldGetterNamed("f_2");

        assertThat(gf2.get(data), is((byte) 8));
        assertThat(gf2.getByte(data), is((byte) 8));
        assertThat(gf2.getShort(data), is((short) 8));
        assertThat(gf2.getInt(data), is(8));
        assertThat(gf2.getLong(data), is( 8L));
        assertThat(gf2.getFloat(data), is( 8.0F));
        assertThat(gf2.getDouble(data), is( 8.0));
        assertThat(gf2.getString(data), is( "8"));

        FieldGetter gf3 = readSchema.getFieldGetterNamed("f_3");

        assertThat(gf3.get(data), is('B'));
        assertThat(gf3.getChar(data), is('B'));
        assertThat(gf3.getString(data), is( "B"));

        FieldGetter gf4 = readSchema.getFieldGetterNamed("f_4");

        assertThat(gf4.get(data), is((short) 24));
        assertThat(gf4.getByte(data), is((byte) 24));
        assertThat(gf4.getShort(data), is((short) 24));
        assertThat(gf4.getInt(data), is(24));
        assertThat(gf4.getLong(data), is( 24L));
        assertThat(gf4.getFloat(data), is( 24.0F));
        assertThat(gf4.getDouble(data), is( 24.0));
        assertThat(gf4.getString(data), is( "24"));

        FieldGetter gf5 = readSchema.getFieldGetterNamed("f_5");

        assertThat(gf5.get(data), is(42));
        assertThat(gf5.getByte(data), is((byte) 42));
        assertThat(gf5.getShort(data), is((short) 42));
        assertThat(gf5.getInt(data), is(42));
        assertThat(gf5.getLong(data), is( 42L));
        assertThat(gf5.getFloat(data), is( 42.0F));
        assertThat(gf5.getDouble(data), is( 42.0));
        assertThat(gf5.getString(data), is( "42"));

        FieldGetter gf6 = readSchema.getFieldGetterNamed("f_6");

        assertThat(gf6.get(data), is(84L));
        assertThat(gf6.getByte(data), is((byte) 84));
        assertThat(gf6.getShort(data), is((short) 84));
        assertThat(gf6.getInt(data), is(84));
        assertThat(gf6.getLong(data), is( 84L));
        assertThat(gf6.getFloat(data), is( 84.0F));
        assertThat(gf6.getDouble(data), is( 84.0));
        assertThat(gf6.getString(data), is( "84"));

        FieldGetter gf7 = readSchema.getFieldGetterNamed("f_7");

        assertThat(gf7.get(data), is(24.24F));
        assertThat(gf7.getByte(data), is((byte) 24));
        assertThat(gf7.getShort(data), is((short) 24));
        assertThat(gf7.getInt(data), is(24));
        assertThat(gf7.getLong(data), is( 24L));
        assertThat(Math.abs(gf7.getFloat(data) - 24.24F), lessThan(0.001F));
        assertThat(gf7.getDouble(data), closeTo(24.24, 0.001));
        assertThat(gf7.getString(data), is( "24.24"));

        FieldGetter gf8 = readSchema.getFieldGetterNamed("f_8");

        assertThat(gf8.get(data), is(42.42));
        assertThat(gf8.getByte(data), is((byte) 42));
        assertThat(gf8.getShort(data), is((short) 42));
        assertThat(gf8.getInt(data), is(42));
        assertThat(gf8.getLong(data), is( 42L));
        assertThat(gf8.getFloat(data), is( 42.42F));
        assertThat(gf8.getDouble(data), is( 42.42));
        assertThat(gf8.getString(data), is( "42.42"));
    }
}
