package com.restaurantaws.menuservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "MenuTable")
public class Menu {

    private double menuVersion;
    private String date;
    private String token;
    private List<Dish> dishes;
    private List<Drink> drinks;
    private List<AddOn> addsOn;

    private Category category;
    private long timestamp;

    public Menu() {
        this.menuVersion = 1;
    }

    public Menu(double menuVersion, String date, String token, List<Dish> dishes, List<Drink> drinks, List<AddOn> addsOn, long timestamp) {
        this.menuVersion = menuVersion;
        this.date = date;
        this.token = token;
        this.dishes = dishes;
        this.drinks = drinks;
        this.addsOn = addsOn;
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute
    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    @DynamoDBAttribute
    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }

    @DynamoDBAttribute
    public List<AddOn> getAddsOn() {
        return addsOn;
    }

    public void setAddsOn(List<AddOn> addsOn) {
        this.addsOn = addsOn;
    }

    @DynamoDBAttribute
    public double getMenuVersion() {
        return menuVersion;
    }

    public void setMenuVersion(double menuVersion) {
        this.menuVersion = menuVersion;
    }

    @DynamoDBAttribute
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @DynamoDBAttribute
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void incrementMenuVersion() {
        this.menuVersion += 0.1;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
