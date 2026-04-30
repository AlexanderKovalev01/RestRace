package org.sk.races.rest.entities;

import java.util.ArrayList;
import java.util.List;

public class Race {

    private String name;
    private List<RaceItem> results;

    public Race(String name) {
        this.name = name;
        this.results = new ArrayList<>();
    }
    public RaceItem findRunnerById(int runnerId) {
        RaceItem found = null;
        for (RaceItem item : results) {
            if (item.getId() == runnerId) {
                return  item;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void addResult(RaceItem item) {
        results.add(item);
    }

    public List<RaceItem> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "Race: " + name;
    }
}