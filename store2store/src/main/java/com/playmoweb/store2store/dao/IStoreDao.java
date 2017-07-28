package com.playmoweb.store2store.dao;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.NullObject;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * DAO interface
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public interface IStoreDao<T> {

    /**
     *
     * @param filter
     * @param sortingMode
     * @return
     */
    Observable<List<T>> getAll(Filter filter, SortingMode sortingMode);

    /**
     *
     * @param filter
     * @return
     */
    Observable<T> getOne(Filter filter, SortingMode sortingMode);

    /**
     *
     * @param id
     * @return
     */
    Observable<T> getById(int id);

    /**
     *
     * @param object
     * @return
     */
    Observable<T> insertOrUpdate(T object);

    /**
     *
     * @param items
     * @return
     */
    Observable<List<T>> insertOrUpdate(List<T> items);

    /**
     *
     * @param items
     */
    Observable<NullObject> delete(List<T> items);

    /**
     *
     * @param object
     */
    Observable<NullObject> delete(T object);

    /**
     * Delete all stored instances
     */
    Observable<NullObject> deleteAll();
}
