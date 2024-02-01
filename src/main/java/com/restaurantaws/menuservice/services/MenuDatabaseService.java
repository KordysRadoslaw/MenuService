package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.AddOn;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuDatabaseService {


    private final String tableName;
    private final DynamoDbClient ddbClient;
    private final MenuCache menuCache;

    private MenuProcessor menuProcessor;

    private S3Service s3Service;



    public MenuDatabaseService(DynamoDbClient ddbClient, String tableName, MenuCache menuCache, MenuProcessor menuProcessor, S3Service s3Service) {
        this.tableName = tableName;
        this.ddbClient = ddbClient;
        this.menuCache = menuCache;
        this.menuProcessor = menuProcessor;
        this.s3Service = s3Service;
    }

    public boolean saveData(Menu menu) {

        Map<String, AttributeValue> itemValues = menuProcessor.menuProcessor(menu);

        try {
            ddbClient.putItem(PutItemRequest.builder().tableName(tableName).item(itemValues).build());
            s3Service.saveToS3(menu);

            return true;
        } catch (DynamoDbException e) {
            throw new RuntimeException("DynamoDB exception: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving data to DynamoDB: " + e.getMessage(), e);
        }
    }

    public Menu loadLatestMenuFromDatabase() {
        String cacheKey = "latestMenu";
        long currentTimeMillis = System.currentTimeMillis();
        String timestamString = Long.toString(currentTimeMillis);
        Menu cachedMenu = menuCache.getMenu(cacheKey, currentTimeMillis);
        if (cachedMenu != null) {
            return cachedMenu;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        // the map with a filter
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":menuDate", AttributeValue.builder().s(formattedDateTime).build());

        // scan
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression("menuDate = :menuDate")
                .expressionAttributeValues(expressionAttributeValues)
                .build();


        try {
            ScanResponse response = ddbClient.scan(scanRequest);

            if (!response.items().isEmpty()) {
                Map<String, AttributeValue> firstItem = response.items().get(0);
                double menuVersion = Double.parseDouble(firstItem.get("menuVersion").n());
                String tokenId = menuProcessor.generateUniqueToken();

                List<Dish> dishes = getDishesFromMap(firstItem);
                List<Drink> drinks = getDrinksFromMap(firstItem);
                List<AddOn> addsOn = getAddsOnFromMap(firstItem);


                Menu menu = new Menu(menuVersion, formattedDateTime, tokenId,
                        dishes, drinks, addsOn, currentTimeMillis);
                menuCache.putMenu(cacheKey, menu, currentTimeMillis);
                return menu;
            }
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }

        return s3Service.loadFromS3();
    }

    public Menu loadLatestMenuFromCache() {
        String cacheKey = "latestMenu";
        long currentTimeMillis = System.currentTimeMillis();
        Menu cachedMenu = menuCache.getMenu(cacheKey, currentTimeMillis);

        if (cachedMenu != null) {
            return cachedMenu;
        }

        return null;
    }
    private List<Dish> getDishesFromMap(Map<String, AttributeValue> itemMap) {
        List<Dish> dishes = new ArrayList<>();
        if (itemMap.containsKey("dishes")) {
            List<AttributeValue> dishAttributeValues = itemMap.get("dishes").l();
            for (AttributeValue dishAttrValue : dishAttributeValues) {
                Map<String, AttributeValue> dishMap = dishAttrValue.m();
                String dishId = dishMap.get("dishId").s();
                int categoryId = Integer.parseInt(dishMap.get("categoryId").n());
                String name = dishMap.get("name").s();
                String price = dishMap.get("price").s();
                String type = dishMap.get("type").s();
                String description = dishMap.get("description").s();

                Dish dish = new Dish(dishId, categoryId, name, price, type, description);
                dishes.add(dish);
            }
        }
        return dishes;
    }

    private List<Drink> getDrinksFromMap(Map<String, AttributeValue> itemMap) {
        List<Drink> drinks = new ArrayList<>();
        if (itemMap.containsKey("drinks")) {
            List<AttributeValue> drinkAttributeValues = itemMap.get("drinks").l();
            for (AttributeValue drinkAttrValue : drinkAttributeValues) {
                Map<String, AttributeValue> drinkMap = drinkAttrValue.m();
                String drinkId = drinkMap.get("drinkId").s();
                int categoryId = Integer.parseInt(drinkMap.get("categoryId").n());
                String name = drinkMap.get("name").s();
                String price = drinkMap.get("price").s();
                String type = drinkMap.get("type").s();
                String description = drinkMap.get("description").s();

                Drink drink = new Drink(drinkId, categoryId, name, price, type, description);
                drinks.add(drink);
            }
        }
        return drinks;
    }

    private List<AddOn> getAddsOnFromMap(Map<String, AttributeValue> itemMap) {
        List<AddOn> addsOn = new ArrayList<>();
        if (itemMap.containsKey("addsOn")) {
            List<AttributeValue> addOnAttributeValues = itemMap.get("addsOn").l();
            for (AttributeValue addOnAttrValue : addOnAttributeValues) {
                Map<String, AttributeValue> addOnMap = addOnAttrValue.m();
                String addOnId = addOnMap.get("addOnId").s();
                int categoryId = Integer.parseInt(addOnMap.get("categoryId").n());
                String name = addOnMap.get("name").s();
                String price = addOnMap.get("price").s();
                String type = addOnMap.get("type").s();
                String description = addOnMap.get("description").s();

                AddOn addOn = new AddOn(addOnId, categoryId, name, price, type, description);
                addsOn.add(addOn);
            }
        }
        return addsOn;
    }
}
