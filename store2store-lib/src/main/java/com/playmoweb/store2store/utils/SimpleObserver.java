package com.playmoweb.store2store.utils;

import rx.Observer;

/**
 * Class used internally to dispatch an updated state
 * @param <T>
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public class SimpleObserver<T> implements Observer<T> {
    private final CustomObserver<T> otherObserver;

    public SimpleObserver(CustomObserver<T> otherObserver) {
        this.otherObserver = otherObserver;
    }

    @Override
    public void onCompleted() {
        if(otherObserver != null) {
            otherObserver.onCompleted(true);
        }
    }

    @Override
    public void onError(Throwable e) {
        if(otherObserver != null) {
            otherObserver.onError(e, true);
        }
    }

    @Override
    public void onNext(T t) {
        if(otherObserver != null) {
            otherObserver.onNext(t, true);
        }
    }
}
