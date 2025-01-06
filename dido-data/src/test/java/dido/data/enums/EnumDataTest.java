package dido.data.enums;

import dido.data.DidoData;
import dido.data.MapData;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EnumDataTest {

    enum Fields {
        Object,
        String,
        Boolean,
        Byte,
        Char,
        Short,
        Int,
        Long,
        Float,
        Double
    }

    @Test
    void testFromStringFieldsAndBack() {

        DidoData stringData = MapData.builder()
                .with("Object", Collections.singletonList("Foo"))
                .withString("String", "Hello")
                .withBoolean("Boolean", true)
                .withByte("Byte", (byte) 32)
                .withChar("Char", 'A')
                .withShort("Short", (short) 42)
                .withInt("Int", 42)
                .withLong("Long", 42L)
                .withFloat("Float", 42.42F)
                .withDouble("Double", 42.42)
                .build();

        EnumData<Fields> enumData = EnumData.fromDidoData(stringData, Fields.class);

        assertThat(enumData.get(Fields.Object), is(Collections.singletonList("Foo")));
        assertThat(enumData.getAt(1), is(Collections.singletonList("Foo")));
        assertThat(enumData.getAt(1), is(Collections.singletonList("Foo")));
        assertThat(enumData.getString(Fields.String), is("Hello"));
        assertThat(enumData.getStringAt(2), is("Hello"));
        assertThat(enumData.getBoolean(Fields.Boolean), is(true));
        assertThat(enumData.getBooleanAt(3), is(true));
        assertThat(enumData.getByte(Fields.Byte), is((byte) 32));
        assertThat(enumData.getByteAt(4), is((byte) 32));
        assertThat(enumData.getChar(Fields.Char), is('A'));
        assertThat(enumData.getCharAt(5), is('A'));
        assertThat(enumData.getShort(Fields.Short), is((short) 42));
        assertThat(enumData.getShortAt(6), is((short) 42));
        assertThat(enumData.getInt(Fields.Int), is(42));
        assertThat(enumData.getIntAt(7), is(42));
        assertThat(enumData.getLong(Fields.Long), is(42L));
        assertThat(enumData.getLongAt(8), is(42L));
        assertThat(enumData.getFloat(Fields.Float), is(42.42F));
        assertThat(enumData.getFloatAt(9), is(42.42F));
        assertThat(enumData.getDouble(Fields.Double), is(42.42));
        assertThat(enumData.getDoubleAt(10), is(42.42));

    }
}