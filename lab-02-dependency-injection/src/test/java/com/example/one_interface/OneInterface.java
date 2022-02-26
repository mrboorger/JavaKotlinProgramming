package com.example.one_interface;

import com.example.DependenciesInjectorImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OneInterface {
    @Test
    public void norm() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Box.class, AppleBox.class);
        myDI.completeRegistration();
        var box = myDI.resolve(Box.class);
        Assert.assertEquals(box.getClass(), AppleBox.class);
    }

    @Test
    public void createImplementation() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Box.class, AppleBox.class);
        myDI.completeRegistration();

        var exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myDI.resolve(AppleBox.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("AppleBox isn't registered"));
    }

    @Test
    public void resolveOnlyInterface() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(Box.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("Box is an interface, " +
                "but expected a class. Use register(interface, implementation)"));
    }

    @Test
    public void resolve2WithClass() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(AppleBox.class, Box.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("AppleBox isn't an interface"));
    }

    @Test
    void resolveWithNoImplementation() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(Box.class, Cat.class);
        });
        Assert.assertTrue(exception.getMessage().contains("Cat isn't implements "));
    }

    @Test
    public void resolve2WithOnlyInterfaces() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(Box.class, Box.class);
        });
        Assert.assertTrue(exception.getMessage().contains("Box is an interface, but expected class"));
    }

    @Test
    void nullTest1() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(null, AppleBox.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("Class expected but null provided"));
    }

    @Test
    void nullTest2() {
        var myDI = new DependenciesInjectorImpl();
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myDI.register(Box.class, null);
        });
        Assert.assertTrue(exception.getMessage().endsWith("Class expected but null provided"));
    }
}
