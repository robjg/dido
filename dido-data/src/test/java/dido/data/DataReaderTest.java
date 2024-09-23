package dido.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DataReaderTest {

    static final int SAMPLE_SIZE = 10; // _000_000;

    @Test
    void howMuchBetterIsDataReaderWithMapData() {

        System.out.println("Map Data");
        System.out.println("--------");
        doTest(MapData.schemaFactory());
    }

    @Test
    void howMuchBetterIsDataReaderWithArrayData() {

        System.out.println("Array Data");
        System.out.println("----------");
        doTest(ArrayData.schemaFactory());
    }

    @Test
    void howMuchBetterIsDataReaderWithNonBoxedData() {

        System.out.println("None Boxed Data");
        System.out.println("---------------");
        doTest(NonBoxedData.schemaFactory());
    }

    void doTest(WritableSchemaFactory<?> schemaFactory) {

        WritableSchema<?> schema = SchemaBuilder.builderFor(schemaFactory)
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DataFactory<?> dataFactory = schema.newDataFactory();
        Setter fruitSetter = dataFactory.getSetterNamed("Fruit");
        Setter quantitySetter = dataFactory.getSetterNamed("Quantity");
        Setter priceSetter = dataFactory.getSetterNamed("Price");

        List<DidoData> data = new ArrayList<>(SAMPLE_SIZE);
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            fruitSetter.setString("Apple");
            quantitySetter.setInt((int) (Math.random() * 10));
            priceSetter.setDouble( Math.random() * 30);
            data.add(dataFactory.toData());
        }

        System.out.println(withReader(schema, data));
        System.out.println(withNamedGetter(data));
        System.out.println(withIntGetter(data));
        System.out.println(withIntGetter(data));
        System.out.println(withNamedGetter(data));
        System.out.println(withReader(schema, data));
    }

    static double withReader(ReadableSchema schema, List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        Getter quantityGetter = schema.getDataGetterNamed("Quantity");
        Getter priceGetter = schema.getDataGetterNamed("Price");

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (quantityGetter.getInt(data) * priceGetter.getDouble(data))) / 2.0;
        }

        System.out.println("Reader: " + (System.currentTimeMillis() - millis));

        return avg;
    }

    static double withNamedGetter(List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (data.getIntNamed("Quantity") * data.getDoubleNamed("Price"))) / 2.0;
        }

        System.out.println("Named Getter: " + (System.currentTimeMillis() - millis));

        return avg;
    }

    static double withIntGetter(List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (data.getIntAt(2) * data.getDoubleAt(3))) / 2.0;
        }

        System.out.println("Int Getter: " + (System.currentTimeMillis() - millis));

        return avg;
    }
}
