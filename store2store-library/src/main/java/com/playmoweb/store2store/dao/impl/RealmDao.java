package com.playmoweb.store2store.dao.impl;

import com.playmoweb.store2store.dao.api.IDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;
import java.util.UnknownFormatFlagsException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

/**
 * Realm DAO implementation
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public class RealmDao<T extends RealmObject> implements IDao<T> {
    protected Class<T> clazz;

    public RealmDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get one with a specific filter object
     * @param filter
     * @return
     */
    @Override
    public final Observable<T> getOne(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);
        T item = query.findFirst();

        if(item != null) {
            return Observable.just(realm.copyFromRealm(item));
        } else {
            return Observable.just(null);
        }
    }

    /**
     * Get one by id
     * @param id
     * @return
     */
    @Override
    public Observable<T> getById(String id) {
        return getOne(new Filter("id", id), null);
    }

    /**
     * Get all for a specific filters
     * @return
     */
    @Override
    public final Observable<List<T>> getAll(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);
        RealmResults<T> items = query.findAllSorted(sortingMode.key, convertToSort(sortingMode.sort));

        return Observable.just(realm.copyFromRealm(items));
    }

    /**
     * Insert one object
     * @return object inserted
     */
    @Override
    public final Observable<T> insertOrUpdate(T object) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        T inserted = realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();

        return Observable.just(realm.copyFromRealm(inserted));
    }

    /**
     * Insert or update all
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public final Observable<List<T>> insertOrUpdate(List<T> items) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        items = realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();

        return Observable.just(realm.copyFromRealm(items));
    }

    /**
     * Remove only these items
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public final Observable<Void> delete(List<T> items) {
        for(T elem : items) {
            if(elem.isManaged()) {
                elem.deleteFromRealm(); // potentially slow
            }
        }
        return Observable.just(null);
    }

    @Override
    public final Observable<Void> delete(T object) {
        if(object.isManaged()) {
            object.deleteFromRealm();
        }
        return Observable.just(null);
    }

    @Override
    public Observable<Void> deleteAll() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
        return Observable.just(null);
    }

    /**************************************************************************
     *   Utils
     *************************************************************************/

    /**
     * For all keys in filter add them to query
     * @param filter
     * @param query
     * @return
     */
    public RealmQuery<T> filterToQuery(Filter filter, RealmQuery<T> query) {
        if(filter != null) {
            for (String key : filter.keySet()) {
                if (filter.containsKey(key)) {
                    query = addRealmFilter(query, key, filter.get(key));
                }
            }
        }
        return query;
    }

    /**
     * Pick the right realm method depending on filter
     * @param query
     * @param key
     * @param kvp
     * @return
     */
    private RealmQuery<T> addRealmFilter(RealmQuery<T> query, String key, Filter.KeyValuePair kvp) {
        switch (kvp.filterType) {
            case EQUAL:
                return query.equalTo(key, kvp.value);
            case NOT_EQUAL:
                return query.notEqualTo(key, kvp.value);
            case GREATER_THAN:
                return query.greaterThan(key, Double.valueOf(kvp.value));
            case LESS_THAN:
                return query.lessThan(key, Double.valueOf(kvp.value));
            case GREATER_THAN_OR_EQUAL:
                return query.greaterThanOrEqualTo(key, Double.valueOf(kvp.value));
            case LESS_THAN_OR_EQUAL:
                return query.greaterThanOrEqualTo(key, Double.valueOf(kvp.value));
        }

        throw new UnknownFormatFlagsException("This type is unknow : " + kvp.filterType.toString());
    }

    /**
     * Convert SortType to realm Sort
     * @param st
     * @return
     */
    private Sort convertToSort(SortType st) {
        return st == SortType.ASCENDING ? Sort.ASCENDING : Sort.DESCENDING;
    }
}
