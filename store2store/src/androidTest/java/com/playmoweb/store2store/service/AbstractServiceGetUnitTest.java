package com.playmoweb.store2store.service;

import android.support.test.runner.AndroidJUnit4;

import com.playmoweb.store2store.mock.MemoryService;
import com.playmoweb.store2store.mock.TestModel;
import com.playmoweb.store2store.utils.CustomObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Test all get operations
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
@RunWith(AndroidJUnit4.class)
public class AbstractServiceGetUnitTest {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private MemoryService service = new MemoryService(TestModel.class);

    @Before
    public void before() {

    }

    @Test
    public void testMe(){
        service.getAll(test)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new CustomObserver<List<TestModel>>() {
                    @Override
                    public CompositeDisposable getCompositeDisposable() {
                        return null;
                    }

                    @Override
                    public void onError(Throwable e, boolean isUpdated) {

                    }

                    @Override
                    public void onNext(List<TestModel> testModels, boolean isUpdated) {

                    }
                });
    }

    CustomObserver<List<TestModel>> test = new CustomObserver<List<TestModel>>(disposables) {
        @Override
        public void onError(Throwable e, boolean isUpdated) {

        }

        @Override
        public void onNext(List<TestModel> testModels, boolean isUpdated) {

        }
    };

    @After
    public void after() {
        disposables.clear();
    }

    @Test
    public void getOne() throws Exception {

    }
}
