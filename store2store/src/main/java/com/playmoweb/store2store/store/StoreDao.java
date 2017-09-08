package com.playmoweb.store2store.store;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    07/09/2017
 */
public abstract class StoreDao<T> {

    public Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<T> getOne(final Filter filter, final SortingMode sortingMode) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<T> getById(final int id) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<T> insert(final T item) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<List<T>> insert(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<T> update(final T item) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<List<T>> update(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<T> insertOrUpdate(final T item) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    public Observable<List<T>> insertOrUpdate(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * @return  int  Number of items deleted
     */
    public Observable<Integer> delete(final List<T> items) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * @return  int  Number of items deleted
     */
    public Observable<Integer> delete(final T item) {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }

    /**
     * @return  int  Number of items deleted
     */
    public Observable<Integer> deleteAll() {
        throw new UnsupportedOperationException("This method has not been implemented in the child class");
    }
}
