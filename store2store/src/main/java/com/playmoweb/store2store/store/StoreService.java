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
    public Flowable<Optional<List<T>>> getAll(final Filter filter, final SortingMode sortingMode) {
        List<Flowable<Optional<List<T>>>> flowables = new ArrayList<>();
        Flowable<Optional<List<T>>> flowStorage = dao.getAll(filter, sortingMode);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(Optional<List<T>> items) throws Exception {
                            final List<T> copy = new ArrayList<>(items.get());
                            if(filter == null) {
                                // full replacement, we clean up the Store dao
                                return syncedStore.deleteAll().map(new Function<Integer, Optional<List<T>>>() {
                                    @Override
                                    public Optional<List<T>> apply(Integer integer) throws Exception {
                                        return Optional.wrap(copy);
                                    }
                                });
                            }
                            return Flowable.just(Optional.wrap(copy));
                        }
                    })
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(Optional<List<T>> items) throws Exception {
                            return syncedStore.insertOrUpdate(items.get());
                        }
                    });

            flowables.add(syncedStore.getAll(filter, sortingMode));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Optional<List<T>>> getAll(final List<T> items) {
        List<Flowable<Optional<List<T>>>> flowables = new ArrayList<>();
        Flowable<Optional<List<T>>> flowStorage = dao.getAll(items);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(Optional<List<T>> items) throws Exception {
                            return syncedStore.insertOrUpdate(items.get());
                        }
                    });

            flowables.add(syncedStore.getAll(items));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    public final Flowable<Optional<List<T>>> getAll(final Filter filter) {
        return getAll(filter, SortingMode.DEFAULT);
    }

    public final Flowable<Optional<List<T>>> getAll() {
        return getAll(Filter.NONE);
    }

    @Override
    public Flowable<Optional<T>> getOne(final Filter filter, final SortingMode sortingMode) {
        List<Flowable<Optional<T>>> flowables = new ArrayList<>();
        Flowable<Optional<T>> flowStorage = dao.getOne(filter, sortingMode);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(Optional<T> item) throws Exception {
                            return syncedStore.insertOrUpdate(item.get());
                        }
                    });

            flowables.add(syncedStore.getOne(filter, sortingMode));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Optional<T>> getOne(final T item) {
        List<Flowable<Optional<T>>> flowables = new ArrayList<>();
        Flowable<Optional<T>> flowStorage = dao.getOne(item);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(Optional<T> item) throws Exception {
                            return syncedStore.insertOrUpdate(item.get());
                        }
                    });

            flowables.add(syncedStore.getOne(item));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    public Flowable<Optional<T>> getOne(final Filter filter) {
        return getOne(filter, null);
    }

    public Flowable<Optional<T>> getOne(final SortingMode sortingMode) {
        return getOne(Filter.NONE, sortingMode);
    }

    public Flowable<Optional<T>> getOne() {
        return getOne(Filter.NONE, null);
    }

    @Override
    public Flowable<Optional<T>> getById(final int id) {
        List<Flowable<Optional<T>>> flowables = new ArrayList<>();
        Flowable<Optional<T>> flowStorage = dao.getById(id);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(final Optional<T> item) throws Exception {
                            return syncedStore.insertOrUpdate(item.get());
                        }
                    });

            flowables.add(syncedStore.getById(id));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Optional<List<T>>> insert(final List<T> items) {
        List<Flowable<Optional<List<T>>>> flowables = new ArrayList<>();
        Flowable<Optional<List<T>>> flowStorage = dao.insert(items);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(items).flatMap(new Function<Object, Flowable<Optional<List<T>>>>() {
                                @Override
                                public Flowable<Optional<List<T>>> apply(Object o) throws Exception {
                                    return Flowable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(Optional<List<T>> it) throws Exception {
                            return syncedStore.insertOrUpdate(it.get());
                        }
                    });

            flowables.add(syncedStore.insert(items));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Optional<T>> insert(final T item) {
        List<Flowable<Optional<T>>> flowables = new ArrayList<>();
        Flowable<Optional<T>> flowStorage = dao.insert(item);

        if(hasSyncedStore()) {
            flowStorage = flowStorage
                    .onErrorResumeNext(new Function<Throwable, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(final Throwable throwable) throws Exception {
                            return syncedStore.delete(item).flatMap(new Function<Integer, Flowable<Optional<T>>>() {
                                @Override
                                public Flowable<Optional<T>> apply(Integer zeroOrOne) throws Exception {
                                    return Flowable.error(throwable);
                                }
                            });
                        }
                    })
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(Optional<T> it) throws Exception {
                            return syncedStore.insertOrUpdate(it.get());
                        }
                    });

            flowables.add(syncedStore.insert(item));
        }

        flowables.add(flowStorage);
        return Flowable.concat(flowables);
    }

    @Override
    public Flowable<Optional<List<T>>> insertOrUpdate(final List<T> items) {
        Flowable<Optional<List<T>>> flowStorage;

        if(hasSyncedStore()) {
            flowStorage = syncedStore.getAll(items) // try to get a copy before trying to update/insert
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(final Optional<List<T>> originalItems) {
                            return Flowable.concat(
                                syncedStore.insertOrUpdate(items),
                                dao.insertOrUpdate(items)
                                        .onErrorResumeNext(new Function<Throwable, Flowable<Optional<List<T>>>>() {
                                            @Override
                                            public Flowable<Optional<List<T>>> apply(final Throwable throwable) {
                                                if(originalItems.isNull() || originalItems.get().size() == 0){
                                                    return syncedStore.delete(items).flatMap(new Function<Integer, Flowable<Optional<List<T>>>>() {
                                                        @Override
                                                        public Flowable<Optional<List<T>>> apply(Integer notUsed) {
                                                            return Flowable.error(throwable);
                                                        }
                                                    });
                                                } else {
                                                    return syncedStore.delete(items)
                                                        .flatMap(new Function<Integer, Flowable<Optional<List<T>>>>() {
                                                            @Override
                                                            public Flowable<Optional<List<T>>> apply(Integer deleteCount) throws Exception {
                                                                return syncedStore.insertOrUpdate(originalItems.get());
                                                            }
                                                        })
                                                        .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                                                            @Override
                                                            public Flowable<Optional<List<T>>> apply(Optional<List<T>> notUsed) {
                                                                return Flowable.error(throwable);
                                                            }
                                                        });
                                                }
                                            }
                                        })
                                        .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                                            @Override
                                            public Flowable<Optional<List<T>>> apply(Optional<List<T>> itemInsertedOrUpdated) {
                                                return syncedStore.insertOrUpdate(itemInsertedOrUpdated.get());
                                            }
                                        })
                            );
                        }
                    });
        } else {
            flowStorage = dao.insertOrUpdate(items);
        }

        return flowStorage;
    }

    @Override
    public Flowable<Optional<T>> insertOrUpdate(final T item) {
        Flowable<Optional<T>> flowStorage;

        if(hasSyncedStore()) {
            flowStorage = syncedStore.getOne(item) // get a copy before trying to update/insert
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(final Optional<T> originalItem) throws Exception {
                            return Flowable.concat(
                                    syncedStore.insertOrUpdate(item),
                                    dao.insertOrUpdate(item)
                                            .onErrorResumeNext(new Function<Throwable, Flowable<Optional<T>>>() {
                                                @Override
                                                public Flowable<Optional<T>> apply(final Throwable throwable) throws Exception {
                                                    // the item did not exist before
                                                    if(originalItem.isNull()){
                                                        return syncedStore.delete(item).flatMap(new Function<Integer, Flowable<Optional<T>>>() {
                                                            @Override
                                                            public Flowable<Optional<T>> apply(Integer notUsed) throws Exception {
                                                                return Flowable.error(throwable);
                                                            }
                                                        });
                                                    } else { // it did exists, re-apply previous version
                                                        return syncedStore.delete(item)
                                                            .flatMap(new Function<Integer, Flowable<Optional<T>>>() {
                                                                @Override
                                                                public Flowable<Optional<T>> apply(Integer integer) throws Exception {
                                                                    return syncedStore.insertOrUpdate(originalItem.get());
                                                                }
                                                            })
                                                            .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                                                                @Override
                                                                public Flowable<Optional<T>> apply(Optional<T> notUsed) throws Exception {
                                                                    return Flowable.error(throwable);
                                                                }
                                                            });
                                                    }
                                                }
                                            })
                                            .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                                                @Override
                                                public Flowable<Optional<T>> apply(Optional<T> itemInsertedOrUpdated) throws Exception {
                                                    return syncedStore.insertOrUpdate(itemInsertedOrUpdated.get());
                                                }
                                            })
                            );
                        }
                    });
        } else {
            flowStorage = dao.insertOrUpdate(item);
        }

        return flowStorage;
    }

    @Override
    public Flowable<Optional<List<T>>> update(final List<T> items) {
        Flowable<Optional<List<T>>> flowStorage;

        if(hasSyncedStore()) {
            flowStorage = syncedStore.getAll(items) // try to get a copy before trying to update/insert
                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                        @Override
                        public Flowable<Optional<List<T>>> apply(final Optional<List<T>> originalItems) {
                            if(originalItems.isNull() || originalItems.get().size() < items.size()){
                                return Flowable.error(new IllegalArgumentException(
                                        "One or many items do not exist and can't be updated. Please use insertOrUpdate if this behaviour is not expected !"
                                ));
                            }

                            return Flowable.concat(
                                    syncedStore.update(items),
                                    dao.update(items)
                                            .onErrorResumeNext(new Function<Throwable, Flowable<Optional<List<T>>>>() {
                                                @Override
                                                public Flowable<Optional<List<T>>> apply(final Throwable throwable) {
                                                    return syncedStore.delete(items)
                                                            .flatMap(new Function<Integer, Flowable<Optional<List<T>>>>() {
                                                                @Override
                                                                public Flowable<Optional<List<T>>> apply(Integer deleteCount) throws Exception {
                                                                    return syncedStore.insert(originalItems.get());
                                                                }
                                                            })
                                                            .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                                                                @Override
                                                                public Flowable<Optional<List<T>>> apply(Optional<List<T>> notUsed) {
                                                                    return Flowable.error(throwable);
                                                                }
                                                            });
                                                }
                                            })
                                            .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                                                @Override
                                                public Flowable<Optional<List<T>>> apply(Optional<List<T>> itemsUpdated) {
                                                    return syncedStore.update(itemsUpdated.get());
                                                }
                                            })
                            );
                        }
                    });
        } else {
            flowStorage = dao.insertOrUpdate(items);
        }

        return flowStorage;
    }

    @Override
    public Flowable<Optional<T>> update(final T item) {
        Flowable<Optional<T>> flowStorage;

        if(hasSyncedStore()) {
            flowStorage = syncedStore.getOne(item) // get a copy before trying to update/insert
                    .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                        @Override
                        public Flowable<Optional<T>> apply(final Optional<T> originalItem) throws Exception {
                            if(originalItem.isNull()){
                                return Flowable.error(new IllegalArgumentException(
                                        "This item does not exists and can't be updated. Please use insertOrUpdate if this behaviour is not expected !"
                                ));
                            }

                            return Flowable.concat(
                                    syncedStore.update(item),
                                    dao.update(item)
                                            .onErrorResumeNext(new Function<Throwable, Flowable<Optional<T>>>() {
                                                @Override
                                                public Flowable<Optional<T>> apply(final Throwable throwable) throws Exception {
                                                    return syncedStore.delete(item)
                                                            .flatMap(new Function<Integer, Flowable<Optional<T>>>() {
                                                                @Override
                                                                public Flowable<Optional<T>> apply(Integer integer) throws Exception {
                                                                    return syncedStore.insert(originalItem.get());
                                                                }
                                                            })
                                                            .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                                                                @Override
                                                                public Flowable<Optional<T>> apply(Optional<T> notUsed) throws Exception {
                                                                    return Flowable.error(throwable);
                                                                }
                                                            });
                                                }
                                            })
                                            .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                                                @Override
                                                public Flowable<Optional<T>> apply(Optional<T> itemUpdated) throws Exception {
                                                    return syncedStore.update(itemUpdated.get());
                                                }
                                            })
                            );
                        }
                    });
        } else {
            flowStorage = dao.update(item);
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
                                    .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                                        @Override
                                        public Flowable<Optional<List<T>>> apply(final Optional<List<T>> items) throws Exception {
                                            return syncedStore.insertOrUpdate(items.get());
                                        }
                                    })
                                    .flatMap(new Function<Optional<List<T>>, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(Optional<List<T>> reinsertedItems) throws Exception {
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
                                    .flatMap(new Function<Optional<List<T>>, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(Optional<List<T>> reinsertedItem) throws Exception {
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
                                    .flatMap(new Function<Optional<T>, Flowable<Integer>>() {
                                        @Override
                                        public Flowable<Integer> apply(Optional<T> reinsertedItem) throws Exception {
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

    /**
     * Wrap an object into an flowable optional
     */
    public <S> Flowable<Optional<S>> wrapOptional(S obj){
        return Flowable.just(Optional.wrap(obj));
    }

    /**
     * Wrap a flowable object into an flowable optional
     */
    public <S> Flowable<Optional<S>> wrapOptional(Flowable<S> obj){
        return obj.flatMap(new Function<S, Flowable<Optional<S>>>() {
            @Override
            public Flowable<Optional<S>> apply(S s) throws Exception {
                return wrapOptional(s);
            }
        });
    }
}
