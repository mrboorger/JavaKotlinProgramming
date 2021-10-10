package com.example;

public interface DependenciesInjector {
    void register(Class<?> cl);

    void register(Class<?> interf, Class<?> implCl);

    void completeRegistration();

    <T >T resolve(Class<T> cl);
}
