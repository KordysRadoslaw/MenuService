package com.restaurantaws.menuservice;

import com.restaurantaws.menuservice.services.AsyncCache;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class AsyncCacheTest {

    @Test
    public void testPutAndGet() throws ExecutionException, InterruptedException, TimeoutException {
        // Arrange
        AsyncCache<String, String> cache = new AsyncCache<>();
        String key = "key";
        String value = "value";

        // Act
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> cache.put(key, value));

        // Wait for the async operation to complete with a timeout
        future.get(1, TimeUnit.SECONDS);

        // Give some time for the cache to update asynchronously
        Thread.sleep(100);

        // Assert
        String cachedValue = cache.get(key);
        assertEquals(value, cachedValue);
    }
}
