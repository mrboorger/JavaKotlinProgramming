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
        Assert.assertEquals(box, AppleBox.class);
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
}
