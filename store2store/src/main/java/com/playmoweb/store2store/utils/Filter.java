package com.playmoweb.store2store.utils;

import java.util.WeakHashMap;

/**
 * A filter is a map of keys with a filter type and a value
 */
public class Filter extends WeakHashMap<String, Filter.KeyValuePair> {
    // TODO optimize access with a copy in an weak list

    public <T> Filter(String key, T value) {
        super();
        add(key, FilterType.EQUAL, value);
    }

    public <T> Filter(String key, FilterType type, T value) {
        super();
        add(key, type, value);
    }

    public <T> Filter add(String key, FilterType type, T value) {
        this.put(key, new KeyValuePair<>(type, value));
        return this;
    }

    /**
     * Immutable KeyValuePair class
     */
    public static class KeyValuePair<T> {
        public final FilterType filterType;
        public final T value;

        public KeyValuePair(FilterType ft, T v){
            value = v;
            filterType = ft;
        }
    }
}
