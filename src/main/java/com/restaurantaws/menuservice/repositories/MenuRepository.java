package com.restaurantaws.menuservice.repositories;

import com.restaurantaws.menuservice.model.Menu;


/**
 * MenuRepository interface for saving and retrieving menu data.
 */
public interface MenuRepository {

    /**
     * Save menu to database and cache.
     * @param menu
     */
    void saveMenu(Menu menu);

    /**
     * Get latest menu from database.
     * @return Menu object containing the latest menu data.
     */
    Menu getLatestMenuFromDatabase();


    /**
     * Get latest menu from cache.
     * @return Menu object containing the latest menu data.
     */
    Menu getLatestMenuFromCache();


}
