package dido.data;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

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

        GenericData<String> stringData = GenericMapData.<String>newBuilderNoSchema()
                .set("Object", Collections.singletonList("Foo"))
                .setString("String", "Hello")
                .setBoolean("Boolean", true)
                .setByte("Byte", (byte) 32)
                .setChar("Char", 'A')
                .setShort("Short", (short) 42)
                .setInt("Int", 42)
                .setLong("Long", 42L)
                .setFloat("Float", 42.42F)
                .setDouble("Double", 42.42)
                .build();

        EnumData<Fields> enumData = EnumData.fromStringData(stringData, Fields.class);

        assertThat(enumData.getOf(Fields.Object), is(Collections.singletonList("Foo")));
        assertThat(enumData.getAt(1), is(Collections.singletonList("Foo")));
        assertThat(enumData.getOfAs(Fields.Object, List.class), is(Collections.singletonList("Foo")));
        assertThat(enumData.getAtAs(1, List.class), is(Collections.singletonList("Foo")));
        assertThat(enumData.getStringOf(Fields.String), is("Hello"));
        assertThat(enumData.getStringAt(2), is("Hello"));
        assertThat(enumData.getBooleanOf(Fields.Boolean), is(true));
        assertThat(enumData.getBooleanAt(3), is(true));
        assertThat(enumData.getByteOf(Fields.Byte), is((byte) 32));
        assertThat(enumData.getByteAt(4), is((byte) 32));
        assertThat(enumData.getCharOf(Fields.Char), is('A'));
        assertThat(enumData.getCharAt(5), is('A'));
        assertThat(enumData.getShortOf(Fields.Short), is((short) 42));
        assertThat(enumData.getShortAt(6), is((short) 42));
        assertThat(enumData.getIntOf(Fields.Int), is(42));
        assertThat(enumData.getIntAt(7), is(42));
        assertThat(enumData.getLongOf(Fields.Long), is(42L));
        assertThat(enumData.getLongAt(8), is(42L));
        assertThat(enumData.getFloatOf(Fields.Float), is(42.42F));
        assertThat(enumData.getFloatAt(9), is(42.42F));
        assertThat(enumData.getDoubleOf(Fields.Double), is(42.42));
        assertThat(enumData.getDoubleAt(10), is(42.42));

    }
}