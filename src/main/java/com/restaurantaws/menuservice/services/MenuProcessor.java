package com.restaurantaws.menuservice.services;


import com.restaurantaws.menuservice.model.AddOn;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuProcessor {

    public Map<String, AttributeValue> menuProcessor(Menu menu) {
        if(menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String tokenId = generateUniqueToken();
        long currentTimeMillis = System.currentTimeMillis();

        //Menu processedMenu = new Menu();


        Map<String, AttributeValue> itemValues = new HashMap<>();

        itemValues.put("menuVersion", AttributeValue.builder().n(Double.toString(menu.getMenuVersion())).build());
        itemValues.put("menuDate", AttributeValue.builder().s(formattedDateTime).build());
        itemValues.put("tokenId", AttributeValue.builder().s(tokenId).build());
        itemValues.put("timestamp", AttributeValue.builder().n(Long.toString(currentTimeMillis)).build());
        itemValues.put("dishes", AttributeValue.builder().l(getDishesAttributeValues(menu.getDishes())).build());
        itemValues.put("drinks", AttributeValue.builder().l(getDrinksAttributeValues(menu.getDrinks())).build());
        itemValues.put("addsOn", AttributeValue.builder().l(getAddsOnAttributeValues(menu.getAddsOn())).build());

        return itemValues;
    }

    private List<AttributeValue> getDishesAttributeValues(List<Dish> dishes) {
        List<AttributeValue> dishAttributeValues = new ArrayList<>();
        for (Dish dish : dishes) {
            Map<String, AttributeValue> dishMap = new HashMap<>();
            dishMap.put("dishId", AttributeValue.builder().s(dish.getDishId()).build());
            dishMap.put("categoryId", AttributeValue.builder().n(Integer.toString(dish.getCategoryId())).build());
            dishMap.put("name", AttributeValue.builder().s(dish.getName()).build());
            dishMap.put("price", AttributeValue.builder().s(dish.getPrice()).build());
            dishMap.put("type", AttributeValue.builder().s(dish.getType()).build());
            dishMap.put("description", AttributeValue.builder().s(dish.getDescription()).build());

            dishAttributeValues.add(AttributeValue.builder().m(dishMap).build());
        }
        return dishAttributeValues;
    }

    private List<AttributeValue> getDrinksAttributeValues(List<Drink> drinks) {
        List<AttributeValue> drinkAttributeValues = new ArrayList<>();
        for (Drink drink : drinks) {
            Map<String, AttributeValue> drinkMap = new HashMap<>();
            drinkMap.put("drinkId", AttributeValue.builder().s(drink.getDrinkId()).build());
            drinkMap.put("categoryId", AttributeValue.builder().n(Integer.toString(drink.getCategoryId())).build());
            drinkMap.put("name", AttributeValue.builder().s(drink.getName()).build());
            drinkMap.put("price", AttributeValue.builder().s(drink.getPrice()).build());
            drinkMap.put("type", AttributeValue.builder().s(drink.getType()).build());
            drinkMap.put("description", AttributeValue.builder().s(drink.getDescription()).build());

            drinkAttributeValues.add(AttributeValue.builder().m(drinkMap).build());
        }
        return drinkAttributeValues;
    }

    private List<AttributeValue> getAddsOnAttributeValues(List<AddOn> addsOn) {
        List<AttributeValue> addOnAttributeValues = new ArrayList<>();
        for (AddOn addOn : addsOn) {
            Map<String, AttributeValue> addOnMap = new HashMap<>();
            addOnMap.put("addOnId", AttributeValue.builder().s(addOn.getAddOnId()).build());
            addOnMap.put("categoryId", AttributeValue.builder().n(Integer.toString(addOn.getCategoryId())).build());
            addOnMap.put("name", AttributeValue.builder().s(addOn.getName()).build());
            addOnMap.put("price", AttributeValue.builder().s(addOn.getPrice()).build());
            addOnMap.put("type", AttributeValue.builder().s(addOn.getType()).build());
            addOnMap.put("description", AttributeValue.builder().s(addOn.getDescription()).build());

            addOnAttributeValues.add(AttributeValue.builder().m(addOnMap).build());
        }
        return addOnAttributeValues;
    }
    public String generateUniqueToken() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating unique token: " + e);
        }
    }
}
