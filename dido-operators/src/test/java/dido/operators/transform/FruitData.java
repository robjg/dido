package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

public class FruitData implements ValueFactory<DidoData> {

    public static final String TYPE = "type";
    public static final String QUANTITY = "qty";
    public static final String PRICE = "price";



    private static final DataSchema schema = SchemaBuilder.newInstance()
            .addNamed(TYPE, String.class)
            .addNamed(QUANTITY, int.class)
            .addNamed(PRICE, double.class)
            .build();

    private String type;

    private int qty;

    private double price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public DidoData toValue() throws ArooaConversionException {
        return MapData.newBuilder(schema)
                .withString(TYPE, type)
                .withInt(QUANTITY, qty)
                .withDouble(PRICE, price)
                .build();
    }

    @Override
    public String toString() {
        return "FruitData{" +
                "type='" + type + '\'' +
                ", quantity=" + qty +
                ", price=" + price +
                '}';
    }

}
