package org.robotnav;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.robotnav.DataParser.GridGenerator;
import org.robotnav.DataParser.ParseTextFile;
import org.robotnav.Robot.Direction;
import org.robotnav.SearchAlgorithm.SearchAlgorithm;

import org.robotnav.gui.controller.MapController;
import org.robotnav.map.Grid;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class where the program first started, extend Application to run the GUI component
 */
public class Main extends Application {

    // Grid map to be accessed by both the CLI and GUI component
    private static Grid map;
    // Controller class that is used to bridge between the GUI view and search algorithm model
    private static MapController controller = new MapController();

    /**
     * Used for JavaFX to initialize GUI window
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Search Algorithm Visualizer");
        primaryStage.setScene(new Scene(controller, 1000, 500));
        primaryStage.show();

        // Send the map to the controller
        controller.setGridMap(map);
    }

    /**
     * Handle the argument accepted by the program, see the report for full details of command available
     * @param args argument for the program to run (available list of arguments is documented in the report)
     */
    public static void main(String[] args) {
        if (args.length == 4 || args.length == 5) {
            if (args[0].equals("generate")) {
                try {
                    int gridX = Integer.parseInt(args[1]);
                    int gridY = Integer.parseInt(args[2]);
                    try {
                        int testCase = Integer.parseInt(args[3]);
                        String outputFile = args[4];
                        automaticTesting(gridX, gridY, testCase, outputFile);
                        System.out.println("Automatic testing completed: " + outputFile);
                    } catch (NumberFormatException nfe) {
                        map = GridGenerator.generateGrid(gridX, gridY);
                        handleSearchAlgorithm(args[3], "Generated map");
                        if (args.length == 5) {
                            if (args[4].equals("-gui")) launch(args);
                        }
                        return;
                    }

                } catch (NumberFormatException nfe) {
                    System.out.println("Argument does not match");
                    return;
                }
            }
        }

        else if (args.length == 2 || args.length == 3) {
            map = ParseTextFile.parseGridTextFile(args[0]);
            if (map == null) {
                System.out.println("Error reading file");
                return;
            }
            handleSearchAlgorithm(args[1], args[0]);
            if (args.length == 3) {
                if (args[2].equals("-gui")) launch(args);
            } else System.exit(0);
        }
    }

    /**
     * Handle search algorithm that is received from the program argument
     * @param searchAlgorithm search algorithm
     * @param filename
     */
    private static void handleSearchAlgorithm(String searchAlgorithm, String filename) {
        String upperInput = searchAlgorithm.toUpperCase();

        SearchAlgorithm selectedSA = SearchAlgorithm.getSearchAlgorithm(upperInput, null);
        if (selectedSA != null) System.out.println(filename + " " + upperInput + " " + selectedSA.getNodeCount() + " " + selectedSA.generateOutput(map, map.getRobot()));
        else System.out.println("No search algorithm found");
    }

    private static void automaticTesting(int n, int m, int testCases, String outputName) {
        FileWriter out = null;

        try {
            out = new FileWriter(outputName);

            out.write("\"Search Algorithm\",\"Grid M\",\"Grid N\",\"Tree Node Count\",\"Path Count\"\n");
            for (int i = 0; i < testCases; i++) {
                map = GridGenerator.generateGrid(n, m);
                for (SearchAlgorithm sa : SearchAlgorithm.getSearchAlgorithms()) {
                    ArrayList<Direction> directions = sa.executeSearch(map, map.getRobot());

                    out.write(parseResult(sa, sa.getNodeCount(), directions.size(), map.getWidth(), map.getHeight()) + "\n");
                }
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseResult(SearchAlgorithm searchAlgorithm, int nodeCount, int pathCount, int mGrid, int nGrid) {
        StringBuilder sb = new StringBuilder();
        sb.append(quoteMarks(searchAlgorithm.getInitials()));
        sb.append(quoteMarks(Integer.toString(mGrid)));
        sb.append(quoteMarks(Integer.toString(nGrid)));
        sb.append(quoteMarks(Integer.toString(nodeCount)));
        sb.append(quoteMarks(Integer.toString(pathCount)));
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private static String quoteMarks(String content) {
        return "\"" + content + "\",";
    }
}
