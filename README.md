## About

Store2Store simplify the synchronization between storages. For instance, you can easily synchronize a REST API with a local Realm datastore.

## Installation

### Gradle
```
compile 'com.github.playmoweb:store2store:1.0'
```


## Create a store or use an existing one

You can create your own store implementing AbstractService : 

```java
public class MyStoreService<T> extends AbstractService<T> {
    ...
}
```

Or use one implemented by this library : 

- Realm service

## Usage

This is an example of a sync between an API and a local Realm storage :

### Create your service based on a store

1. Extend RealmService<T>


```java
public class MyObjectModelService extends BaseRealmService<MyObjectModel> {
    
}
```

2. Implement required methods


We want to get all objects from our API. 
This example use Dagger for the injection and the attribut ApiService is a Retrofit class to connect to your API. The class below is just an entry point to it.

We implement the getAll method from the RealmService to point to our API. This method can call any kind of service (file, sql, ...), it doesn't matter.

```java
public class MyObjectModelService extends BaseRealmService<MyObjectModel> {
    private final ApiService apiService;

    @Inject
    public MyObjectModelService(ApiService apiService) {
        super(MyObjectModel.class); // give to the AbstractService the Class used
        this.apiService = apiService;
    }

    @Override
    protected Observable<List<MyObjectModel>> getAll(Filter filter, SortingMode sortingMode) {
        // in this example the filter object and the sortingMode are not used
        return apiService.getAllMyObject(); // return an Observable<List<MyObjectModel>>
    }
}
```

3. Call from your presenter (MVP) your service

```java
private final MyObjectModelService myObjectService; // injected by dagger in the constructor

public void loadAllMyObjects() {
    // no filter and no sortingMode
    final Subscription s = myObjectService.getAll(null, null, getAllMyObjectObserver)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(getAllMyObjectObserver);

    subscriptions.add(s);
}

/**
* We create a CustomObserver to handle callbacks from the library.
*/
private final CustomObserver<List<MyObjectModel>> getAllMyObjectObserver = new CustomObserver<List<MyObjectModel>>() {
    @Override
    public void onError(Throwable e, boolean isUpdated) {
        // Log the error if needed
    }

    @Override
    public void onNext(List<MyObjectModel> d, boolean isUpdated) {
        // This method will be fired twice :
        //     - The first call will come from Realm and isUpdated will be false
        //     - The second call will come from our API and after the sync with Realm.
        //       The datas will be fresh and fully updated. 
    }
};
````

