package com.playmoweb.store2store.store;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;

/**
 * Abstract StoreService
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    07/09/2017
 */
public abstract class StoreService<T> extends StoreDao<T> {

    /**
     * Class used by the Store
     */
    private final Class<T> clazz;

    /**
     * Default dao used
     */
    private final StoreDao<T> dao;

    /**
     * List of store synced with this one
     */
    private StoreService<T> syncedStore;

    /**
     * A local composite disposable to handle local subscriptions
     */
    protected final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Create a typed Store
     */
    public StoreService(Class<T> clazz, StoreDao<T> dao) {
        this.clazz = clazz;
        this.dao = dao;
    }

    /**
     * Add a Store to be synced to
     */
    public StoreService<T> syncWith(StoreService<T> otherStore){
        syncedStore = otherStore;
        return this;
    }

    /**
     * Get the dao used by this Store
     * @return
     */
    public StoreDao<T> getDao() {
        return dao;
    }

    /**
     * Clear all remaining disposables
     */
    public void clearAllDisposables(){
        compositeDisposable.clear();
    }

    /**
     * This Store is Synced with another
     * @return
     */
    public boolean hasSyncedStore(){
        return syncedStore != null;
    }

    @Override
    public final Flowable<List<T>> getAll(final Filter filter, final SortingMode sortingMode) {
        List<Flowable<List<T>>> flowables = new ArrayList<>();
        Flowable<List<T>> flowStorage = dao.getAll(filter, sortingMode);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<List<T>, Flowable<List<T>>>() {
                        @Override
                        public Flowable<List<T>> apply(List<T> items) throws Exception {
                            final List<T> copy = new ArrayList<>(items);
                            if(filter == null) {
                                // full replacement, we clean up the Store dao
                                return syncedStore.deleteAll().map(new Function<Integer, List<T>>() {
                                    @Override
                                    public List<T> apply(Integer integer) throws Exception {
                                        return copy;
                                    }
                                });
                            }
                            return Flowable.just(copy);
                        }
                    })
                    .flatMap(new Function<List<T>, Flowable<List<T>>>() {
                        @Override
                        public Flowable<List<T>> apply(List<T> items) throws Exception {
                            return syncedStore.insertOrUpdate(items);
                        }
                    });

            flowables.add(syncedStore.getAll(filter, sortingMode));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    public final Flowable<List<T>> getAll(final Filter filter) {
        return getAll(filter, SortingMode.DEFAULT);
    }

    public final Flowable<List<T>> getAll() {
        return getAll(null);
    }

    @Override
    public Flowable<T> getOne(final Filter filter, final SortingMode sortingMode) {
        List<Flowable<T>> flowables = new ArrayList<>();
        Flowable<T> flowStorage = dao.getOne(filter, sortingMode);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<T, Flowable<T>>() {
                        @Override
                        public Flowable<T> apply(T items) throws Exception {
                            return syncedStore.insertOrUpdate(items);
                        }
                    });

            flowables.add(syncedStore.getOne(filter, sortingMode));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    public Flowable<T> getOne(final Filter filter) {
        return getOne(filter, null);
    }

    public Flowable<T> getOne(final SortingMode sortingMode) {
        return getOne(null, sortingMode);
    }

    public Flowable<T> getOne() {
        return getOne(null, null);
    }

    public Flowable<T> getById(final int id) {
        List<Flowable<T>> flowables = new ArrayList<>();
        Flowable<T> flowStorage = dao.getById(id);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<T, Flowable<T>>() {
                        @Override
                        public Flowable<T> apply(final T item) throws Exception {
                            return syncedStore.insertOrUpdate(item);
                        }
                    });

