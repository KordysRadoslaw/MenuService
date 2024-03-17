package com.restaurantaws.menuservice.model;


import java.security.SecureRandom;
import java.util.Base64;

public class AddOn {
    private String addOnId;

    private String category = "AddOn";
    private String name;

    private String description;
    private String price;
    private String type;

    public AddOn() {
        this.addOnId = generateUniqueToken();
    }

    public AddOn(String addOnId, String category, String name, String description, String price, String type) {
        this.addOnId = addOnId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }
    public AddOn(String category, String name, String description, String price, String type) {
        this.addOnId = generateUniqueToken();
        this.category = category;
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

    public String getCategory() {
        return category;
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
        return "AddOn: " + name + " " + description + " " + price + " " + type;
    }
}
