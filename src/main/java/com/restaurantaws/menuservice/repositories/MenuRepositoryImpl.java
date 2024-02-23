package com.restaurantaws.menuservice.repositories;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.restaurantaws.menuservice.model.Category;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MenuRepositoryImpl implements MenuRepository{


    private final Table menuTable;

    public MenuRepositoryImpl(Table menuTable) {
        this.menuTable = menuTable;
    }
    @Override
    public void saveMenu(Menu menu) {
        try{
            Item item = new Item();
            item.withPrimaryKey("menuVersion", menu.getMenuVersion())
                    .withString("date", menu.getDate())
                    .withString("token", menu.getToken())
                    .withList("dishes", menu.getDishes())
                    .withList("drinks", menu.getDrinks())
                    .withList("addsOn", menu.getAddsOn())
                    .withString("category", menu.getCategory().name())
                    .withLong("timestamp", menu.getTimestamp());

        } catch (Exception e){
            throw new RuntimeException("Error saving menu: " + e);
        }

    }

    @Override
    public Menu getLatestMenuFromDatabase() {
        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(1);

        Iterator<Item> iterator = menuTable.scan(scanSpec).iterator();
        Menu latestMenu = null;
        while(iterator.hasNext()){
            Item item = iterator.next();
            latestMenu = toItem(item);
        }
        return latestMenu;
    }

    @Override
    public Menu getLatestMenuFromCache() {
        return null;
    }

    private Menu toItem(Item item) {
        Menu menu = new Menu();
        menu.setMenuVersion(item.getDouble("menuVersion"));
        menu.setTimestamp(item.getLong("timestamp"));
        menu.setDishes(item.getList("dishes"));
        menu.setDrinks(item.getList("drinks"));
        menu.setAddsOn(item.getList("addsOn"));
        menu.setCategory(Category.valueOf(item.getString("category")));
        menu.setDate(item.getString("date"));
        return menu;
    }
}
