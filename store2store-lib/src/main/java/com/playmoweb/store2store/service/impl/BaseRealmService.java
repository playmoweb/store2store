package com.playmoweb.store2store.service.impl;

import com.playmoweb.store2store.service.api.AbstractService;
import com.playmoweb.store2store.dao.impl.RealmDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

/**
 * This class try to facilitate usage of Realm with any other async storage system.
 * For now this abstract class implements the most basics CRUD operations only.
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    31/01/2017
 */
public abstract class BaseRealmService<T extends RealmObject> extends AbstractService<T> {

    /**
     * Public constructor
     * @param clazz
     */
    public BaseRealmService(Class<T> clazz) {
        super(clazz, new RealmDao<T>(clazz));
    }

    @Override
    protected Observable<Void> deleteAll() {
        return null;
    }

    @Override
    protected Observable<List<T>> getAll(Filter filter, SortingMode sortingMode) {
        return null;
    }

    @Override
    protected Observable<T> getOne(Filter filter, SortingMode sortingMode) {
        return null;
    }

    @Override
    protected Observable<T> getById(String id) {
        return null;
    }

    @Override
    protected Observable<T> insert(T object) {
        return null;
    }

    @Override
    protected Observable<List<T>> insert(List<T> items) {
        return null;
    }

    @Override
    protected Observable<Void> delete(List<T> items) {
        return null;
    }

    @Override
    protected Observable<Void> delete(T object) {
        return null;
    }
}
