package com.playmoweb.store2store.utils;


/**
 * Bind a key to a sorting mode
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public final class SortingMode {
    public static final SortingMode DEFAULT = new SortingMode("id", SortType.ASCENDING);
    public final String key;
    public final SortType sort;

    public SortingMode(String k, SortType s) {
        key = k;
        sort = s;
    }
}
