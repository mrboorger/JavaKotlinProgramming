package com.example.one_singleton_interface;

import javax.inject.Inject;
import javax.inject.Singleton;

public class SomeSingletonImpl implements SomeSingleton {
    @Inject
    @Singleton
    public SomeSingletonImpl() {
    }
}
