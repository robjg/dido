package dido.examples.objects;

public class FruitBean {
    private String fruit;
    private int qty;
    private double price;

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "FruitBean{" +
                "fruit='" + fruit + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}
