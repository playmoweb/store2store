package com.playmoweb.store2store.mock;

import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.store.StoreService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Test Store
 */
public class TestStore extends StoreService<TestModel> {

    public TestStore() {
        super(TestModel.class, new TestStoreDao());
    }

    /**
     * Local DAO
     */
    private static class TestStoreDao extends StoreDao<TestModel> {
        @Override
        public Observable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
            List<TestModel> list = new ArrayList<>();
            list.add(new TestModel(10));
            list.add(new TestModel(20));
            list.add(new TestModel(30));

            return Observable.just(list).delay(1, TimeUnit.SECONDS);
        }
    }
}
