package dido.data.mutable;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MalleableArrayDataTest {

    @Test
    void addDisparateIndices() {

        MalleableArrayData test = new MalleableArrayData();

        test.setAt(33, 7);
        test.setAt(3, "Apple");
        test.setAt(75, 37.4);

        assertThat(test, is(DidoData.of("Apple", 7, 37.4)));

        DataSchema expectedSchema = DataSchema.builder()
                .addAt(3, String.class)
                .addAt(33, Integer.class)
                .addAt(75, Double.class)
                .build();

        assertThat(test.getSchema(), is(expectedSchema));
    }

    @Test
    void addPrimitivesNamed() {

        MalleableArrayData test = new MalleableArrayData(2);

        test.setBooleanNamed("Boolean", true);
        test.setByteNamed("Byte", (byte) 80);
        test.setCharNamed("Char", 'A');
        test.setShortNamed("Short", (short) 1600);
        test.setIntNamed("Integer", 32_000);
        test.setLongNamed("Long", 640_000L);
        test.setFloatNamed("Float", 32.64F);
        test.setDoubleNamed("Double", 128.256);
        test.setStringNamed("String", "Foo");

        assertThat(test, is(DidoData.of(true,
                (byte) 80, 'A', (short) 1600, 32_000,
                640_000L, 32.64F, 128.256, "Foo")));

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Boolean", boolean.class)
                .addNamed("Byte", byte.class)
                .addNamed("Char", char.class)
                .addNamed("Short", short.class)
                .addNamed("Integer", int.class)
                .addNamed("Long", long.class)
                .addNamed("Float", float.class)
                .addNamed("Double", double.class)
                .addNamed("String", String.class)
                .build();

        assertThat(test.getSchema(), is(expectedSchema));
    }

    @Test
    void addPrimitivesAt() {

        MalleableArrayData test = new MalleableArrayData();

        test.setBooleanAt(2, true);
        test.setByteAt(4, (byte) 80);
        test.setCharAt(8, 'A');
        test.setShortAt(16, (short) 1600);
        test.setIntAt(32, 32_000);
        test.setLongAt(64, 640_000L);
        test.setFloatAt(128, 32.64F);
        test.setDoubleAt(256, 128.256);
        test.setStringAt(3, "Foo");

        assertThat(test, is(DidoData.of(true, "Foo",
                (byte) 80, 'A', (short) 1600, 32_000,
                640_000L, 32.64F, 128.256)));

        DataSchema expectedSchema = DataSchema.builder()
                .addAt(2, boolean.class)
                .addAt(4, byte.class)
                .addAt(8, char.class)
                .addAt(16, short.class)
                .addAt(32, int.class)
                .addAt(64, long.class)
                .addAt(128, float.class)
                .addAt(256, double.class)
                .addAt(3, String.class)
                .build();

        assertThat(test.getSchema(), is(expectedSchema));
    }

    @Test
    void changeFields() {

        DidoData data = DidoData.withSchema(DataSchema.builder()
                        .addNamed("Boolean", boolean.class)
                        .addNamed("Byte", byte.class) // 2
                        .addNamed("Char", char.class)
                        .addNamed("Short", short.class) // 4
                        .addNamed("Integer", int.class)
                        .addNamed("Long", long.class)  // 6
                        .addNamed("Float", float.class)
                        .addNamed("Double", double.class) // 8
                        .addNamed("Number_1", Number.class)
                        .addNamed("Number_2", Number.class) // 10
                        .addNamed("String", String.class)
                        .addNamed("Date", LocalDate.class) // 12
                        .build())
                .of(true,
                        (byte) 80, 'A',
                        (short) 1600,
                        32_000,
                        640_000L, 32.64F,
                        128.256,
                        1.1,
                        2,
                        "Foo",
                        LocalDate.parse("2025-02-08"));

        MalleableData test = MalleableArrayData.copy(data);

        test.setBooleanNamedAt(2, "Boolean", false); // Changes of index for same name
        test.setByteNamedAt(4, "Byte", (byte) 90); // new field overwriting short
        test.setCharNamedAt(8, "Char", 'B'); // moves to 8
        test.setShortNamedAt(16, "Short", (short) 1700); // new field
        test.setIntNamedAt(5, "An_Integer", 33_000); // Change of name for same index
        test.setLongNamedAt(64, "Long", 650_000L); // move
        test.setFloatNamedAt(128, "Float", 33.64F); // move
        test.setDoubleNamedAt(256, "A_Double", 129.256); // move
        test.setAt(9, 1.2); // Should not change type
        test.setAt(10, "2"); // Should change type
        test.setStringNamedAt(3, "New_String", "Bar"); // New String
        test.setNamed("Date", LocalDateTime.parse("2025-02-08T17:56")); // Change of Type for name

        assertThat(test, is(DidoData.of(
                false, "Bar",
                (byte) 90,
                33_000,
                'B',
                1.2,
                "2",
                "Foo",
                LocalDateTime.parse("2025-02-08T17:56"),
                (short) 1700,
                650_000L,
                33.64F,
                129.256)));

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(2, "Boolean", boolean.class)
                .addNamedAt(4, "Byte", byte.class)
                .addNamedAt(8, "Char", char.class)
                .addNamedAt(16, "Short", short.class)
                .addNamedAt(5, "An_Integer", int.class)
                .addNamedAt(64, "Long", long.class)
                .addNamedAt(128, "Float", float.class)
                .addNamedAt(256, "A_Double", double.class)
                .addNamedAt(9, "Number_1", Number.class)
                .addNamedAt(10, "Number_2", String.class)
                .addNamedAt(3, "New_String", String.class)
                .addNamedAt(11, "String", String.class)
                .addNamedAt(12, "Date", LocalDateTime.class)
                .build();

        assertThat(test.getSchema(), is(expectedSchema));
    }
}