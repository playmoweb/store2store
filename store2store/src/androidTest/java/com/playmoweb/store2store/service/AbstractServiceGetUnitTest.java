package com.playmoweb.store2store.service;

import android.support.test.runner.AndroidJUnit4;

import com.playmoweb.store2store.mock.MemoryService;
import com.playmoweb.store2store.mock.TestModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.subscriptions.CompositeSubscription;

/**
 * Test all get operations
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
@RunWith(AndroidJUnit4.class)
public class AbstractServiceGetUnitTest {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private MemoryService service = new MemoryService(TestModel.class);

    @Before
    public void before() {

    }

    @After
    public void after() {
        subscriptions.clear();
    }

    @Test
    public void getOne() throws Exception {

    }
}
