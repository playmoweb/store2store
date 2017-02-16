package com.playmoweb.store2store.utils;

/**
 * Sort availables
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public enum SortType {
    ASCENDING(true),
    DESCENDING(false);

    private final boolean value;

    SortType(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}