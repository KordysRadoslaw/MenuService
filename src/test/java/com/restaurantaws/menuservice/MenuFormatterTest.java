package com.restaurantaws.menuservice;

import com.restaurantaws.menuservice.model.AddOn;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.services.MenuFormatter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MenuFormatterTest {

    @Test
    public void testFormatMenu() {
        // Arrange
        MenuFormatter menuFormatter = new MenuFormatter();
        List<Dish> dishes = List.of(new Dish("dishId", "Category", "dishName", "price", "type", "description"));
        List<Drink> drinks = List.of(new Drink("drinkId", "Category", "drinkName", "price", "type", "description"));
        List<AddOn> addOns = List.of(new AddOn("addOnId", "Category", "addOnName", "price", "type", "description"));
        Menu menu = new Menu(1.1, "2021-01-01", "token", dishes, drinks, addOns, null, 123123);
        // Act
        Menu formattedMenu = menuFormatter.formatMenu(menu);
        // Assert
        assertNotNull(formattedMenu);
    }

    @Test
    public void testMenuFormatWhenMenuIsEmpty(){
        // Arrange
        MenuFormatter menuFormatter = new MenuFormatter();
        Menu menu = new Menu();
        // Act
        Menu formattedMenu = menuFormatter.formatMenu(menu);
        // Assert
        assertNull(formattedMenu);
    }
}
