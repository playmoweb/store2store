## About

Store2Store simplify the synchronization between storages. For instance, you can easily synchronize a REST API with a local Realm datastore.

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
compile 'com.github.playmoweb:store2store:<TAG>'
```


## Create a store or use an existing one

You can create your own store implementing AbstractService : 

```java
public class MyStoreService<T> extends AbstractService<T> {
    ...
}
```

Have a look to existing implementations  : 

- Realm : https://github.com/playmoweb/store2realm


