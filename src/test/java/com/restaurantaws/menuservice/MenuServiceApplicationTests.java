package com.restaurantaws.menuservice;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class MenuServiceApplicationTests {

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    private MenuLambdaHandler menuLambdaHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(context.getLogger()).thenReturn(logger);
        menuLambdaHandler = new MenuLambdaHandler();
    }

//    @Test
//    void handleRequest_ValidRequest_ReturnsValidResponse() {
//        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
//        requestEvent.setPath("/restaurant/menu/loadMenu");
//
//        APIGatewayProxyResponseEvent responseEvent = menuLambdaHandler.handleRequest(requestEvent, context);
//
//        assertEquals(200, responseEvent.getStatusCode());
//        // Dodaj więcej asercji w zależności od oczekiwań
//    }

}
