package com.playmoweb.store2store.mock;

import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.store.StoreService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
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
        public Flowable<List<TestModel>> getAll(Filter filter, SortingMode sortingMode) {
            if(shouldThrowError){
                return Flowable.error(new Exception("getAll.error"));
            }

            List<TestModel> list = new ArrayList<>();
            list.add(new TestModel(10));
            list.add(new TestModel(20));
            list.add(new TestModel(30));

            if(sortingMode != null && sortingMode.sort == SortType.DESCENDING){
                Collections.reverse(list);
            }

            return Flowable.just(list).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Flowable<TestModel> getOne(Filter filter, SortingMode sortingMode) {
            if(shouldThrowError){
                return Flowable.error(new Exception("getOne.error"));
            }

            return getAll(filter, sortingMode).flatMap(new Function<List<TestModel>, Flowable<TestModel>>() {
                @Override
                public Flowable<TestModel> apply(List<TestModel> testModels) throws Exception {
                    return Flowable.just(testModels.get(0));
                }
            });
        }

        @Override
        public Flowable<List<TestModel>> insert(List<TestModel> items) {
            if(shouldThrowError){
                return Flowable.error(new Exception("insert.error"));
            }
            return Flowable.just(items).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Flowable<TestModel> insert(TestModel item) {
            if(shouldThrowError){
                return Flowable.error(new Exception("insertSingle.error"));
            }
            return Flowable.just(item).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Flowable<Integer> delete(TestModel item) {
            if(shouldThrowError){
                return Flowable.error(new Exception("deleteSingle.error"));
            }
            return Flowable.just(1).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Flowable<Integer> delete(List<TestModel> items) {
            if(shouldThrowError){
                return Flowable.error(new Exception("deleteSingle.error"));
            }
            return Flowable.just(items.size()).delay(1, TimeUnit.SECONDS);
        }

        @Override
        public Flowable<Integer> deleteAll() {
            if(shouldThrowError){
                shouldThrowError = false; // special case because the StoreService needs to call again getAll()
                return Flowable.error(new Exception("deleteAll.error"));
            }

            return getAll(null, null)
                    .delay(1, TimeUnit.SECONDS)
                    .flatMap(new Function<List<TestModel>, Flowable<Integer>>() {
                        @Override
                        public Flowable<Integer> apply(List<TestModel> ts) throws Exception {
                            return Flowable.just(ts.size());
                        }
                    });
        }
    }
}
