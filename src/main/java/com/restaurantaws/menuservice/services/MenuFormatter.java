package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats the menu to make sure it has all the required fields and make easily to generate a a jason in next steps
 */
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

        if (menu.getDishes() != null) {
            List<Dish> dishes = new ArrayList<>(menu.getDishes());
            formattedMenu.setDishes(dishes);
        }

        if (menu.getDrinks() != null) {
            List<Drink> drinks = new ArrayList<>(menu.getDrinks());
            formattedMenu.setDrinks(drinks);
        }

        if (menu.getAddsOn() != null) {
            List<AddOn> addsOn = new ArrayList<>(menu.getAddsOn());
            formattedMenu.setAddsOn(addsOn);
        }

        if(menu.getDishes() == null && menu.getDrinks() == null && menu.getAddsOn() == null){
            //menu cannot be null
            return null;
        }

        return formattedMenu;
    }
}
