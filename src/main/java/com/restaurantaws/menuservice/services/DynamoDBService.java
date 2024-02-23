package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.AddOn;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.repositories.MenuRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DynamoDBService {


    private final String tableName;
    private final DynamoDbClient ddbClient;

    private final Cache<String, Menu> menuCache;

    private final MenuRepository menuRepository;


    private MenuFormatter menuFormatter;

    private S3Service s3Service;

    private S3Uploader s3Uploader;

    private GenerateToken generateToken;

    public DynamoDBService(DynamoDbClient ddbClient, String tableName,Cache<String, Menu> menuCache, MenuRepository menuRepository, MenuFormatter menuFormatter, S3Service s3Service, S3Uploader s3Uploader, GenerateToken generateToken) {
        this.tableName = tableName;
        this.ddbClient = ddbClient;
        this.menuRepository = menuRepository;
        this.menuFormatter = menuFormatter;
        this.s3Service = s3Service;
        this.s3Uploader = s3Uploader;
        this.generateToken = generateToken;
        this.menuCache = menuCache;
    }





//    public DynamoDBService(DynamoDbClient ddbClient, String tableName, MenuCache menuCache, MenuProcessor menuProcessor, S3Service s3Service) {
//        this.tableName = tableName;
//        this.ddbClient = ddbClient;
//        this.menuCache = menuCache;
//        this.menuProcessor = menuProcessor;
//        this.s3Service = s3Service;
//    }

//    public boolean saveData(Menu menu) {
//
//        Map<String, AttributeValue> itemValues = menuProcessor.menuProcessor(menu);
//
//        try {
//            ddbClient.putItem(PutItemRequest.builder().tableName(tableName).item(itemValues).build());
//            s3Service.saveToS3(menu);
//
//            return true;
//        } catch (DynamoDbException e) {
//            throw new RuntimeException("DynamoDB exception: " + e.getMessage(), e);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while saving data to DynamoDB: " + e.getMessage(), e);
//        }
//    }

    public boolean saveData(Menu menu){
        //tutaj gdzies ten formatter trzeba uzyc
        Menu formattedMenu = menuFormatter.formatMenu(menu);
        // a tu zapisywnaie
        try{
            menuRepository.saveMenu(formattedMenu);
            s3Uploader.uploadToS3(formattedMenu);
            return true;
        } catch (Exception e){
            throw new RuntimeException("Error while saving data to DynamoDB: " + e.getMessage(), e);
        }
    }

    public Menu loadLatestMenuFromDatabase() {
//        String cacheKey = "latestMenu";
//        long currentTimeMillis = System.currentTimeMillis();
//        String timestamString = Long.toString(currentTimeMillis);
//        Menu cachedMenu = menuCache.getMenu(cacheKey, currentTimeMillis);
//        if (cachedMenu != null) {
//            return cachedMenu;
//        }
//
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedDateTime = currentDateTime.format(formatter);
//
//        // the map with a filter
//        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":menuDate", AttributeValue.builder().s(formattedDateTime).build());
//
//        // scan
//        ScanRequest scanRequest = ScanRequest.builder()
//                .tableName(tableName)
//                .filterExpression("menuDate = :menuDate")
//                .expressionAttributeValues(expressionAttributeValues)
//                .build();
//
//
//        try {
//            ScanResponse response = ddbClient.scan(scanRequest);
//
//            if (!response.items().isEmpty()) {
//                Map<String, AttributeValue> firstItem = response.items().get(0);
//                double menuVersion = Double.parseDouble(firstItem.get("menuVersion").n());
//                String tokenId = generateToken.generateUniqueToken();
//
//                List<Dish> dishes = getDishesFromMap(firstItem);
//                List<Drink> drinks = getDrinksFromMap(firstItem);
//                List<AddOn> addsOn = getAddsOnFromMap(firstItem);
//
//
//                Menu menu = new Menu(menuVersion, formattedDateTime, tokenId,
//                        dishes, drinks, addsOn, currentTimeMillis);
//                menuCache.putMenu(cacheKey, menu, currentTimeMillis);
//                return menu;
//            }
//        } catch (DynamoDbException e) {
//            throw new RuntimeException(e);
//        }
//
//        return s3Service.loadFromS3();



        String cacheKey = "latestMenu";
        long currentTimeMillis = System.currentTimeMillis();
        String timestamString = Long.toString(currentTimeMillis);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);


        try{
            Menu menu = menuRepository.getLatestMenuFromDatabase();
            if(menu != null){
                //menuCache.putMenu(cacheKey, menu, currentTimeMillis);
                Menu cachedMenu = menuCache.get(cacheKey);

                return cachedMenu;
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public Menu loadLatestMenuFromCache() {
        String cacheKey = "latestMenu";
        long currentTimeMillis = System.currentTimeMillis();
        Menu cachedMenu = menuCache.get(cacheKey);
//        Menu cachedMenu = menuCache.getMenu(cacheKey, currentTimeMillis);

        try{
            if (cachedMenu != null) {
                return cachedMenu;
            }
            Menu menu = menuRepository.getLatestMenuFromDatabase();
            //to ponizej sprawdz
            s3Uploader.uploadToS3(menu);
            // i ze ma jeszcze do cache ladowac
            menuCache.put(cacheKey, menu);
            return menu;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
