package ru.feryafox.Task1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ServiceImpl implements Service, Serializable {
    @Override
    public List<String> run(String item, double value, Date date) throws InterruptedException {
        System.out.println("Начало выполнение тяжелой работы...");
        Thread.sleep(3000);
        return Arrays.asList("Результат для " + item, "Значение: " + value);
    }
}