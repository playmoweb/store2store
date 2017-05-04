package com.playmoweb.store2store.mock;

import com.playmoweb.store2store.service.AbstractService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import rx.Observable;

/**
 * Memory service for tests purposes
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
public class MemoryService extends AbstractService<TestModel> {
    // to simplify we use a second memory DAO to simulate another storage :)
    private MemoryStoreDao myCustomAPI = new MemoryStoreDao();

    /**
     * Public constructor
     *
     * @param clazz
     */
    public MemoryService(Class<TestModel> clazz) {
        super(clazz, new MemoryStoreDao());
    }

    @Override
    protected Observable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
        return myCustomAPI.getAll(filter, sortingMode);
    }

    @Override
    protected Observable<TestModel> getOne(Filter filter, SortingMode sortingMode) {
        return myCustomAPI.getOne(filter, sortingMode);
    }

    @Override
    protected Observable<TestModel> getById(int id) {
        return myCustomAPI.getById(id);
    }

    @Override
    protected Observable<TestModel> insert(TestModel object) {
        return myCustomAPI.insertOrUpdate(object);
    }

    @Override
    protected Observable<List<TestModel>> insert(List<TestModel> items) {
        return myCustomAPI.insertOrUpdate(items);
    }

    @Override
    protected Observable<TestModel> update(TestModel object) {
        return myCustomAPI.insertOrUpdate(object);
    }

    @Override
    protected Observable<List<TestModel>> update(List<TestModel> items) {
        return myCustomAPI.insertOrUpdate(items);
    }

    @Override
    protected Observable<Void> delete(List<TestModel> items) {
        return myCustomAPI.delete(items);
    }

    @Override
    protected Observable<Void> delete(TestModel object) {
        return myCustomAPI.delete(object);
    }

    @Override
    protected Observable<Void> deleteAll() {
        return myCustomAPI.deleteAll();
    }
}
