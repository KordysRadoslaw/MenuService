package com.restaurantaws.menuservice.services;

import com.restaurantaws.menuservice.model.Menu;

import java.util.HashMap;
import java.util.Map;

public class MenuCache {
    private static final Map<String, Menu> cache = new HashMap<>();
    private static final long CACHE_DURATION_MS = 12 * 60 * 60 * 1000;

    public Menu getMenu(String key, long currentTimeMillis) {
        Menu menu = cache.get(key);

        if (menu != null && currentTimeMillis - menu.getTimestamp() < CACHE_DURATION_MS) {
            return menu;
        }
        return null;
    }

    public void putMenu(String key, Menu menu, long currentTimeMillis) {
        menu.setTimestamp(currentTimeMillis);
        cache.put(key, menu);
    }
}
