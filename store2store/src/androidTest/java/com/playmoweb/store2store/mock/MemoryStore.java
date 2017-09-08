package com.playmoweb.store2store.mock;

import com.playmoweb.store2store.store.StoreService;

/**
 * Memory service for testModel only
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
public class MemoryStore extends StoreService<TestModel> {
    public MemoryStore() {
        super(TestModel.class, new MemoryDao());
    }


}
