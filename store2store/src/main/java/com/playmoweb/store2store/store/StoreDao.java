package com.playmoweb.store2store.store;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    07/09/2017
 */
public abstract class StoreDao<T> {
    /**
     * Get all
     * @param filter
     * @param sortingMode
     * @return
     */
    public Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Get One
     * @param filter
     * @return
     */
    public Observable<T> getOne(final Filter filter, final SortingMode sortingMode) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Get By Id
     * @param id
     * @return
     */
    public Observable<T> getById(final int id) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Insert or Update
     * @param object
     * @return
     */
    public Observable<T> insertOrUpdate(final T object) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Insert Or Update
     * @param items
     * @return
     */
    public Observable<List<T>> insertOrUpdate(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Delete a List
     * @param items
     */
    public Completable delete(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Delete One
     * @param object
     */
    public Completable delete(final T object) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * Delete all stored instances
     */
    public Completable deleteAll() {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }
}
