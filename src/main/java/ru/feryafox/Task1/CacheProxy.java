package ru.feryafox.Task1;

import java.lang.reflect.Proxy;

public class CacheProxy {
    private final String rootDir;

    public CacheProxy(String rootDir) {
        this.rootDir = rootDir;
    }

    public <T> T cache(T service) {
        return (T) Proxy.newProxyInstance(
                service.getClass().getClassLoader(),
                service.getClass().getInterfaces(),
                new CacheHandler(service, rootDir)
        );
    }
}