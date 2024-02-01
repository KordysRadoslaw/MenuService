package com.restaurantaws.menuservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.AddOn;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.services.MenuCache;
import com.restaurantaws.menuservice.services.MenuDatabaseService;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restaurantaws.menuservice.services.MenuProcessor;
import com.restaurantaws.menuservice.services.S3Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class MenuLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final MenuDatabaseService menuDatabaseService;
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.EU_WEST_1).build();
    private static final String TABLE_NAME = "MenuTable";


    public MenuLambdaHandler() {
        this.menuDatabaseService = new MenuDatabaseService(dynamoDbClient, TABLE_NAME, new MenuCache(), new MenuProcessor(), new S3Service());
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        String path = apiGatewayProxyRequestEvent.getPath();

        if ("/restaurant/menu/loadMenu".equals(path)) {
            return loadMenu(apiGatewayProxyRequestEvent, context);
        }


        logger.log("received: " + apiGatewayProxyRequestEvent);
        String requestBody = apiGatewayProxyRequestEvent.getBody();

        Gson gson = new Gson();
        if (requestBody == null || requestBody.isEmpty()) {
            return createErrorResponse(400, "Request body is empty or null");
        }
        Menu menu = gson.fromJson(requestBody, Menu.class);

        if (menu == null) {
            return createErrorResponse(400, "Failed to parse Menu object from JSON");
        }

        logger.log("Request body: " + requestBody);
        Menu newMenu = new Menu();

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = currentDateTime.format(formatter);

        long currentTimeMillis = System.currentTimeMillis();
        String token = generateUniqueToken();

        List<Dish> dishes = menu.getDishes() != null ? menu.getDishes() : List.of();

        for (Dish dish : dishes) {
            System.out.println("DishId before adding to Menu: " + dish.getDishId());
        }

        List<Drink> drinks = menu.getDrinks() != null ? menu.getDrinks() : List.of();
        List<AddOn> addsOn = menu.getAddsOn() != null ? menu.getAddsOn() : List.of();

        Menu existingMenu = menuDatabaseService.loadLatestMenuFromDatabase();

        if(existingMenu != null){

            existingMenu.setDate(date);
            existingMenu.setToken(token);
            existingMenu.setDishes(dishes);
            existingMenu.setDrinks(drinks);
            existingMenu.setAddsOn(addsOn);
            existingMenu.setTimestamp(currentTimeMillis);

            existingMenu.incrementMenuVersion();
            try{
                logger.log("Existing Menu before save: " + existingMenu.toString());

                menuDatabaseService.saveData(existingMenu);
                logger.log("ADDED!");
            }
            catch (DynamoDbException e){
                logger.log("Error while saving data: " + e);
                APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
                errorResponse.setStatusCode(500);  // Internal Server Error
                errorResponse.setBody("Error while saving data: " + e.getMessage());

                return errorResponse;
            }
        }else{

            newMenu.setDate(date);
            newMenu.setToken(token);
            newMenu.setDishes(dishes);
            newMenu.setDrinks(drinks);
            newMenu.setAddsOn(addsOn);
            newMenu.setTimestamp(currentTimeMillis);

            try {
                logger.log("new Menu before save: " + newMenu);
                menuDatabaseService.saveData(newMenu);
                logger.log("ADDED!");
            } catch (DynamoDbException e) {
                logger.log("Error while saving data: " + e);
                APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
                return createErrorResponse(500, "Error while saving data: " + e.getMessage());
            }
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);

        response.setBody(gson.toJson(newMenu));
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        response.setHeaders(responseHeaders);

        return response;
    }

    public APIGatewayProxyResponseEvent loadMenu(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received: " + apiGatewayProxyRequestEvent);

        if ("GET".equals(apiGatewayProxyRequestEvent.getHttpMethod())) {
            try {
                Menu loadedMenu = menuDatabaseService.loadLatestMenuFromCache();
                logger.log("Loaded menu from cache: " + loadedMenu);
                if (loadedMenu == null) {
                    loadedMenu = menuDatabaseService.loadLatestMenuFromDatabase();
                    logger.log("Loaded menu from database: " + loadedMenu);

                    if (loadedMenu == null) {
                        return createErrorResponse(404, "Menu not found");
                    }
                }

                APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
                response.setStatusCode(200);
                response.setBody(new Gson().toJson(loadedMenu));
                Map<String, String> responseHeaders = new HashMap<>();
                responseHeaders.put("Content-Type", "application/json");
                response.setHeaders(responseHeaders);

                return response;
            } catch (Exception e) {
                logger.log("Error while loading menu: " + e);
                return createErrorResponse(500, "Error while loading menu: " + e.getMessage());
            }
        } else {
            return createErrorResponse(405, "Method not allowed:");
        }
    }

    private String generateUniqueToken() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating unique token: " + e);
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int status, String errorMessage){
        APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
        errorResponse.setStatusCode(status);
        errorResponse.setBody(errorMessage);
        return errorResponse;
    }
}
