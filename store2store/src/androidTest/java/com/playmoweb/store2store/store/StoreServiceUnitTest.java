package com.playmoweb.store2store.store;

import android.support.test.runner.AndroidJUnit4;

import com.playmoweb.store2store.mock.MemoryDao;
import com.playmoweb.store2store.mock.MemoryStore;
import com.playmoweb.store2store.mock.TestModel;
import com.playmoweb.store2store.mock.TestStore;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

import static com.playmoweb.store2store.mock.MemoryDao.models;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Test all get operations
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
@RunWith(AndroidJUnit4.class)
public class StoreServiceUnitTest {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private TestStore testStore = new TestStore();
    private MemoryStore memoryStore = new MemoryStore();

    @Before
    public void before() {
        testStore.syncWith(memoryStore);
    }

    @Test
    public void testGetOneDescending(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.getOne(new SortingMode("id", SortType.DESCENDING))
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(2, models.size());

        TestModel tm = observer.values().get(0).get();
        Assert.assertEquals(30, tm.getId());
    }

    @Test
    public void testGetOneAscending(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.getOne()
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(2, models.size());

        TestModel tm = observer.values().get(0).get();
        Assert.assertEquals(10, tm.getId());
    }

    @Test
    public void testGetOneWithError(){
        testStore.shouldThrowError(true);
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(20));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.getOne()
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("getOne.error");

        testStore.shouldThrowError(false);

