package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.repositories.MenuRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DynamoDBService {


    private final String tableName;
    private final DynamoDbClient ddbClient;

    private final Cache<String, Menu> menuCache;

    private final MenuRepository menuRepository;


    private MenuFormatter menuFormatter;

    private S3Service s3Service;

    private S3Uploader s3Uploader;

    private final GenerateToken generateToken;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


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

    public boolean saveData(Menu menu){
        Menu formattedMenu = menuFormatter.formatMenu(menu);
        try{
            //kurwa tu gdzies jest blad
            menuRepository.saveMenu(formattedMenu);
            s3Uploader.uploadToS3(formattedMenu);
            return true;
        } catch (Exception e){
            throw new RuntimeException("Error while saving data to DynamoDB: " + e.getMessage(), e);
        }
    }

    public Menu loadLatestMenuFromDatabase() {

        String cacheKey = "latestMenu";

        try{
            Menu cachedMenu = menuCache.get(cacheKey);
            if(cachedMenu != null){
                return cachedMenu;
            }
        } catch (Exception e){
            throw new RuntimeException("Error getting menu from cache: " + e.getMessage());
        }

        try{
            Menu menu = menuRepository.getLatestMenuFromDatabase();
            if(menu != null){
                menuCache.put(cacheKey, menu);
                return menu;
            }

        } catch (Exception e){
            throw new RuntimeException("Error getting menu from database: " + e.getMessage());
        }
        return null;
    }

    public Menu loadLatestMenuFromCache() {
        String cacheKey = "latestMenu";

        try {
            Menu cachedMenu = menuCache.get(cacheKey);
            if (cachedMenu != null) {
                return cachedMenu;
            }
            cachedMenu = menuRepository.getLatestMenuFromDatabase();


            if(cachedMenu != null){
                s3Uploader.uploadToS3(cachedMenu);
                menuCache.put(cacheKey, cachedMenu);

                return cachedMenu;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void scheduleCacheRefresh(){
        scheduler.scheduleAtFixedRate(this::refreshCache, 0, 12, TimeUnit.HOURS);
    }

    public void refreshCache(){
        String cacheKey = "latestMenu";
        long currentTimeMillis = System.currentTimeMillis();
        Menu latestMenu = menuRepository.getLatestMenuFromDatabase();
        if(latestMenu != null){
            menuCache.put(cacheKey, latestMenu);

        }
    }
}
