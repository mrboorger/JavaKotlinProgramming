package com.example.one_singleton_interface;


import com.example.DependenciesInjectorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OneSingletonInterface {
    @Test
    public void testSingletonInterface() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(SomeSingleton.class, SomeSingletonImpl.class);
        myDI.completeRegistration();

        var singeton1 = myDI.resolve(SomeSingleton.class);
        var singeton2 = myDI.resolve(SomeSingleton.class);
        Assertions.assertSame(singeton1, singeton2);
    }
}
