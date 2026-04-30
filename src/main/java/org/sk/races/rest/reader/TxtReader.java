package org.sk.races.rest.reader;

import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
import org.sk.races.rest.entities.Runner;

import java.io.InputStream;
import java.util.Scanner;

public class TxtReader {
    public static Race readRace(String fileName, String raceName) {
        Race race = new Race(raceName);

        try {
            InputStream inputStream = TxtReader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                return race;
            }
            Scanner sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String LastName = parts[2];
                        String genderStr = parts[3].trim();
                        int totalSeconds = RaceItem.parseTimeToSeconds(parts[7].trim());
                        int age = Integer.parseInt(parts[4].trim());
                        String country = parts[5].trim();
                        String fullName = name + " " + LastName;
                        Gender gender = Gender.fromString(genderStr);
                        Runner runner = new Runner(fullName, age, country, gender);
                        RaceItem raceItem = new RaceItem(id, runner, totalSeconds);
                        race.addResult(raceItem);

                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
        }
        return race;
    }
}