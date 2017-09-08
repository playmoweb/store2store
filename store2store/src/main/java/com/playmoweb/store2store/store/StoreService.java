package com.playmoweb.store2store.store;

import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

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
     * Default storage used
     */
    private final StoreDao<T> storage;

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
    public StoreService(Class<T> clazz, StoreDao<T> storage) {
        this.clazz = clazz;
        this.storage = storage;
    }

    /**
     * Add a Store to be synced to
     */
    public StoreService<T> syncWith(StoreService<T> otherStore){
        syncedStore = otherStore;
        return this;
    }

    /**
     * Get the storage used by this Store
     * @return
     */
    public StoreDao<T> getStorage() {
        return storage;
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
        Observable<List<T>> obsStorage = storage.getAll(filter, sortingMode);

        if(hasSyncedStore()) {
            // sync response with syncedStore
            obsStorage = obsStorage
                .flatMap(new Function<List<T>, ObservableSource<List<T>>>() {
                    @Override
                    public ObservableSource<List<T>> apply(final List<T> items) throws Exception {
                        List<T> copy = new ArrayList<>(items);
                        if(filter == null) {
                            return syncedStore.deleteAll().andThen(Observable.just(copy)); // full replacement, we clean up the Store storage
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

    @Override
    public Observable<List<T>> insertOrUpdate(List<T> items) {
        return storage.insertOrUpdate(items);
    }

    @Override
    public Completable deleteAll() {
        return storage.deleteAll();
    }

    /**
     * Wrap an emitter into another Observer
     * @param emitter
     * @param <S>
     * @return
     */
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
}
