package dido.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DataReaderTest {

    static final int SAMPLE_SIZE = 10; //_000_000;

    @Test
    void howMuchBetterAreFieldGettersWithMapData() {

        System.out.println("Map Data");
        System.out.println("--------");
        doTest(new MapDataDataFactoryProvider());
    }

    @Test
    void howMuchBetterAreFieldGettersWithArrayData() {

        System.out.println("Array Data");
        System.out.println("----------");
        doTest(new ArrayDataDataFactoryProvider());
    }

    @Test
    void howMuchBetterAreFieldGettersWithNonBoxedData() {

        System.out.println("None Boxed Data");
        System.out.println("---------------");
        doTest(new NonBoxedDataFactoryProvider());
    }

    void doTest(DataFactoryProvider<?> dataFactoryProvider) {

        WriteSchema schema = SchemaBuilder.builderFor(dataFactoryProvider.getSchemaFactory())
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        DataFactory<?> dataFactory = dataFactoryProvider.provideFactory(schema);

        List<DidoData> data = writeWithFieldSetters(dataFactory);

        System.out.println(withFieldGetters(
                ReadStrategy.fromSchema(dataFactory.getSchema()), data));
        System.out.println(withNamedGetters(data));
        System.out.println(withIndexedGetters(data));
        System.out.println(withIndexedGetters(data));
        System.out.println(withNamedGetters(data));
        System.out.println(withFieldGetters(
                ReadStrategy.fromSchema(dataFactory.getSchema()), data));
    }

    static List<DidoData> writeWithFieldSetters(DataFactory<?> dataFactory) {

        WriteStrategy schema = WriteStrategy.fromSchema(dataFactory.getSchema());

        FieldSetter fruitSetter = schema.getFieldSetterNamed("Fruit");
        FieldSetter quantitySetter = schema.getFieldSetterNamed("Quantity");
        FieldSetter priceSetter = schema.getFieldSetterNamed("Price");

        List<DidoData> data = new ArrayList<>(SAMPLE_SIZE);

        long millis = System.currentTimeMillis();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            WritableData writable = dataFactory.getWritableData();
            fruitSetter.setString(writable,"Apple");
            quantitySetter.setInt(writable, (int) (Math.random() * 10));
            priceSetter.setDouble(writable, Math.random() * 30);
            data.add(dataFactory.toData());
        }

        System.out.println("Write: " + (System.currentTimeMillis() - millis));

        return data;
    }

    static double withFieldGetters(ReadStrategy readStrategy, List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        FieldGetter quantityGetter = readStrategy.getFieldGetterNamed("Quantity");
        FieldGetter priceGetter = readStrategy.getFieldGetterNamed("Price");

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (quantityGetter.getInt(data) * priceGetter.getDouble(data))) / 2.0;
        }

        System.out.println("Field Getters: " + (System.currentTimeMillis() - millis));

        return avg;
    }

    static double withNamedGetters(List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (data.getIntNamed("Quantity") * data.getDoubleNamed("Price"))) / 2.0;
        }

        System.out.println("Named Getters: " + (System.currentTimeMillis() - millis));

        return avg;
    }

    static double withIndexedGetters(List<DidoData> list) {

        System.gc();

        double avg = 0.0;

        long millis = System.currentTimeMillis();

        for (DidoData data : list) {
            avg = (avg + (data.getIntAt(2) * data.getDoubleAt(3))) / 2.0;
        }

        System.out.println("Indexed Getters: " + (System.currentTimeMillis() - millis));

        return avg;
    }
}
