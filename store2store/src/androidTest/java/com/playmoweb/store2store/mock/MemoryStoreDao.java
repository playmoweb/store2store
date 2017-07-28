package com.playmoweb.store2store.mock;

import com.playmoweb.store2store.dao.IStoreDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.NullObject;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Memory dao for tests purposes
 *
 * @warning This class is not tested !
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
public class MemoryStoreDao implements IStoreDao<TestModel> {
    private List<TestModel> models = new ArrayList<>();

    @Override
    public Observable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
        return Observable.just(models);
    }

    @Override
    public Observable<TestModel> getOne(Filter filter, SortingMode sortingMode) {
        return Observable.just(models.get(0));
    }

    @Override
    public Observable<TestModel> getById(int id) {
        TestModel t = null;
        for(TestModel tm : models) {
            if(tm.getId() == id) {
                t = tm;
                break;
            }
        }
        return Observable.just(t);
    }

    @Override
    public Observable<TestModel> insertOrUpdate(TestModel object) {
        return Observable.just(insertObjectOrUpdate(object));
    }

    @Override
    public Observable<List<TestModel>> insertOrUpdate(List<TestModel> items) {
        List<TestModel> output = new ArrayList<>(items.size());

        for(int i = 0; i < items.size(); i++) {
            output.set(i, insertObjectOrUpdate(items.get(i)));
        }
        return Observable.just(output);
    }

    @Override
    public Observable<NullObject> delete(List<TestModel> items) {
        for(TestModel tm : models) {
            removeItemIfExists(tm);
        }
        return Observable.just(new NullObject());
    }

    @Override
    public Observable<NullObject> delete(TestModel object) {
        removeItemIfExists(object);
        return Observable.just(new NullObject());
    }

    @Override
    public Observable<NullObject> deleteAll() {
        models.clear();
        return Observable.just(new NullObject());
    }


    // PRIVATES METHODS

    private List<TestModel> removeItemIfExists(TestModel object) {
        List<TestModel> output = new ArrayList<>();
        for(TestModel tm : models) {
            if(tm.getId() != object.getId()) {
                output.add(tm);
            }
        }
        return output;
    }

    private TestModel insertObjectOrUpdate(TestModel object) {
        TestModel t = null;
        int i = 0;
        for(TestModel tm : models) {
            if(tm.getId() == object.getId()) {
                t = tm;
                models.set(i, object);
                break;
            }
            i++;
        }

        if(t == null) {
            models.add(object);
        }
        return object;
    }
}
