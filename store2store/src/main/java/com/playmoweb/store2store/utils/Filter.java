package com.playmoweb.store2store.utils;

import java.util.List;
import java.util.WeakHashMap;

/**
 * A filter is a map of keys with a filter type and a value
 */
public class Filter extends WeakHashMap<String, Filter.KeyValuePair> {
    // TODO optimize access with a copy in an weak list

    public Filter() {
        super();
    }

    public <T> Filter(String key, T value) {
        this(key, FilterType.EQUAL, value);
    }

    public <T> Filter(String key, FilterType type, T value) {
        super();
        add(key, type, value);
    }

    public <T> Filter add(String key, T value) {
        this.put(key, new KeyValuePair<>(FilterType.EQUAL, value));
        return this;
    }

    public <T> Filter add(String key, FilterType type, T value) {
        this.put(key, new KeyValuePair<>(type, value));
        return this;
    }

    public <T> Filter add(String key, FilterType type, List<T> values) {
        for(T value : values) {
            this.put(key, new KeyValuePair<>(type, value));
        }
        return this;
    }

    public <T> Filter add(String key, FilterType type, T[] values) {
        for(T value : values) {
            this.put(key, new KeyValuePair<>(type, value));
        }
        return this;
    }

    /**
     * Immutable KeyValuePair class
     */
    static class KeyValuePair<T> {
        final FilterType filterType;
        final T value;

        KeyValuePair(FilterType ft, T v){
            value = v;
            filterType = ft;
        }
    }
}
