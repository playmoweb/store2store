package com.playmoweb.store2store.mock;

import android.util.Log;

import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.store.StoreService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Test Store
 */
public class TestStore extends StoreService<TestModel> {
    private static boolean shouldThrowError = false;

    public TestStore() {
        super(TestModel.class, new TestStoreDao());
    }

    public void shouldThrowError(boolean value){
        shouldThrowError = value;
    }

    /**
     * Local DAO
     */
    private static class TestStoreDao extends StoreDao<TestModel> {
        @Override
        public Observable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
            if(shouldThrowError){
                return Observable.error(new Exception("getAll.error"));
            }

            List<TestModel> list = new ArrayList<>();
            list.add(new TestModel(10));
            list.add(new TestModel(20));
            list.add(new TestModel(30));

            return Observable.just(list).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Observable<List<TestModel>> insert(List<TestModel> items) {
            if(shouldThrowError){
                return Observable.error(new Exception("insert.error"));
            }
            return Observable.just(items).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Observable<TestModel> insert(TestModel item) {
            if(shouldThrowError){
                return Observable.error(new Exception("insertSingle.error"));
            }
            return Observable.just(item).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Observable<Integer> delete(TestModel item) {
            if(shouldThrowError){
                return Observable.error(new Exception("deleteSingle.error"));
            }
            return Observable.just(1).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Observable<Integer> delete(List<TestModel> items) {
            if(shouldThrowError){
                return Observable.error(new Exception("deleteSingle.error"));
            }
            return Observable.just(items.size()).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Observable<Integer> deleteAll() {
            if(shouldThrowError){
                shouldThrowError = false; // special case because the StoreService needs to call again getAll()
                return Observable.error(new Exception("deleteAll.error"));
            }

            return getAll(null, null)
                    .delay(1, TimeUnit.SECONDS)
                    .flatMap(new Function<List<TestModel>, ObservableSource<Integer>>() {
                        @Override
                        public ObservableSource<Integer> apply(List<TestModel> testModels) throws Exception {
                            return Observable.just(testModels.size());
                        }
                    });
        }
    }
}
