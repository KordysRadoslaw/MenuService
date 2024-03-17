package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MenuFormatter {
    private final GenerateToken generateToken = new GenerateToken();

    public Menu formatMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }

        Menu formattedMenu = new Menu();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String tokenId = generateToken.generateUniqueToken();
        long currentTimeMillis = System.currentTimeMillis();

        formattedMenu.setMenuVersion(menu.getMenuVersion() + 0.1);
        formattedMenu.setDate(formattedDateTime);
        formattedMenu.setToken(tokenId);
        formattedMenu.setTimestamp(currentTimeMillis);

        // the copy of the list is created to avoid modifying the original list
        List<Dish> dishes = new ArrayList<>(menu.getDishes());
        List<Drink> drinks = new ArrayList<>(menu.getDrinks());
        List<AddOn> addsOn = new ArrayList<>(menu.getAddsOn());

        // set the values of the original list to the formatted menu
        formattedMenu.setDishes(dishes);
        formattedMenu.setDrinks(drinks);
        formattedMenu.setAddsOn(addsOn);

        return formattedMenu;
    }
}
