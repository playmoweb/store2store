package com.playmoweb.store2store.utils;

import io.reactivex.observers.DisposableObserver;

/**
 * This class replace the default observers used with RX to add more datas.
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017
 *
 * @updated hoanghiep
 * @date    28/07/2017
 */
public abstract class CustomObserver<T> extends DisposableObserver<T> {

    @Override
    public void onNext(T value) {
        onNext(value, false);
    }

    @Override
    public void onError(Throwable e) {
        onError(e, false);
    }

    @Override
    public void onComplete() {
        onComplete(false);
    }

    public void onComplete(boolean isUpdated) {

    }

    public abstract void onError(Throwable e, boolean isUpdated);

    public abstract void onNext(T t, boolean isUpdated);
}
