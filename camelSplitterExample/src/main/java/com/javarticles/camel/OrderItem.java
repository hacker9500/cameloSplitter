package com.javarticles.camel;

public class OrderItem {
    private Order order;
    private String itemName;
    private int quantity;
    private int price;
    private boolean processed;
    
    public OrderItem(Order order, String itemName, int quantity, int price) {
        this.order = order;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public String getItemName() {
        return itemName;
    }
    
    public void process() {
        processed = true;
    }

    public boolean isProcessed() {
        return processed;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
    
    public String toString() {
        return "Item[" + itemName + ", total price: " + (quantity * price) + ", processed: " + isProcessed() + "]";
    }
    
}
