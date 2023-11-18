package com.purah.checker;

public class CheckInstance<INSTANCE> {
    INSTANCE instance;

    private CheckInstance(INSTANCE instance) {
        this.instance = instance;
    }

    public  static <T> CheckInstance<T> create(T instance) {
        return new CheckInstance<>(instance);
    }

    public INSTANCE instance() {
        return instance;
    }
}