        Assert.assertEquals(3, models.size());
    }

    @Test
    public void testGetOneObject(){
        models.clear();

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(20));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        TestModel toFind = new TestModel(20);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.getOne(toFind)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(3, models.size());

        TestModel tm = observer.values().get(0).get();
        Assert.assertEquals(20, tm.getId());
    }

    @Test
    public void testGetAll(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.getAll(null, null)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(models.size(), 3);

        int sum = 0;
        for(TestModel tm : models){
            sum += tm.getId();
        }

        Assert.assertEquals(60, sum);
    }

    @Test
    public void testGetAllWithError(){
        models.clear();
        testStore.shouldThrowError(true); // enable error

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.getAll(null, null)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("getAll.error");

        testStore.shouldThrowError(false); // disable error
        Assert.assertEquals(models.size(), 3);

        int sum = 0;
        for(TestModel tm : models){
            sum += tm.getId();
        }

        Assert.assertEquals(6, sum);
    }

    @Test
    public void testInsertList(){
        models.clear();

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.insert(list)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Assert.assertEquals(3, models.size());
    }

    @Test
    public void testInsertListWithError(){
        models.clear();
        testStore.shouldThrowError(true); // enable error

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.insert(list)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("insert.error");

        testStore.shouldThrowError(false); // disable error

        Assert.assertEquals(0, models.size()); // should have been cleared
    }

    @Test
    public void testInsert(){
        models.clear();
        TestModel model = new TestModel(99);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.insert(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        int sum = 0;
        for(TestModel tm : models){
            sum += tm.getId();
        }

        Assert.assertEquals(1, models.size());
        Assert.assertEquals(99, sum);
    }

    @Test
    public void testInsertWithError(){
        models.clear();
        testStore.shouldThrowError(true); // enable error

        TestModel model = new TestModel(99);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.insert(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("insertSingle.error");

        testStore.shouldThrowError(false); // disable error
        Assert.assertEquals(0, models.size());
    }

    @Test
    public void testDeleteSingle(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestModel model = new TestModel(2);
        TestSubscriber<Integer> observer = new TestSubscriber<>();
        disposables.add(testStore.delete(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        int sum = 0;
        for(TestModel tm : models){
            sum += tm.getId();
        }

        Integer numberOfDeletions = observer.values().get(0);

        Assert.assertEquals(2, models.size());
        Assert.assertEquals(1, (int) numberOfDeletions);
        Assert.assertEquals(4, sum);
    }

    @Test
    public void testDeleteSingleWithError(){
        models.clear();
        testStore.shouldThrowError(true);

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestModel model = new TestModel(2);
        TestSubscriber<Integer> observer = new TestSubscriber<>();
        disposables.add(testStore.delete(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(2, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("deleteSingle.error");

        int sum = 0;
        for(TestModel tm : models){
            sum += tm.getId();
        }

        testStore.shouldThrowError(false); // disable error

        Assert.assertEquals(3, models.size());
        Assert.assertEquals(6, sum);
    }

    @Test
    public void testDeleteAll(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Integer> observer = new TestSubscriber<>();
        disposables.add(testStore.deleteAll()
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        Integer numberOfDeletions = observer.values().get(0);

        Assert.assertEquals(0, models.size());
        Assert.assertEquals(list.size(), (int) numberOfDeletions);
    }

    @Test
    public void testDeleteAllWithError(){
        testStore.shouldThrowError(true);
        models.clear();

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(1));
        list.add(new TestModel(2));
        list.add(new TestModel(3));
        memoryStore.insertOrUpdate(list);

        TestSubscriber<Integer> observer = new TestSubscriber<>();
        disposables.add(testStore.deleteAll()
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("deleteAll.error");

        testStore.shouldThrowError(false); // disable error
        Assert.assertEquals(3, models.size());
    }

    @Test
    public void testInsertOrUpdate(){
        models.clear();
        final TestModel model = new TestModel(99);
        model.setAvailable(true);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.insertOrUpdate(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(4, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        final TestModel output = observer.values().get(0).get();

        Assert.assertNotNull(output);
        Assert.assertTrue(output.isAvailable());
        Assert.assertEquals(1, models.size());
    }

    @Test
    public void testInsertOrUpdateForInsertWithError(){
        models.clear();
        testStore.shouldThrowError(true);

        final TestModel model = new TestModel(99);
        model.setAvailable(true);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.insertOrUpdate(model)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(4, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("insertOrUpdateSingle.error");

        testStore.shouldThrowError(false);

        Assert.assertEquals(0, models.size());
    }

    @Test
    public void testInsertOrUpdateForUpdateWithError(){
        models.clear();
        final TestModel model = new TestModel(99);
        model.setAvailable(true); // should be rollback to this version
        models.add(model);

        testStore.shouldThrowError(true);

        final TestModel otherModelReferenceWithSameId = new TestModel(99);
        otherModelReferenceWithSameId.setAvailable(false);

        TestSubscriber<Optional<TestModel>> observer = new TestSubscriber<>();
        disposables.add(testStore.insertOrUpdate(otherModelReferenceWithSameId)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("insertOrUpdateSingle.error");

        testStore.shouldThrowError(false);

        final TestModel output = models.get(0);
        Assert.assertEquals(1, models.size());
        Assert.assertNotNull(output);
        Assert.assertTrue(output.isAvailable()); // rollback to first version
    }

    @Test
    public void testInsertOrUpdateMany(){
        models.clear();
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(20));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        List<TestModel> listToInsertOrUpdate = new ArrayList<>();
        final TestModel first = new TestModel(20);
        first.setAvailable(true);
        final TestModel second = new TestModel(99);
        second.setAvailable(true);

        listToInsertOrUpdate.add(first);
        listToInsertOrUpdate.add(second);

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.insertOrUpdate(listToInsertOrUpdate)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertComplete();
        observer.assertNoErrors();

        final List<TestModel> output = observer.values().get(0).get();
        Assert.assertNotNull(output);
        Assert.assertEquals(4, models.size());

        for(TestModel tm : output){
            if(tm.getId() == 99 || tm.getId() == 20){
                Assert.assertTrue(tm.isAvailable());
            } else {
                Assert.assertFalse(tm.isAvailable());
            }
        }
    }

    @Test
    public void testInsertOrUpdateManyWithError(){
        models.clear();
        testStore.shouldThrowError(true);

        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel(10));
        list.add(new TestModel(20));
        list.add(new TestModel(30));
        memoryStore.insertOrUpdate(list);

        List<TestModel> listToInsertOrUpdate = new ArrayList<>();
        final TestModel first = new TestModel(20);
        first.setAvailable(true);
        final TestModel second = new TestModel(99);
        second.setAvailable(true);

        listToInsertOrUpdate.add(first);
        listToInsertOrUpdate.add(second);

        TestSubscriber<Optional<List<TestModel>>> observer = new TestSubscriber<>();
        disposables.add(testStore.insertOrUpdate(listToInsertOrUpdate)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer));

        observer.awaitTerminalEvent(5, SECONDS);
        observer.assertError(Throwable.class);
        observer.assertErrorMessage("insertOrUpdate.error");

        testStore.shouldThrowError(false);

        for(TestModel tm : MemoryDao.models){
            Assert.assertFalse(tm.isAvailable());
        }

        Assert.assertEquals(3, MemoryDao.models.size());
    }

    @After
    public void after() {
        disposables.clear();
    }
}
