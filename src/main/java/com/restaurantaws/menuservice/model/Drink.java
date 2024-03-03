package com.restaurantaws.menuservice.model;


import java.security.SecureRandom;
import java.util.Base64;

public class Drink {

    private String drinkId;

    private int categoryId = 3;
    private String name;
    private String description;
    private String price;

    private String type;

    public Drink(){
        this.drinkId = generateUniqueToken();
    }

    public Drink(String drinkId, int categoryId, String name, String description, String price, String type) {
        this.drinkId = drinkId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }
    public Drink(int categoryId, String name, String description, String price, String type) {
        this.drinkId = generateUniqueToken();
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if(isValidPrice(price)){
            this.price = price;
        }else{
            throw new IllegalArgumentException("Price must be a positive number");
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    private String generateUniqueToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[12];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private boolean isValidPrice(String price){
        try{
            double parsedPrice = Double.parseDouble(price);
            return parsedPrice >= 0;
        }catch(NumberFormatException e){
            return false;
        }
    }
    public String toString(){
        return "Drink: " + name + " " + description + " " + price + " " + type;
    }
}
