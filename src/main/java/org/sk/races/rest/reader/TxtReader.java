package org.sk.races.rest.reader;

import org.sk.races.rest.api.RestRacesApp;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
import org.sk.races.rest.entities.Runner;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

public class TxtReader {
    private static final Logger logger = Logger.getLogger(TxtReader.class.getName());

    public static Race readRace(String fileName, String raceName) {
        Race race = new Race(raceName);

        try {
            InputStream inputStream = TxtReader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                logger.severe("File not found" + fileName);
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
                        logger.warning("Failed to parse data" + e.getMessage());
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            logger.severe("Failed to read race data from file" + e.getMessage());
        }
        return race;
    }
}