package org.sk.races.rest.entities;

import java.sql.Time;

public class RaceItem {

    private int id;
    private Runner runner;
    private Time time;

    public RaceItem(int id, Runner runner, Time time) {
        this.id = id;
        this.runner = runner;
        this.time = time;
    }
    public static int parseTimeToSeconds(String timeStr) {
        String[] parts = timeStr.split(":");
        if (parts.length != 3) return 0;
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
    public static Time secondsToTime(int seconds) {
        return new Time(seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public int getId() {
        return id;
    }

    public Runner getRunner() {
        return runner;
    }

    public Time getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getStringTime() {
        return time.toString();
    }

    @Override
    public String toString() {
        return runner.getName() + " - " + getStringTime();
    }
}