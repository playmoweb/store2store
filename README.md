## About

Store2Store simplify the synchronization between Store of datas. 
For instance, you can easily synchronize a REST API with a local Realm datastore.

**This is the rxJava2 implementation.** 

*If you want to use a rxJava1 version, please use the 1.x.x tags (API is different).*


## Installation with gradle

```
# main build.gradle file
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' } # add this line
    }
}
```

```
# app/build.gradle file
compile 'com.github.playmoweb:store2store:3.0.0'
```


## Create a store or use an existing one

You can create your own store implementing StoreService : 

```java
public class MyStore<T> extends StoreService<T> {
    public MyStore(Class<T> clazz) {
        super(clazz, new MyStoreDao());
    }
    
    // internal class for the DAO  
    private class MyStoreDao extends StoreDao<T> {
        // implements all methods of the abstract class you can need (some methods needs other to work)
    }
}
```

## Sync your store to another one

```
public class MyConcreteModelStore extends MyStore<MyModel> {
    public MyConcreteModelStore(){
        super(MyModel.class);
        this.syncWith(new MemoryStore()); // eg: sync with a newly created memoryStore
        // you can inject a store with dagger and syncWith() here too :)
    }
    
    // Here you just have to implements the methods you need 
}
```

## Existing implementations

- Realm : https://github.com/playmoweb/store2realm


## Contributors
Please see [CONTRIBUTORS.md](https://github.com/playmoweb/store2store/blob/master/CONTRIBUTORS.md)