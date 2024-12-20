package ru.feryafox.Task1;

import java.util.Date;
import java.util.List;

public interface Service {
    @Cache(cacheType = CacheType.FILE, fileNamePrefix = "data", zip = true, identityBy = {String.class})
    List<String> run(String item, double value, Date date) throws InterruptedException;

}