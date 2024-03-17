package com.restaurantaws.menuservice.services;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);

    void remove(K key);
}
