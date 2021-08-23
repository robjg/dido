package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapRecord;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

public class FruitData implements ValueFactory<GenericData<String>> {

    public static final String TYPE = "type";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";



    private static final DataSchema<String> schema = SchemaBuilder.forStringFields()
            .addField(TYPE, String.class)
            .addField(QUANTITY, int.class)
            .addField(PRICE, double.class)
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
    public GenericData<String> toValue() throws ArooaConversionException {
        return MapRecord.newBuilder(schema)
                .setString(TYPE, type)
                .setInt(QUANTITY, qty)
                .setDouble(PRICE, price)
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
