package com.playmoweb.store2store.service;

import com.playmoweb.store2store.dao.IStoreDao;
import com.playmoweb.store2store.utils.CustomObserver;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SimpleObserver;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * This abstract service hide basic implementation of CRUD operations combined with a storage (dao)
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 *
 * @updated hoanghiep
 * @date    28/07/2017
 */
public abstract class AbstractService<T> implements IService<T> {
    /**
     * The type manipulated by the manager
     */
    private final Class<T> clazz;

    /**
     * The storage used by this manager
     */
    private final IStoreDao<T> storage;

    /**
     * A local subscription to handle local observers
     */
    protected final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Public constructor
     *
     * @param clazz
     */
    public AbstractService(Class<T> clazz, IStoreDao<T> storage) {
        this.clazz = clazz;
        this.storage = storage;
    }

    public IStoreDao<T> getStorage() {
        return storage;
    }

    /**************************************************************************
     *   CRUD operations and helpers to simplify usage
     *************************************************************************/

    @Override
    public Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode, CustomObserver<List<T>> otherSubscriber) {
        Observable<List<T>> observable = getAll(filter, sortingMode)
                .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(final List<T> ts) throws Exception {
                        return storage.deleteAll().map(new Function<Void, List<T>>() {
                            @Override
                            public List<T> apply(Void aVoid) throws Exception {
                                return ts;
                            }
                        });
                    }
                })
                .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(List<T> ts) throws Exception {
                        storage.insertOrUpdate(ts);
                        return storage.getAll(filter, sortingMode);
                    }
                });

        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.getAll(filter, sortingMode);
    }

    public final Observable<List<T>> getAll(final CustomObserver<List<T>> otherSubscriber) {
        return getAll(null, SortingMode.DEFAULT, otherSubscriber);
    }

    @Override
    public Observable<T> getById(int id, CustomObserver<T> otherSubscriber) {
        Observable<T> observable = getById(id)
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T itemFromAsync) throws Exception {
                        return storage.insertOrUpdate(itemFromAsync);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.getOne(new Filter("id", id), null);
    }

    @Override
    public Observable<T> getOne(Filter filter, SortingMode sortingMode, CustomObserver<T> otherSubscriber) {
        Observable<T> observable = getOne(filter, sortingMode)
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T itemFromAsync) throws Exception {
                        return storage.insertOrUpdate(itemFromAsync);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.getOne(filter, sortingMode);
    }

    @Override
    public Observable<T> insert(final T object, CustomObserver<T> otherSubscriber) {
        Observable<T> observable = insert(object)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(final Throwable throwable) throws Exception {
                        return storage.delete(object)
                                .flatMap(new Function<Void, ObservableSource<T>>() {
                                    @Override
                                    public ObservableSource<T> apply(Void aVoid) throws Exception {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                })
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T item) throws Exception {
                        return storage.insertOrUpdate(item);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.insertOrUpdate(object);
    }

    @Override
    public Observable<List<T>> insert(final List<T> objects, CustomObserver<List<T>> otherSubscriber) {
        Observable<List<T>> observable = insert(objects)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(final Throwable throwable) throws Exception {
                        return storage.delete(objects).flatMap(new Function<Void, ObservableSource<List<T>>>() {
                            @Override
                            public ObservableSource<List<T>> apply(Void aVoid) throws Exception {
                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(List<T> ts) throws Exception {
                        return storage.insertOrUpdate(ts);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.insertOrUpdate(objects);
    }

    @Override
    public Observable<T> update(T object, CustomObserver<T> otherSubscriber) {
        Observable<T> observable = update(object)
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T item) throws Exception {
                        return storage.insertOrUpdate(item);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return Observable.just(object);
    }

    @Override
    public Observable<List<T>> update(List<T> objects, CustomObserver<List<T>> otherSubscriber) {
        Observable<List<T>> observable = update(objects)
                .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(List<T> ts) throws Exception {
                        return storage.insertOrUpdate(ts);
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.insertOrUpdate(objects);
    }

    @Override
    public Observable<Void> delete(final T object, CustomObserver<Void> otherSubscriber) {
        Observable<Void> observable = delete(object)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<Void>>() {
                    @Override
                    public ObservableSource<Void> apply(final Throwable throwable) throws Exception {
                        return storage.insertOrUpdate(object)
                                .flatMap(new Function<T, ObservableSource<Void>>() {
                                    @Override
                                    public ObservableSource<Void> apply(T item) throws Exception {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.delete(object);
    }

    @Override
    public Observable<Void> delete(final List<T> objects, CustomObserver<Void> otherSubscriber) {
        Observable<Void> observable = delete(objects)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<Void>>() {
                    @Override
                    public ObservableSource<Void> apply(final Throwable throwable) throws Exception {
                        return storage.insertOrUpdate(objects)
                                .flatMap(new Function<List<T>, ObservableSource<Void>>() {
                                    @Override
                                    public ObservableSource<Void> apply(List<T> ts) throws Exception {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.delete(objects);
    }

    @Override
    public Observable<Void> deleteAll(CustomObserver<Void> otherSubscriber) {
        Observable<Void> observable = deleteAll()
                .onErrorResumeNext(new Function<Throwable, ObservableSource<Void>>() {
                    @Override
                    public ObservableSource<Void> apply(final Throwable throwable) throws Exception {
                        return getAll(null, null)
                                .flatMap(new Function<List<T>, ObservableSource<Void>>() {
                                    @Override
                                    public ObservableSource<Void> apply(List<T> ts) throws Exception {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                });
        subscribeNonNullObserver(observable, otherSubscriber);
        return storage.deleteAll();
    }

    /**
     * This method execute an observable only if the subscriber exists.
     *
     * @note The defaults schedulers are io() for subscribeOn and Android.mainThread() for observeOn.
     * @param observable
     * @param observer
     * @param <S>
     */
    private <S> void subscribeNonNullObserver(final Observable<S> observable, final CustomObserver<S> observer) {
        if (observable != null && observer != null) {
            DisposableObserver obs = observable.subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeWith(new SimpleObserver<>(observer));
            compositeDisposable.add(obs);
        }
    }

    /**************************************************************************
     *   Abstracts protected methods called by operations above
     *************************************************************************/

    /**
     *
     * @param filter
     * @param sortingMode
     * @return
     */
    protected abstract Observable<List<T>> getAll(Filter filter, SortingMode sortingMode);

    /**
     *
     * @param filter
     * @return
     */
    protected abstract Observable<T> getOne(Filter filter, SortingMode sortingMode);

    /**
     *
     * @param id
     * @return
     */
    protected abstract Observable<T> getById(int id);

    /**
     *
     * @param object
     * @return
     */
    protected abstract Observable<T> insert(T item);

    /**
     *
     * @param items
     * @return
     */
    protected abstract Observable<List<T>> insert(List<T> items);

    /**
     *
     * @param object
     * @return
     */
    protected abstract Observable<T> update(T item);

    /**
     *
     * @param items
     * @return
     */
    protected abstract Observable<List<T>> update(List<T> items);

    /**
     *
     * @param items
     */
    protected abstract Observable<Void> delete(List<T> items);

    /**
     *
     * @param object
     */
    protected abstract Observable<Void> delete(T item);

    /**
     * Delete all stored instances
     */
    protected abstract Observable<Void> deleteAll();
}
