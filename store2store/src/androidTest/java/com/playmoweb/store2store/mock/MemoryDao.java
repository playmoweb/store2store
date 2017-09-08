package com.playmoweb.store2store.mock;

import android.util.Log;

import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
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
public class MemoryDao extends StoreDao<TestModel> {
    public final static List<TestModel> models = new ArrayList<>(); // shared datastorage

    @Override
    public Observable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
        List<TestModel> copy = new ArrayList<>(models);
        return Observable.just(copy);
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
    public Observable<List<TestModel>> insertOrUpdate(final List<TestModel> items) {
        Log.e("INSERT", "SIZE = "+items.size());
        for(int i = 0; i < items.size(); i++) {
            Log.e("INSERT", ""+items.get(i).getId());
            insertObjectOrUpdate(items.get(i));
        }
        return Observable.just(items);
    }

    @Override
    public Completable delete(List<TestModel> items) {
        for(TestModel tm : models) {
            removeItemIfExists(tm);
        }
        return Completable.complete();
    }

    @Override
    public Completable delete(TestModel object) {
        removeItemIfExists(object);
        return Completable.complete();
    }

    @Override
    public Completable deleteAll() {
        models.clear();
        return Completable.complete();
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
