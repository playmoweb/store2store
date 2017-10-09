package com.playmoweb.store2store.utils;


import java.util.AbstractMap;
import java.util.LinkedList;

/**
 * Bind a key to a sorting mode
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public final class SortingMode {
    public static final SortingMode DEFAULT = new SortingMode("id", SortType.ASCENDING);
    public final LinkedList<AbstractMap.SimpleEntry<String, SortType>> entries = new LinkedList<>();

    public SortingMode(String key, SortType sortType) {
        and(key, sortType);
    }

    /**
     * Create a list of Sorting rules with the same SortType
     */
    public SortingMode(String[] keys, SortType sortType) {
        for(String k : keys){
            if(k != null){
                this.and(k, sortType);
            }
        }
    }

    /**
     * Create a list of Sorting rules mapped by position in array
     * @warn    If the arrays have a different size, an exception will be throw
     */
    public SortingMode(String[] keys, SortType[] sortTypes) {
        if(keys.length != sortTypes.length){
            throw new IllegalArgumentException("The String[] (keys) and the SortType[] arrays must have the same size");
        }

        for (int i = 0; i < keys.length; i++) {
            if(keys[i] != null && sortTypes[i] != null){
                and(keys[i], sortTypes[i]);
            }
        }
    }

    /**
     * Add a sort rule
     * @note    This rule is always added at the end and will apply after previous ones.
     */
    public SortingMode and(String key, SortType sortType){
        entries.add(new AbstractMap.SimpleEntry<>(key, sortType));
        return this;
    }

    /**
     * Build a new SortingMode object to allow static chaining
     */
    public static SortingMode with(String key, SortType sortType){
        return new SortingMode(key, sortType);
    }
}
