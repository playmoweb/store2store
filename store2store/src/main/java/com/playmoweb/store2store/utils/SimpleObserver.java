package com.playmoweb.store2store.utils;

import io.reactivex.observers.DisposableObserver;

/**
 * Class used internally to dispatch an updated state
 * @param <T>
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017
 *
 * @updated hoanghiep
 * @date    28/07/2017
 */
public class SimpleObserver<T> extends DisposableObserver<T> {
    private final CustomObserver<T> observer;

    public SimpleObserver(CustomObserver<T> observer) {
        this.observer = observer;
    }

    @Override
    public void onNext(T value) {
        if (observer != null) {
            observer.onNext(value, true);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (observer != null) {
            observer.onError(e, true);
        }
    }

    @Override
    public void onComplete() {
        if (observer != null) {
            observer.onComplete(true);
        }
    }
}
