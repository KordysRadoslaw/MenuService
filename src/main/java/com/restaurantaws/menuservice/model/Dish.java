package com.restaurantaws.menuservice.model;


import java.security.SecureRandom;
import java.util.Base64;

public class Dish {

    private String dishId;

    private int categoryId = 2;
    private String name;
    private String price;
    private String type;

    private String description;


    public Dish(){
        this.dishId = generateUniqueToken();
    }


    public Dish(String dishId, int categoryId, String name, String price, String type, String description) {
        this.dishId = dishId;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    public Dish(int categoryId, String name, String price, String type, String description) {
        this.dishId = generateUniqueToken();
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    private String generateUniqueToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[12]; // Możesz dostosować długość identyfikatora
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
        return "Dish: " + name + " Price: " + price;
    }
}
