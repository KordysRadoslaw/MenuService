package com.restaurantaws.menuservice;

import com.restaurantaws.menuservice.model.Category;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.repositories.MenuRepository;
import com.restaurantaws.menuservice.services.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class DynamoDBServiceTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private Cache<String, Menu> mockMenuCache;

    @Mock
    private MenuRepository mockMenuRepository;

    @Mock
    private MenuFormatter mockMenuFormatter;

    @Mock
    private S3Service mockS3Service;

    @Mock
    private S3Uploader mockS3Uploader;

    @Mock
    private GenerateToken mockGenerateToken;

    private DynamoDBService dynamoDBService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dynamoDBService = new DynamoDBService(
                mockDynamoDbClient,
                "TestTableName",
                mockMenuCache,
                mockMenuRepository,
                mockMenuFormatter,
                mockS3Service,
                mockS3Uploader,
                mockGenerateToken
        );
    }
    @Test
    public void testSaveDataWhenMenuIsCorrect() {

        // Arrange
        Menu menu = new Menu(1.2, "2023-11-21", "abc123",
                Arrays.asList(new Dish("Pasta", "Danie obiadowe", "12.5f", "Main", "Makaron z sosem pomidorowym")),
                Collections.singletonList(new Drink("Drink", "Cola", "Cola", "3.5f", "Soft")),
                Collections.emptyList(), Category.MAIN_DISH, 1669054400L);

        Menu formattedMenu = new Menu();
        Mockito.when(mockMenuFormatter.formatMenu(menu)).thenReturn(formattedMenu);
        Mockito.doNothing().when(mockS3Service).saveToS3(formattedMenu);

        // Act
        boolean result = dynamoDBService.saveData(menu);

        // Assert
        assertTrue(result);
        verify(mockMenuFormatter).formatMenu(menu);
        verify(mockMenuRepository).saveMenu(formattedMenu);
    }

    @Test
    public void testSaveDataWhenMenuIsEmpty() {

        // Arrange
        Menu menu = null;

        // Assert
        Assert.assertThrows(RuntimeException.class, () -> {
            // Act
            dynamoDBService.saveData(menu);
        });
    }

    @Test
    public void testLoadLatestMenuFromDatabaseWhenMenuIsValid() {

        // Arrange
        Menu menu = new Menu(1.2, "2023-11-21", "abc123",
                Arrays.asList(new Dish("Pasta", "Danie obiadowe", "12.5f", "Main", "Makaron z sosem pomidorowym")),
                Collections.singletonList(new Drink("Drink", "Cola", "Cola", "3.5f", "Soft")),
                Collections.emptyList(), Category.MAIN_DISH, 1669054400L);

        Mockito.when(mockMenuCache.get("latestMenu")).thenReturn(menu);
        Mockito.when(mockMenuRepository.getLatestMenuFromDatabase()).thenReturn(menu);

        // Act
        Menu result = dynamoDBService.loadLatestMenuFromDatabase();

        // Assert
        Assert.assertEquals(menu, result);
    }

    @Test
    public void testLoadLatestMenuFromDatabaseWhenMenuIsInvalid() {

        // Arrange
        Menu menu = null;

        Mockito.when(mockMenuCache.get("latestMenu")).thenReturn(menu);
        Mockito.when(mockMenuRepository.getLatestMenuFromDatabase()).thenReturn(menu);

        // Act

        Menu result = dynamoDBService.loadLatestMenuFromDatabase();

        // Assert
        Assert.assertNull(result);

    }

    @Test
    public void testLoadLatestMenuFromCacheWhenMenuIsValid() {

        // Arrange
        Menu menu = new Menu(1.2, "2023-11-21", "abc123",
                Arrays.asList(new Dish("Pasta", "Danie obiadowe", "12.5f", "Main", "Makaron z sosem pomidorowym")),
                Collections.singletonList(new Drink("Drink", "Cola", "Cola", "3.5f", "Soft")),
                Collections.emptyList(), Category.MAIN_DISH, 1669054400L);

        Mockito.when(mockMenuCache.get("latestMenu")).thenReturn(menu);
        Mockito.when(mockMenuRepository.getLatestMenuFromDatabase()).thenReturn(menu);

        // Act
        Menu result = dynamoDBService.loadLatestMenuFromCache();

        // Assert
        Assert.assertEquals(menu, result);
    }
}
