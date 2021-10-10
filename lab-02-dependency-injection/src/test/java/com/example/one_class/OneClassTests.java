package com.example.one_class;

import com.example.DependenciesInjectorImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class OneClassTests {
    @Test
    public void Basic() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Apple.class);
        myDI.completeRegistration();
        Assert.assertEquals(myDI.resolve(Apple.class).getClass(), Apple.class);
    }

    @Test
    public void resolveBeforeComplete() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Apple.class);
        var exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myDI.resolve(Apple.class);
        });

        Assert.assertTrue(exception.getMessage().endsWith("Registration isn't completed"));
    }

    @Test
    public void doubleRegisterTest() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Apple.class);
        var exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myDI.register(Apple.class);
        });
        Assert.assertTrue(exception.getMessage().endsWith("has already registered"));
    }

    @Test
    public void doubleCompleteRegistrationTest() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Apple.class);
        myDI.completeRegistration();
        var exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myDI.completeRegistration();
        });
        Assert.assertTrue(exception.getMessage().endsWith("Can't complete registration. Registration was complete"));
    }
}
