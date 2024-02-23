package com.restaurantaws.menuservice.repositories;

import com.restaurantaws.menuservice.model.Menu;

public interface MenuRepository {

    void saveMenu(Menu menu);

    Menu getLatestMenuFromDatabase();

    Menu getLatestMenuFromCache();


}
