package com.restaurantaws.menuservice.model;



import java.util.List;


public class Menu {

    private double menuVersion;
    private String date;
    private String token;
    private List<Dish> dishes;
    private List<Drink> drinks;
    private List<AddOn> addsOn;
//    private String timestamp;
    private long timestamp;

    public Menu() {
        this.menuVersion = 1;
        incrementMenuVersion();
    }

        public Menu(double menuVersion, String date, String token, List<Dish> dishes, List<Drink> drinks, List<AddOn> addsOn, long timestamp) {
            this.menuVersion = 1;
            this.date = date;
            this.token = token;
            this.dishes = dishes;
            this.drinks = drinks;
            this.addsOn = addsOn;
            this.timestamp = timestamp;
            incrementMenuVersion();
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


    public double getMenuVersion() {
        return menuVersion;
    }

    public void setMenuVersion(double menuVersion) {
        this.menuVersion = menuVersion;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public void incrementMenuVersion() {
        this.menuVersion += 0.1;
    }
}
