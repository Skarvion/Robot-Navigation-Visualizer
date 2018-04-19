package org.robotnav.DataParser;

import org.robotnav.map.Grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTextFile {

    public static Grid parseGridTextFile(String fileName) {
        Grid gridMap = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                switch (lineNumber) {
                    case 0:
                        int[] gridSize = parse2IntRegex(line);
                        gridMap = new Grid(gridSize[1], gridSize[0]);
                        break;

                    case 1:
                        int[] robotLocation = parse2IntRegex(line);
                        gridMap.setRobot(robotLocation[0], robotLocation[1]);
                        break;

                    case 2:
                        int[] goalLocation = parse2IntRegex(line);
                        gridMap.setGoal(goalLocation[0], goalLocation[1]);
                        break;

                    default:
                        int[] wallLocation = parse4IntRegex(line);
                        gridMap.setWall(wallLocation[0], wallLocation[1], wallLocation[2], wallLocation[3]);
                        break;
                }

                lineNumber++;
            }

        } catch (Exception ex) {
            return null;
        }

        return gridMap;
    }

    private static int[] parse2IntRegex(String line) {
        int[] result = new int[2];

        Pattern parserPattern = Pattern.compile("(\\d+)");
        Matcher matcher = parserPattern.matcher(line);

        int i = 0;
        for (i = 0; i < 2; i++) {
            if (matcher.find()) {
                result[i] = Integer.parseInt(matcher.group());
            } else {
                break;
            }
        }

        return result;
    }

    private static int[] parse4IntRegex(String line) {
        int[] result = new int[4];

        Pattern parserPattern = Pattern.compile("(\\d+)");
        Matcher matcher = parserPattern.matcher(line);

        for (int i = 0; i < 4; i++) {
            if (matcher.find()) {
                result[i] = Integer.parseInt(matcher.group());
            } else {
                break;
            }
        }

        return result;
    }
}
