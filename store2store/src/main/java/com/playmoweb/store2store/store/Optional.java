package com.playmoweb.store2store.store;

/**
 * Class to wrap object with null values in RxJava2
 * @note    The reactivex specs do not allow null values
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    07/09/2017
 */
public class Optional<T> {

    public static <T> Optional<T> wrap(T obj){
        return new Optional<>(obj);
    }

    private final T optional;

    public Optional(T optional) {
        this.optional = optional;
    }

    public boolean isNull() {
        return this.optional == null;
    }

    public T get() {
        return optional;
    }
}
