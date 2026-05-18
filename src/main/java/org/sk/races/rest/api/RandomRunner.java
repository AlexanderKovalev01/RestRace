package org.sk.races.rest.api;

import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Runner;

import java.util.Random;

public class RandomRunner {
    private static final String[] NAMES = {"John", "Jane", "Mike", "Sarah", "Tom", "Anna", "David", "Maria"};
    private static final String[] SURNAMES = {"Smith", "Johnson", "Brown", "Lee", "Kim", "Chen"};
    private static final String[] COUNTRIES = {"USA", "UK", "Germany", "France", "Japan"};
    private static final Random random = new Random();

    public static Runner generateRunner() {
        String name = NAMES[random.nextInt(NAMES.length)] + " " + SURNAMES[random.nextInt(SURNAMES.length)];
        int age = 18 + random.nextInt(50);
        String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        return new Runner(name, age, country, gender);
    }
}

