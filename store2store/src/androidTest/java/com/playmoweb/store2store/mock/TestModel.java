package com.playmoweb.store2store.mock;

/**
 * Bean for tests purposes
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
public class TestModel{
    private int id;
    private String name;
    private boolean available = false;

    public TestModel(int id) {
        this.id = id;
    }

    public TestModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TestModel){
            return id == ((TestModel) obj).getId();
        }
        return super.equals(obj);
    }
}
