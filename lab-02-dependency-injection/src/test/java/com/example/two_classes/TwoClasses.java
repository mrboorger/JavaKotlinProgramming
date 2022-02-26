package com.example.two_classes;

import com.example.DependenciesInjectorImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TwoClasses {
    @Test
    public void Norm() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(AppleBox.class);
        myDI.register(Apple.class);
        myDI.completeRegistration();
        Assert.assertEquals(myDI.resolve(Apple.class).getClass(), Apple.class);
        Assert.assertEquals(myDI.resolve(AppleBox.class).getClass(), AppleBox.class);
    }

    @Test
    public void dependentClassOnly() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(AppleBox.class);
        myDI.completeRegistration();
        var exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myDI.resolve(AppleBox.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("Apple isn't registered"));
    }
}
