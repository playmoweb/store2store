package com.playmoweb.store2store.service.api;

import com.playmoweb.store2store.utils.CustomObserver;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import rx.Observable;

/**
 * Service interface
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public interface IService<T> {
    /**
     * Get all object of type
     * @param otherSubscriber
     * @param sortingMode
     * @return
     */
    Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode, final CustomObserver<List<T>> otherSubscriber);

    /**
     * Get one object by id
     * @param id
     * @param otherSubscriber
     * @return
     */
    Observable<T> getById(final String id, final CustomObserver<T> otherSubscriber);

    /**
     * Get one object for filter and sorting mode
     * @param filter
     * @param otherSubscriber
     * @return
     */
    Observable<T> getOne(final Filter filter, final SortingMode sortingMode, final CustomObserver<T> otherSubscriber);

    /**
     * Insert one object
     * @param otherSubscriber
     * @param object
     * @return
     */
    Observable<T> insert(final T object, final CustomObserver<T> otherSubscriber);

    /**
     * Insert all objects
     * @param otherSubscriber
     * @param objects
     * @return
     */
    Observable<List<T>> insert(final List<T> objects, final CustomObserver<List<T>> otherSubscriber);

    /**
     * Delete one object
     * @param object
     * @param otherSubscriber
     */
    Observable<Void> delete(final T object, final CustomObserver<Void> otherSubscriber);

    /**
     * Delete all objects
     * @param objects
     * @param otherSubscriber
     */
    Observable<Void> delete(final List<T> objects, final CustomObserver<Void> otherSubscriber);

    /**
     * Delete all objects of this type
     * @param otherSubscriber
     */
    Observable<Void> deleteAll(final CustomObserver<Void> otherSubscriber);
}
