package org.sk.races.rest.reader;

import org.sk.races.rest.entities.Race;

import java.util.HashMap;
import java.util.Map;

public class RaceCache {
    private static RaceCache instance;
    private Map<String, Race> races;

    private RaceCache() {
        races = new HashMap<>();
    }

    public static RaceCache getInstance() {
        if (instance == null) {
            instance = new RaceCache();
        }
        return instance;
    }

    public void addRace(String name, Race race) {
        races.put(name, race);
    }

    public Race getRace(String name) {
        return races.get(name);
    }
}
