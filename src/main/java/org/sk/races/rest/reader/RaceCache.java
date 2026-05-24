package org.sk.races.rest.reader;

import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceCache {
    private static RaceCache instance;
    private Map<String, Race> races;
    private List<Runner> generatedRunners;

    private RaceCache() {
        races = new HashMap<>();
        generatedRunners = new ArrayList<>();
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

    public void addRunners(List<Runner> runners) {
        generatedRunners.addAll(runners);
    }

    public List<Runner> getAllRunners() {
        return new ArrayList<>(generatedRunners);
    }
}