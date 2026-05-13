package org.sk.races.rest.entities;

public class RaceItem {

    private int id;
    private Runner runner;
    private int time;

    public RaceItem(int id, Runner runner, int time) {
        this.runner = runner;
        this.time = time;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Runner getRunner() {
        return runner;
    }

    public int getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStringTime() {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public String toString() {
        return runner.getName() + " - " + getStringTime();
    }

    public static int parseTimeToSeconds(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}