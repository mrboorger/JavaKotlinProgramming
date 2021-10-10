package com.example.some_classes;

import com.example.DependenciesInjectorImpl;
import com.example.two_classes.Apple;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SomeClasses {
    @Test
    public void NormRoomTest() {
        var myDI = new DependenciesInjectorImpl();
        myDI.register(Room.class);
        myDI.register(Pillow.class);
        myDI.register(Bed.class);
        myDI.register(Blanket.class);
        myDI.register(Table.class);
        myDI.completeRegistration();

        Assert.assertEquals(myDI.resolve(Table.class).getClass(), Table.class);
        Assert.assertEquals(myDI.resolve(Room.class).getClass(), Room.class);
        Assert.assertEquals(myDI.resolve(Bed.class).getClass(), Bed.class);
        Assert.assertEquals(myDI.resolve(Pillow.class).getClass(), Pillow.class);
        Assert.assertEquals(myDI.resolve(Blanket.class).getClass(), Blanket.class);
    }
}
