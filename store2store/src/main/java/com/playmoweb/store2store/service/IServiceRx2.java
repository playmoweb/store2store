package com.playmoweb.store2store.service;

import com.playmoweb.store2store.utils.CustomObserver;
import com.playmoweb.store2store.utils.CustomObserverRx2;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hoanghiep on 7/28/17.
 */

public interface IServiceRx2<T> {
    /**
     * Get all object of type
     * @param otherSubscriber
     * @param sortingMode
     * @return
     */
    Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode, final CustomObserverRx2<List<T>> otherSubscriber);

    /**
     * Get one object by id
     * @param id
     * @param otherSubscriber
     * @return
     */
    Observable<T> getById(final int id, final CustomObserverRx2<T> otherSubscriber);

    /**
     * Get one object for filter and sorting mode
     * @param filter
     * @param otherSubscriber
     * @return
     */
    Observable<T> getOne(final Filter filter, final SortingMode sortingMode, final CustomObserverRx2<T> otherSubscriber);

    /**
     * Insert one object
     * @param otherSubscriber
     * @param object
     * @return
     */
    Observable<T> insert(final T object, final CustomObserverRx2<T> otherSubscriber);

    /**
     * Insert all objects
     * @param otherSubscriber
     * @param objects
     * @return
     */
    Observable<List<T>> insert(final List<T> objects, final CustomObserverRx2<List<T>> otherSubscriber);

    /**
     * Update one object
     * @param otherSubscriber
     * @param object
     * @return
     */
    Observable<T> update(final T object, final CustomObserverRx2<T> otherSubscriber);

    /**
     * Update all objects
     * @param otherSubscriber
     * @param objects
     * @return
     */
    Observable<List<T>> update(final List<T> objects, final CustomObserverRx2<List<T>> otherSubscriber);

    /**
     * Delete one object
     * @param object
     * @param otherSubscriber
     */
    Observable<Void> delete(final T object, final CustomObserverRx2<Void> otherSubscriber);

    /**
     * Delete all objects
     * @param objects
     * @param otherSubscriber
     */
    Observable<Void> delete(final List<T> objects, final CustomObserverRx2<Void> otherSubscriber);

    /**
     * Delete all objects of this type
     * @param otherSubscriber
     */
    Observable<Void> deleteAll(final CustomObserverRx2<Void> otherSubscriber);
}
