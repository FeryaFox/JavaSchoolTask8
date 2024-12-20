package ru.feryafox.Task1;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.zip.*;

public class CacheHandler implements InvocationHandler {
    private final Object service;
    private final String rootDir;
    private final Map<String, Object> inMemoryCache = new HashMap<>();

    public CacheHandler(Object service, String rootDir) {
        this.service = service;
        this.rootDir = rootDir;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Cache cache = method.getAnnotation(Cache.class);

        if (cache == null) {
            return method.invoke(service, args);
        }

        String cacheKey = buildCacheKey(method, args, cache.identityBy());
        if (cache.cacheType() == CacheType.IN_MEMORY) {
            return handleInMemoryCache(cacheKey, method, args, cache);
        } else {
            return handleFileCache(cacheKey, method, args, cache);
        }
    }

    private Object handleInMemoryCache(String cacheKey, Method method, Object[] args, Cache cache) throws Throwable {
        if (inMemoryCache.containsKey(cacheKey)) {
            System.out.println("Returning from memory cache: " + cacheKey);
            return inMemoryCache.get(cacheKey);
        }

        Object result = method.invoke(service, args);
        Object trimmedResult = trimListIfNeeded(result, cache.listLimit());
        inMemoryCache.put(cacheKey, trimmedResult);
        return trimmedResult;
    }

    private Object handleFileCache(String cacheKey, Method method, Object[] args, Cache cache) throws Throwable {
        String fileName = rootDir + File.separator + (cache.fileNamePrefix().isEmpty() ? method.getName() : cache.fileNamePrefix()) + "_" + cacheKey + ".cache";
        File file = new File(fileName);

        if (file.exists()) {
            System.out.println("Returning from file cache: " + fileName);
            return deserializeFromFile(file, cache.zip());
        }

        Object result = method.invoke(service, args);
        Object trimmedResult = trimListIfNeeded(result, cache.listLimit());
        serializeToFile(trimmedResult, file, cache.zip());
        return trimmedResult;
    }

    private String buildCacheKey(Method method, Object[] args, Class<?>[] identityBy) {
        StringBuilder keyBuilder = new StringBuilder(method.getName());
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (identityBy.length == 0 || Arrays.asList(identityBy).contains(args[i].getClass())) {
                    keyBuilder.append("_").append(args[i]);
                }
            }
        }
        return keyBuilder.toString();
    }

    private Object trimListIfNeeded(Object result, int limit) {
        if (result instanceof List<?> list && list.size() > limit) {
            return list.subList(0, limit);
        }
        return result;
    }

    private void serializeToFile(Object result, File file, boolean zip) throws IOException {
        if (!(result instanceof Serializable)) {
            throw new IllegalArgumentException("Result is not serializable. Implement Serializable interface.");
        }

        if (zip) {
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
                zos.putNextEntry(new ZipEntry("cache"));

                ObjectOutputStream oos = new ObjectOutputStream(zos);
                oos.writeObject(result);
                oos.flush();

                zos.closeEntry();
                System.out.println("Successfully written ZIP file: " + file.getAbsolutePath());
            }
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(result);
                System.out.println("Successfully written file: " + file.getAbsolutePath());
            }
        }
    }


    private Object deserializeFromFile(File file, boolean zip) throws IOException, ClassNotFoundException {
        if (zip) {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry = zis.getNextEntry();
                if (entry == null) {
                    throw new EOFException("No entries found in the ZIP file: " + file.getName());
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    Object result = ois.readObject();
                    System.out.println("Successfully read from ZIP file: " + file.getAbsolutePath());
                    return result;
                }
            }
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return ois.readObject();
            }
        }
    }
}

