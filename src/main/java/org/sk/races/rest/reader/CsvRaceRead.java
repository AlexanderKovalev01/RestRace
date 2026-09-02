package org.sk.races.rest.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
import org.sk.races.rest.entities.Runner;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;

public class CsvRaceRead {

    private static int parseTimeToSeconds(String timeStr) {
        String[] parts = timeStr.split(":");
        if (parts.length != 3) return 0;
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    private static Time secondsToTime(int seconds) {
        return new Time(seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public static Race readRaceFromCSV(String filePath, String raceName) {
        Race race = new Race(raceName);

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean headerSkipped = false;

            while ((line = csvReader.readNext()) != null) {
                if (headerSkipped) {
                    int id = Integer.parseInt(line[0].trim());
                    String firstName = line[1].trim();
                    String lastName = line[2].trim();
                    String fullName = firstName + " " + lastName;
                    int age = Integer.parseInt(line[4].trim());
                    String country = line[5].trim();
                    Gender gender = Gender.fromString(line[3].trim());
                    Runner runner = new Runner(fullName, age, country, gender);

                    int seconds = parseTimeToSeconds(line[7].trim());
                    Time time = secondsToTime(seconds);

                    RaceItem item = new RaceItem(id, runner, time);
                    race.addResult(item);
                } else {
                    headerSkipped = true;
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading the file: " + filePath + " - " + e.getMessage());
        }

        return race;
    }
}