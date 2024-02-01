package com.restaurantaws.menuservice.model;



import java.util.ArrayList;
import java.util.List;


public class Category {
    private String name;
    private List<Dish> dishes;
    private List<Drink> drinks;
    private List<AddOn> addsOn;


    public Category() {
    }


    public Category(String name, List<Dish> dishes, List<Drink> drinks, List<AddOn> addsOn) {
        this.name = name;
        //if the list is null i set empty list to dont get null pointer exception
        this.dishes = (dishes != null) ? dishes : new ArrayList<>();
        this.drinks = (drinks != null) ? drinks : new ArrayList<>();
        this.addsOn = (addsOn != null) ? addsOn : new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }

    public List<AddOn> getAddsOn() {
        return addsOn;
    }

    public void setAddsOn(List<AddOn> addsOn) {
        this.addsOn = addsOn;
    }
}
