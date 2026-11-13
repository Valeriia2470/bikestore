package com.bikestore.model;

public class Bike {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private int stock;

    public Bike(int id, String name, String category, double price, int stock) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is empty");
        if (price < 0) throw new IllegalArgumentException("price < 0");
        if (stock < 0) throw new IllegalArgumentException("stock < 0");
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("stock < 0");
        this.stock = stock;
    }
}