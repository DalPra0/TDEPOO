package com.drinks;

import java.util.List;

public class Drink {
    private int id;
    private String name;
    private String photo;
    private double price;
    private String location;
    private boolean homemade;
    private List<Ingredient> ingredients;

    public Drink() {}

    public Drink(int id, String name, String photo, double price, String location, boolean homemade, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.location = location;
        this.homemade = homemade;
        this.ingredients = ingredients;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isHomemade() { return homemade; }
    public void setHomemade(boolean homemade) { this.homemade = homemade; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}

