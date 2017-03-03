package com.playmoweb.store2store.utils;

import java.util.WeakHashMap;

/**
 * A filter is a map of keys with a filter type and a value
 */
public class Filter extends WeakHashMap<String, Filter.KeyValuePair> {
    // TODO optimize access with a copy in an weak list

    public Filter(String key, String value) {
        super();
        add(key, FilterType.EQUAL, value);
    }

    public Filter(String key, FilterType type, String value) {
        super();
        add(key, type, value);
    }

    public Filter(String key, int value) {
        super();
        add(key, FilterType.EQUAL, String.valueOf(value));
    }

    public Filter add(String key, FilterType type, String value) {
        this.put(key, new KeyValuePair(type, value));
        return this;
    }

    /**
     * Optimized immutable KeyValuePair class
     */
    public static class KeyValuePair {
        public final FilterType filterType;
        public final String value;

        public KeyValuePair(FilterType ft, String v){
            value = v;
            filterType = ft;
        }
    }
}