            flowables.add(syncedStore.getById(id));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<List<T>> insert(final List<T> items) {
        List<Flowable<List<T>>> flowables = new ArrayList<>();
        Flowable<List<T>> flowStorage = dao.insert(items);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<List<T>>>() {
                        @Override
                        public Flowable<List<T>> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(items).flatMap(new Function<Object, Flowable<List<T>>>() {
                                @Override
                                public Flowable<List<T>> apply(Object o) throws Exception {
                                    return Flowable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<List<T>, Flowable<List<T>>>() {
                        @Override
                        public Flowable<List<T>> apply(List<T> it) throws Exception {
                            return syncedStore.insertOrUpdate(it);
                        }
                    });

            flowables.add(syncedStore.insert(items));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<T> insert(final T item) {
        List<Flowable<T>> flowables = new ArrayList<>();
        Flowable<T> flowStorage = dao.insert(item);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<T>>() {
                        @Override
                        public Flowable<T> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(item).flatMap(new Function<Object, Flowable<T>>() {
                                @Override
                                public Flowable<T> apply(Object o) throws Exception {
                                    return Flowable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<T, Flowable<T>>() {
                        @Override
                        public Flowable<T> apply(T it) throws Exception {
                            return syncedStore.insertOrUpdate(it);
                        }
                    });

            flowables.add(syncedStore.insert(item));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<List<T>> insertOrUpdate(final List<T> items) {
        return dao.insertOrUpdate(items);
    }

    @Override
    public Flowable<T> insertOrUpdate(final T item) {
        Flowable<T> flowStorage;

        if(hasSyncedStore()) {
            flowStorage = syncedStore.getOne(item) // get a copy before trying to update/insert
                    .flatMap(new Function<T, Flowable<T>>() {
                        @Override
                        public Flowable<T> apply(final T originalItem) throws Exception {
                            return syncedStore.insertOrUpdate(item)
                                .flatMap(new Function<T, Flowable<T>>() {
                                    @Override
                                    public Flowable<T> apply(T t) throws Exception {
                                        return dao.insertOrUpdate(item)
                                                .onErrorResumeNext(new Function<Throwable, Flowable<T>>() {
                                                    @Override
                                                    public Flowable<T> apply(final Throwable throwable) throws Exception {
                                                        return syncedStore.insertOrUpdate(originalItem).flatMap(new Function<T, Flowable<T>>() {
                                                                    @Override
                                                                    public Flowable<T> apply(T t) throws Exception {
                                                                        return Flowable.error(throwable);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .flatMap(new Function<T, Flowable<T>>() {
                                                    @Override
                                                    public Flowable<T> apply(T itemInsertedOrUpdated) throws Exception {
                                                        return syncedStore.insertOrUpdate(itemInsertedOrUpdated);
                                                    }
                                                });
                                    }
                                });
                        }
                    });
        } else {
            flowStorage = dao.insertOrUpdate(item);
        }

        return flowStorage;
    }

    @Override
    public Flowable<Integer> deleteAll() {
        List<Flowable<Integer>> flowables = new ArrayList<>();
        Flowable<Integer> flowStorage = dao.deleteAll();

        // TODO improve the deleteAll method in case of double fail
        // copy the syncedStore for insert if double fail
        // execute the deleteAll (this)
        // if error, try getAll()
        // if error again => re-insert syncedStore datas (better than nothing)
        // if not error, insert fresh datas

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<Integer>>() {
                        @Override
                        public Flowable<Integer> apply(final Throwable throwable) throws Exception {
                            return dao.getAll(null, null)
                                    .flatMap(new Function<List<T>, Flowable<List<T>>>() {
                                        @Override
                                        public Flowable<List<T>> apply(final List<T> items) throws Exception {
                                            return syncedStore.insertOrUpdate(items);
                                        }
                                    })
                                    .flatMap(new Function<List<T>, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(List<T> reinsertedItems) throws Exception {
                                            return Flowable.error(throwable);
                                        }
                                    });
                        }
                    });

            flowables.add(syncedStore.deleteAll());
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Integer> delete(final List<T> items) {
        List<Flowable<Integer>> flowables = new ArrayList<>();
        Flowable<Integer> flowStorage = dao.delete(items);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<Integer>>() {
                        @Override
                        public Flowable<Integer> apply(final Throwable throwable) throws Exception {
                            return syncedStore.insertOrUpdate(items)
                                    .flatMap(new Function<List<T>, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(List<T> reinsertedItem) throws Exception {
                                            return Flowable.error(throwable);
                                        }
                                    });
                        }
                    });

            flowables.add(syncedStore.delete(items));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Integer> delete(final T item) {
        List<Flowable<Integer>> flowables = new ArrayList<>();
        Flowable<Integer> flowStorage = dao.delete(item);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<Integer>>() {
                        @Override
                        public Flowable<Integer> apply(final Throwable throwable) throws Exception {
                            return syncedStore.insertOrUpdate(item)
                                    .flatMap(new Function<T, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(T reinsertedItem) throws Exception {
                                            // we could emit a new Integer(0) before the error but it breaks the concept of rollbackIfErrorWithoutOnNext
                                            return Flowable.error(throwable);
                                        }
                                    });
                        }
                    });

            flowables.add(syncedStore.delete(item));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }
}
