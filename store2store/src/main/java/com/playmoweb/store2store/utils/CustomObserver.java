package com.playmoweb.store2store.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
public abstract class CustomObserver<T> implements Observer<T> {
    private final CompositeDisposable compositeDisposable;

    /**
     * Default constructor for CustomObserver
     * @warn    Do not pass null if you don't override getCompositeDisposable() method.
     * @param   compositeDisposable
     */
    public CustomObserver(CompositeDisposable compositeDisposable){
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * Empty constructor of a CustomObserver
     * @warn    You have to override the getCompositeDisposable to prevent NPE when subscribing !
     */
    public CustomObserver(){
        this(null);
    }

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
        onComplete(false);
    }

    public CompositeDisposable getCompositeDisposable(){
        if(compositeDisposable == null){
            throw new NullPointerException("A custom observer need a CompositeDisposable to work properly. The reference given in the constructor is null.");
        }
        return compositeDisposable;
    }

    public void onComplete(boolean isUpdated) {

    }

    public abstract void onError(Throwable e, boolean isUpdated);

    public abstract void onNext(T t, boolean isUpdated);
}
