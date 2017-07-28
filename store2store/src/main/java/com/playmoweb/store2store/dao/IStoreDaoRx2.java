package com.playmoweb.store2store.dao;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 7/28/17.
 */

public interface IStoreDaoRx2<T> {
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
    Observable<Void> delete(List<T> items);

    /**
     *
     * @param object
     */
    Observable<Void> delete(T object);

    /**
     * Delete all stored instances
     */
    Observable<Void> deleteAll();
}
