package com.restaurantaws.menuservice.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantaws.menuservice.model.Category;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.services.AsyncCache;
import com.restaurantaws.menuservice.services.Cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MenuRepositoryImpl implements MenuRepository {

    private final Table menuTable;
    private final Cache<String, Menu> asyncCache;


    public MenuRepositoryImpl(DynamoDB dynamodb) {
        this.asyncCache = new AsyncCache<>();
        this.menuTable = dynamodb.getTable("MenuTable");
    }

    @Override
    public void saveMenu(Menu menu) {

        try {
            Item item = new Item();
            item.withPrimaryKey("menuVersion", menu.getMenuVersion())
                    .withString("date", menu.getDate())
                    .withString("token", menu.getToken())
                    .withList("dishes", convertObjectListToMapList(menu.getDishes()))
                    .withList("drinks", convertObjectListToMapList(menu.getDrinks()))
                    .withList("addsOn", convertObjectListToMapList(menu.getAddsOn()))
//                    .withString("category", menu.getCategory().name())
                    .withLong("timestamp", menu.getTimestamp());

            menuTable.putItem(item);

            asyncCache.put("latestMenu", menu);
        } catch (Exception e) {
            throw new RuntimeException("Error saving menu: " + e);
        }
    }

    @Override
    public Menu getLatestMenuFromDatabase() {
        Iterator<Item> iterator = menuTable.scan(new ScanSpec().withMaxResultSize(1)).iterator();
        try{
            if (iterator.hasNext()) {
                return toItem(iterator.next());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting latest menu from database: " + e);
        }
        return null;
    }

    @Override
    public Menu getLatestMenuFromCache() {
        Menu menu = asyncCache.get("latestMenu");
        return menu;
    }

    private Menu toItem(Item item) {
        Menu menu = new Menu();
        menu.setMenuVersion(item.getDouble("menuVersion"));
        menu.setTimestamp(item.getLong("timestamp"));
        menu.setAddsOn(item.getList("addsOn"));
        menu.setDishes(item.getList("dishes"));
        menu.setDrinks(item.getList("drinks"));
        menu.setDate(item.getString("menuDate"));
        menu.setToken(item.getString("tokenId"));
        return menu;
    }
    private List<Map<String, Object>> convertObjectListToMapList(List<? extends Object> objectList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object obj : objectList) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});

            mapList.add(map);
        }
        return mapList;
    }
}
