package com.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

public class DependenciesInjectorImpl implements DependenciesInjector {
    boolean mIsCompleteReg = false;

    final HashSet<Class<?>> mRegisteredClasses = new HashSet<>();
    final HashMap<Class<?>, Class<?>> mInterfacesImplementation = new HashMap<>();
    final HashMap<Class<?>, Class<?>[]> mNeededParams = new HashMap<>();
    final HashMap<Class<?>, Constructor<?>> mConstructor = new HashMap<>();

    final HashSet<Class<?>> isSingleton = new HashSet<>();
    final HashMap<Class<?>, Object> singletonInstance = new HashMap<>();


    @Override
    public void register(Class<?> cl) {
        nullCheck(cl);
        if (mIsCompleteReg) {
            throw new RuntimeException("Can't register. Registration was completed");
        }
        if (mRegisteredClasses.contains(cl)) {
            throw new RuntimeException(cl + " has already registered");
        }
        if (cl.isInterface()) {
            throw new IllegalArgumentException(cl + " ");
        }

        mRegisteredClasses.add(cl);
        var constructor = getInjectConstructor(cl);
        mConstructor.put(cl, constructor);
        mNeededParams.put(cl, constructor.getParameterTypes());

        if (constructor.isAnnotationPresent(Singleton.class)) {
            isSingleton.add(cl);
        }
    }

    @Override
    public void register(Class<?> interf, Class<?> implCl) {
        nullCheck(interf);
        nullCheck(implCl);
        if (mIsCompleteReg) {
            throw new RuntimeException("Can't register. Registration was completed");
        }
        if (mInterfacesImplementation.containsKey(interf)) {
            throw new RuntimeException(interf + " has already registered");
        }
        if (!interf.isInterface()) {
            throw new IllegalArgumentException(interf + " isn't an interface");
        }
        if (!interf.isAssignableFrom(implCl)) {
            throw new IllegalArgumentException(implCl + " isn't implements " + interf);
        }

        mInterfacesImplementation.put(interf, implCl);
        var constructor = getInjectConstructor(implCl);
        mConstructor.put(implCl, constructor);
        mNeededParams.put(implCl, constructor.getParameterTypes());

        if (constructor.isAnnotationPresent(Singleton.class)) {
            isSingleton.add(implCl);
        }
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
        if (cl.isInterface()) {
            if (!mInterfacesImplementation.containsKey(cl)) {
                throw new RuntimeException(cl + " isn't registered");
            }
            return cl.cast(createClass(mInterfacesImplementation.get(cl)));
        } else {
            if (!mRegisteredClasses.contains(cl)) {
                throw new RuntimeException(cl + " isn't registered");
            }
            return createClass(cl);
        }
    }

    private <T> T createClass(Class<T> cl) {
        var constructor = mConstructor.get(cl);
        var parameters = mNeededParams.get(cl);
        var parametersValues = new ArrayList<>();

        if (singletonInstance.containsKey(cl)) {
            return (T) singletonInstance.get(cl);
        }

        if (parameters != null) {
            for (var to : parameters) {
                parametersValues.add(resolve(to));
            }
        }
        try {
            var instance = cl.cast(constructor.newInstance(parametersValues.toArray()));
            if (isSingleton.contains(cl)) {
                singletonInstance.put(cl, instance);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            assert false : "Failed to construct " + cl;
        }
        assert false : "Unreachable";
        return null;
    }

    private Constructor<?> getInjectConstructor(Class<?> cl) {
        var injectConstructors = Arrays.stream(cl.getConstructors()).
                filter(constr -> constr.isAnnotationPresent(Inject.class)).
                collect(Collectors.toList());
        if (injectConstructors.size() == 0) {
            throw new RuntimeException(cl + " doesn't have an inject constructor");
        }
        if (injectConstructors.size() > 1) {
            throw new RuntimeException(cl + "has more than 1 inject constructor");
        }
        return injectConstructors.get(0);
    }

    static private void nullCheck(Class<?> cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Class expected but null provided");
        }
    }
}
