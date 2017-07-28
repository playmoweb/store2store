package com.playmoweb.store2store.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by hoanghiep on 7/28/17.
 */

public abstract class CustomObserverRx2<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {
        getCompositeDisposable().add(d);
    }

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
        onCompleted(false);
    }

    public abstract CompositeDisposable getCompositeDisposable();

    public void onCompleted(boolean isUpdated) {
    }

    public abstract void onError(Throwable e, boolean isUpdated);

    public abstract void onNext(T t, boolean isUpdated);
}
