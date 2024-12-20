package ru.feryafox.Task1;

import java.util.Date;
import java.util.List;

public class Task1 {
    public static void main(String[] args) throws InterruptedException {
        CacheProxy cacheProxy = new CacheProxy("cache");
        Service service = cacheProxy.cache(new ServiceImpl());

        List<String> result1 = service.run("work1", 10, new Date());
        List<String> result2 = service.run("work1", 10, new Date()); // Из кэша

        System.out.println(result1);
        System.out.println(result2);
    }
}
