package com.playmoweb.store2store.store;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
    public final Observable<List<T>> getAll(final Filter filter, final SortingMode sortingMode) {
        List<Observable<List<T>>> observables = new ArrayList<>();
        Observable<List<T>> obsStorage = dao.getAll(filter, sortingMode);

        if(hasSyncedStore()) {
            // sync response with syncedStore
            obsStorage = obsStorage
                    .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                        @Override
                        public ObservableSource<List<T>> apply(final List<T> items) throws Exception {
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
                            return Observable.just(copy);
                        }
                    })
                    .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                        @Override
                        public ObservableSource<List<T>> apply(List<T> items) throws Exception {
                            return syncedStore.insertOrUpdate(items)
                                    .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                                        @Override
                                        public ObservableSource<List<T>> apply(List<T> ts) throws Exception {
                                            return syncedStore.getAll(filter, sortingMode);
                                        }
                                    });
                        }
                    });

            observables.add(syncedStore.getAll(filter, sortingMode)); // add synced store call, first in list
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

    public final Observable<List<T>> getAll(final Filter filter) {
        return getAll(filter, SortingMode.DEFAULT);
    }

    public final Observable<List<T>> getAll() {
        return getAll(null);
    }

    @Override
    public Observable<List<T>> insert(final List<T> items) {
        List<Observable<List<T>>> observables = new ArrayList<>();
        Observable<List<T>> obsStorage = dao.insert(items);

        if(hasSyncedStore()) {
            obsStorage = obsStorage
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<List<T>>>() {
                        @Override
                        public ObservableSource<List<T>> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(items).flatMap(new Function<Object, ObservableSource<List<T>>>() {
                                @Override
                                public ObservableSource<List<T>> apply(Object o) throws Exception {
                                    return Observable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                        @Override
                        public ObservableSource<List<T>> apply(List<T> it) throws Exception {
                            return syncedStore.insertOrUpdate(it);
                        }
                    });

            observables.add(syncedStore.insert(items));
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

    @Override
    public Observable<T> insert(final T item) {
        List<Observable<T>> observables = new ArrayList<>();
        Observable<T> obsStorage = dao.insert(item);

        if(hasSyncedStore()) {
            obsStorage = obsStorage
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<T>>() {
                        @Override
                        public ObservableSource<T> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(item).flatMap(new Function<Object, ObservableSource<T>>() {
                                @Override
                                public ObservableSource<T> apply(Object o) throws Exception {
                                    return Observable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<T, ObservableSource<T>>() {
                        @Override
                        public ObservableSource<T> apply(T it) throws Exception {
                            return syncedStore.insertOrUpdate(it);
                        }
                    });

            observables.add(syncedStore.insert(item));
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

    @Override
    public Observable<List<T>> insertOrUpdate(List<T> items) {
        return dao.insertOrUpdate(items);
    }

    @Override
    public Observable<T> insertOrUpdate(T item) {
        return dao.insertOrUpdate(item);
    }

    @Override
    public Observable<Integer> deleteAll() {
        List<Observable<Integer>> observables = new ArrayList<>();
        Observable<Integer> obsStorage = dao.deleteAll();

        // TODO improve the deleteAll method in case of double fail
        // copy the syncedStore for insert if double fail
        // execute the deleteAll (this)
        // if error, try getAll()
        // if error again => re-insert syncedStore datas (better than nothing)
        // if not error, insert fresh datas
        //

        if(hasSyncedStore()) {
            obsStorage = obsStorage
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<Integer>>() {
                        @Override
                        public ObservableSource<Integer> apply(final Throwable throwable) throws Exception {
                            return getAll()
                                    .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                                        @Override
                                        public ObservableSource<List<T>> apply(final List<T> items) throws Exception {
                                            return syncedStore.insertOrUpdate(items);
                                        }
                                    })
                                    .flatMap(new Function<List<T>, ObservableSource<Integer>>() {
                                        @Override
                                        public ObservableSource<Integer> apply(List<T> reinsertedItems) throws Exception {
                                            return Observable.error(throwable);
                                        }
                                    });
                        }
                    });

            observables.add(syncedStore.deleteAll());
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

    @Override
    public Observable<Integer> delete(final List<T> items) {
        List<Observable<Integer>> observables = new ArrayList<>();
        Observable<Integer> obsStorage = dao.delete(items);

        if(hasSyncedStore()) {
            obsStorage = obsStorage
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<Integer>>() {
                        @Override
                        public ObservableSource<Integer> apply(final Throwable throwable) throws Exception {
                            return syncedStore.insertOrUpdate(items)
                                    .flatMap(new Function<List<T>, ObservableSource<Integer>>() {
                                        @Override
                                        public ObservableSource<Integer> apply(List<T> reinsertedItem) throws Exception {
                                            return Observable.error(throwable);
                                        }
                                    });
                        }
                    });

            observables.add(syncedStore.delete(items));
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

    @Override
    public Observable<Integer> delete(final T item) {
        List<Observable<Integer>> observables = new ArrayList<>();
        Observable<Integer> obsStorage = dao.delete(item);

        if(hasSyncedStore()) {
            obsStorage = obsStorage
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<Integer>>() {
                        @Override
                        public ObservableSource<Integer> apply(final Throwable throwable) throws Exception {
                            return syncedStore.insertOrUpdate(item)
                                    .flatMap(new Function<T, ObservableSource<Integer>>() {
                                        @Override
                                        public ObservableSource<Integer> apply(T reinsertedItem) throws Exception {
                                            // we could emit a new Integer(0) before the error but it breaks the concept of rollbackIfErrorWithoutOnNext
                                            return Observable.error(throwable);
                                        }
                                    });
                        }
                    });

            observables.add(syncedStore.delete(item));
        }

        observables.add(obsStorage);
        return Observable.concat(observables);
    }

/*
    /**
     * Wrap an emitter into another Observer
     * @param emitter
     * @param <S>
     * @return
     */
/*
    private <S> DisposableObserver<S> wrapEmitterInNewObserver(final ObservableEmitter<S> emitter, final boolean shouldCompleteEmitter){
        return new DisposableObserver<S>() {
            @Override
            public void onNext(S value) {
                if(!emitter.isDisposed()) {
                    emitter.onNext(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                if(!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if(!emitter.isDisposed() && shouldCompleteEmitter) {
                    emitter.onComplete();
                }
            }
        };
    }
*/
}
