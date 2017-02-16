package com.playmoweb.store2store.utils;

import rx.Observer;

/**
 * This class replace the default observers used with RX to add more datas.
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public abstract class CustomObserver<T> implements Observer<T> {

    @Override
    @Deprecated
    public final void onCompleted() {
        onCompleted(false);
    }

    @Override
    @Deprecated
    public final void onError(Throwable e) {
        onError(e, false);
    }

    @Override
    @Deprecated
    public final void onNext(T t) {
        onNext(t, false);
    }

    public void onCompleted(boolean isUpdated) {

    }

    public abstract void onError(Throwable e, boolean isUpdated);
    public abstract void onNext(T t, boolean isUpdated);
}
