package com.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

public class DependenciesInjectorImpl implements DependenciesInjector {
    boolean mIsCompleteReg = false;
    final HashMap<Class<?>, Constructor<?>> mConstructor = new HashMap<>();
    final HashMap<Class<?>, Class<?>> mRegisteredInterfaces = new HashMap<>();
    final HashMap<Class<?>, Class<?>[]> mNeededParams = new HashMap<>();


    @Override
    public void register(Class<?> cl) {
        nullCheck(cl);
        if (mIsCompleteReg) {
            throw new RuntimeException("Can't register. Registration was completed");
        }
        if (mConstructor.containsKey(cl)) {
            throw new RuntimeException(cl + " has already registered");
        }

        var injectConstructors = Arrays.stream(cl.getConstructors()).
                filter(constr -> constr.isAnnotationPresent(Inject.class)).
                collect(Collectors.toList());
        if (injectConstructors.size() == 0) {
            throw new RuntimeException(cl + " doesn't have an inject constructor");
        }
        if (injectConstructors.size() > 1) {
            throw new RuntimeException(cl + "has more than 1 inject constructor");
        }

        mConstructor.put(cl, injectConstructors.get(0));
        mNeededParams.put(cl, injectConstructors.get(0).getParameterTypes());
    }

    @Override
    public void register(Class<?> interf, Class<?> implCl) {
        nullCheck(interf);
        nullCheck(implCl);
        if (mIsCompleteReg) {
            throw new RuntimeException("Can't register. Registration was completed");
        }
        if (!interf.isInterface()) {
            throw new IllegalArgumentException(interf + " doesn't an interface");
        }
        if (!interf.isAssignableFrom(implCl)) {
            throw new IllegalArgumentException(implCl + " doesn't an implementation of " + interf);
        }

        mRegisteredInterfaces.put(interf, implCl);
    }

    @Override
    public void completeRegistration() {
        if (mIsCompleteReg) {
            throw new RuntimeException("Can't complete registration. Registration was completed");
        }
        mIsCompleteReg = true;
    }

    @Override
    public <T> T resolve(Class<T> cl) {
        if (!mIsCompleteReg) {
            throw new RuntimeException("Registration isn't completed");
        }
        nullCheck(cl);
        System.out.println(cl);
        if (!mConstructor.containsKey(cl)) {
            throw new RuntimeException(cl + " isn't registered");
        }
        var constructor = mConstructor.get(cl);
        assert constructor != null : "kekkk";
        var parameters = mNeededParams.get(cl);
        var parametersValues = new ArrayList<>();

        if (parameters != null) {
            for (var to : parameters) {
                parametersValues.add(resolve(to));
            }
        }
        try {
            return (T) constructor.newInstance(parametersValues.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            assert false : "Failed to construct " + cl;
        }
        assert false : "Unreacheble";
        return null;
    }

    static private void nullCheck(Class<?> cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Class expected but null provided");
        }
    }
}
