package com.restaurantaws.menuservice.model;


import java.security.SecureRandom;
import java.util.Base64;

public class AddOn {
    private String addOnId;

    private int categoryId = 1;
    private String name;

    private String description;
    private String price;
    private String type;

    public AddOn() {
        this.addOnId = generateUniqueToken();
    }

    public AddOn(String addOnId, int categoryId, String name, String description, String price, String type) {
        this.addOnId = addOnId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }
    public AddOn(int categoryId, String name, String description, String price, String type) {
        this.addOnId = generateUniqueToken();
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }

    public String getAddOnId() {
        return addOnId;
    }

    public void setAddOnId(String addOnId) {
        this.addOnId = addOnId;
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
}
