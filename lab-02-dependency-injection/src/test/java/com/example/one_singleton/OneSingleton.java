package com.example.one_singleton;


import com.example.DependenciesInjectorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OneSingleton {
    @Test
    public void singeletonsVsNotSingeltons() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(SingletonClass.class);
        myDI.register(NotSingletonClass.class);
        myDI.completeRegistration();

        var singeton1 = myDI.resolve(SingletonClass.class);
        var singeton2 = myDI.resolve(SingletonClass.class);
        System.out.println(singeton1);
        System.out.println(singeton2);

        var notSingleton1 = myDI.resolve(NotSingletonClass.class);
        var notSingleton2 = myDI.resolve(NotSingletonClass.class);
        Assertions.assertNotSame(notSingleton1, notSingleton2);
    }

}
