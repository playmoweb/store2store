package com.playmoweb.store2store.store;

import android.support.test.runner.AndroidJUnit4;

import com.playmoweb.store2store.mock.MemoryDao;
import com.playmoweb.store2store.mock.MemoryStore;
import com.playmoweb.store2store.mock.TestModel;
import com.playmoweb.store2store.mock.TestStore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Test all get operations
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
@RunWith(AndroidJUnit4.class)
public class AbstractServiceGetUnitTest {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private TestStore testStore = new TestStore();
    private MemoryStore memoryStore = new MemoryStore();

    @Before
    public void before() {
        testStore.syncWith(memoryStore);
    }

    @Test
    public void testGetAll(){
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestObserver<List<TestModel>> observer = new TestObserver<>();
        testStore.getAll(null, null)
                .subscribeOn(Schedulers.computation())
                .subscribeWith(observer);

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(MemoryDao.models.size(), 3);

        int sum = 0;
        for(TestModel tm : MemoryDao.models){
            sum += tm.getId();
        }

        Assert.assertEquals(60, sum);
    }

    @After
    public void after() {
        disposables.clear();
    }

    @Test
    public void getOne() throws Exception {

    }
}
