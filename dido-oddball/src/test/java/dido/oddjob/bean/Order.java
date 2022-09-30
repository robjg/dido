package dido.oddjob.bean;

import java.util.List;
import java.util.Objects;

public class Order {

    private String orderId;

    private List<OrderLine> orderLines;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) && Objects.equals(orderLines, order.orderLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderLines);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderLines=" + orderLines +
                '}';
    }

    public static class OrderLine {

        private String fruit;

        private int qty;

        public String getFruit() {
            return fruit;
        }

        public void setFruit(String fruit) {
            this.fruit = fruit;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderLine orderLine = (OrderLine) o;
            return qty == orderLine.qty && Objects.equals(fruit, orderLine.fruit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fruit, qty);
        }

        @Override
        public String toString() {
            return "OrderLine{" +
                    "fruit='" + fruit + '\'' +
                    ", qty=" + qty +
                    '}';
        }
    }
}
