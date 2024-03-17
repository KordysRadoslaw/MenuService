package com.restaurantaws.menuservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.repositories.MenuRepository;
import com.restaurantaws.menuservice.repositories.MenuRepositoryImpl;
import com.restaurantaws.menuservice.services.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class MenuLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDBService menuDatabaseService;

    private final GenerateToken generateToken;
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.EU_WEST_1).build();

    private final MenuFormatter menuFormatter;

    private final MenuRepository menuRepository;
    private static final String TABLE_NAME = "MenuTable";
    private static final Region AWS_REGION = Region.EU_WEST_1;



    public MenuLambdaHandler(DynamoDBService menuDatabaseService, GenerateToken generateToken, MenuRepository menuRepository, MenuFormatter menuFormatter) {
        this.menuDatabaseService = menuDatabaseService;
        this.generateToken = generateToken;
        this.menuRepository = menuRepository;
        this.menuFormatter = menuFormatter;
    }

    public MenuLambdaHandler() {
        Cache<String, Menu> asyncCache = new AsyncCache<>();
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(AWS_REGION)
                .build();

        this.menuFormatter = new MenuFormatter();
        AmazonDynamoDB amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion(AWS_REGION.id())
                .build();

        this.menuDatabaseService = new DynamoDBService(dynamoDbClient, TABLE_NAME, asyncCache,
                new MenuRepositoryImpl(new DynamoDB(amazonDynamoDBClient)), new MenuFormatter(),
                new S3Service(), new S3Uploader(), new GenerateToken());

        this.generateToken = new GenerateToken();
        this.menuRepository = new MenuRepositoryImpl(new DynamoDB(amazonDynamoDBClient));
        menuDatabaseService.scheduleCacheRefresh();
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


        if (requestBody == null || requestBody.isEmpty()) {
            return createErrorResponse(400, "Request body is empty or null");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        long timestamp = System.currentTimeMillis();
        Gson gson = new Gson();
        logger.log("requestBody after: " + requestBody);
        Menu menu = gson.fromJson(requestBody, Menu.class);

        String token = generateToken.generateUniqueToken();

        menu.incrementMenuVersion();

        String date = currentDateTime.format(formatter);

        try{
            Menu formattedMenu = menuFormatter.formatMenu(menu);
            logger.log("Formatted menu: " + formattedMenu);
            menuDatabaseService.saveData(formattedMenu);

        } catch (Exception e){
            logger.log("Error while setting menu fields: " + e);
            return createErrorResponse(500, "Error while setting menu fields: " + e.getMessage());
        }
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("Menu saved successfully");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Custom-Header", "Custom Value");
        response.setHeaders(headers);

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
                logger.log("Error while loadiing menu: " + e.getMessage());
                return createErrorResponse(500, "Error while loading menu: " + e.getMessage());
            }
        } else {
            return createErrorResponse(405, "Method not allowed:");
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int status, String errorMessage){
        APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
        errorResponse.setStatusCode(status);
        errorResponse.setBody(errorMessage);
        return errorResponse;
    }
}
