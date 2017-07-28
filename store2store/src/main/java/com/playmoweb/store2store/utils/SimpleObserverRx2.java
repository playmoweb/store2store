package com.playmoweb.store2store.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by hoanghiep on 7/28/17.
 */

public class SimpleObserverRx2<T> implements Observer<T> {
    private final CustomObserverRx2<T> observerRx2;

    public SimpleObserverRx2(CustomObserverRx2<T> observerRx2) {
        this.observerRx2 = observerRx2;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (observerRx2 != null) {
            observerRx2.getCompositeDisposable().add(d);
        }
    }

    @Override
    public void onNext(T value) {
        if (observerRx2 != null) {
            observerRx2.onNext(value, true);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (observerRx2 != null) {
            observerRx2.onError(e, true);
        }
    }

    @Override
    public void onComplete() {
        if (observerRx2 != null) {
            observerRx2.onCompleted(true);
        }
    }
}
