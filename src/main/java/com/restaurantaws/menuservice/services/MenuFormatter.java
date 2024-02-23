package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFormatter {
    GenerateToken generateToken = new GenerateToken();

    public Menu formatMenu(Menu menu){
        if(menu == null){
            throw new IllegalArgumentException("Menu cannot be null");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String tokenId = generateToken.generateUniqueToken();
        long currentTimeMillis = System.currentTimeMillis();

        menu.setMenuVersion(menu.getMenuVersion() + 0.1);
        menu.setDate(formattedDateTime);
        menu.setToken(tokenId);
        menu.setTimestamp(currentTimeMillis);

        // Mapowanie dań

//        //nizej chyba zle, zamiast mapy Stirng String to mapa Stirng i np. Dish. ale nie jestem pewien, sprawdz jeszcze cala logike
//        List<Dish> dishes = menu.getDishes();
//        if (dishes != null) {
//            for (Dish dish : dishes) {
//                Map<String, String> dishMap = new HashMap<>();
//                dishMap.put("dishId", dish.getDishId());
//                dishMap.put("categoryId", Integer.toString(dish.getCategoryId()));
//                dishMap.put("name", dish.getName());
//                dishMap.put("price", dish.getPrice());
//                dishMap.put("type", dish.getType());
//                dishMap.put("description", dish.getDescription());
//
//            }
//        }

        // mappping dishes
        List<Dish> dishes = menu.getDishes();
        List<Dish> newDishesList = new ArrayList<>();
        if (dishes != null) {
            for (Dish dish : dishes) {
                Dish newDish = new Dish();
                newDish.setDishId(dish.getDishId());
                newDish.setName(dish.getName());
                newDish.setPrice(dish.getPrice());
                newDish.setType(dish.getType());
                newDish.setDescription(dish.getDescription());
                newDishesList.add(newDish);
            }
        }
        menu.setCategory(Category.MAIN_DISH);
        menu.setDishes(newDishesList);


        // Mapowanie napojów
        List<Drink> drinks = menu.getDrinks();
        List<Drink> newDrinksList = new ArrayList<>();
        if (drinks != null) {
            for (Drink drink : drinks) {
                Drink newDrink = new Drink();
                newDrink.setDrinkId(drink.getDrinkId());
                newDrink.setName(drink.getName());
                newDrink.setPrice(drink.getPrice());
                newDrink.setType(drink.getType());
                newDrink.setDescription(drink.getDescription());
                newDrinksList.add(newDrink);
            }
        }
        menu.setCategory(Category.DRINK);
        menu.setDrinks(newDrinksList);

        // Mapowanie dodatków
        List<AddOn> addOns = menu.getAddsOn();
        List<AddOn> newAddOnsList = new ArrayList<>();
        if (addOns != null) {
            for (AddOn addOn : addOns) {
               AddOn newAddOn = new AddOn();
                newAddOn.setAddOnId(addOn.getAddOnId());
                newAddOn.setName(addOn.getName());
                newAddOn.setPrice(addOn.getPrice());
                newAddOn.setType(addOn.getType());
                newAddOn.setDescription(addOn.getDescription());
                newAddOnsList.add(newAddOn);
            }
        }
        menu.setCategory(Category.ADD_ON);
        menu.setAddsOn(newAddOnsList);

        return menu;
    }
}
