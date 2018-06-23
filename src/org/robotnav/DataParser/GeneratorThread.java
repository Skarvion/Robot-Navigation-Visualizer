package org.robotnav.DataParser;

import org.robotnav.Robot.Direction;
import org.robotnav.SearchAlgorithm.SearchAlgorithm;
import org.robotnav.map.Grid;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Generator thread is used to speed up the grid generation method by utilizing multi-threading support
 * of modern quad core CPU
 */
public class GeneratorThread implements Runnable {
    private int id;
    private int n;
    private int m;
    private int testCase;
    private String outputName;
    private Thread thread;

    /**
     * Constructor of each thread
     * @param id thread id
     * @param n grid width
     * @param m grid height
     * @param testCase number of test case
     * @param outputName output name
     */
    public GeneratorThread(int id, int n, int m, int testCase, String outputName) {
        this.id = id;
        this.n = n;
        this.m = m;
        this.testCase = testCase;
        this.outputName = outputName;
    }

    /**
     * Override run method from Runnable, runs automatic testing function
     */
    @Override
    public void run() {
        System.out.println("Running generator thread " + id);
        char outputId = (char) ('a' + id);
        StringBuilder sb = new StringBuilder(outputName);
        sb.insert(sb.indexOf("."), outputId);
        automaticTesting(n, m, testCase, sb.toString());
        System.out.println("Automatic testing completed: " + sb.toString());
    }

    /**
     * Start this object and run its thread
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this, "Generator Thread " + id);
            thread.start();
        }
    }

    /**
     * Conduct automatic test case generation with specified width and height and save it to csv file
     * @param n map width
     * @param m map height
     * @param testCases number of test maps
     * @param outputName output csv file name
     */
    private void automaticTesting(int n, int m, int testCases, String outputName) {
        FileWriter out = null;

        try {
            out = new FileWriter(outputName);

            out.write("\"Search Algorithm\",\"Grid M\",\"Grid N\",\"Tree Node Count\",\"Path Count\"\n");
            for (int i = 0; i < testCases; i++) {
                Grid map = GridGenerator.generateGrid(n, m);
                for (SearchAlgorithm sa : SearchAlgorithm.getSearchAlgorithms()) {
                    ArrayList<Direction> directions = sa.executeSearch(map, map.getRobot());

                    int pathCount = directions != null ? directions.size() : 0;
                    out.write(parseResult(sa, sa.getNodeCount(), pathCount, map.getWidth(), map.getHeight()) + "\n");
                }
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used in conjunction with automaticTesting function to print each line of result
     * @param searchAlgorithm search algorithm used
     * @param nodeCount number of node in tree
     * @param pathCount number of node in path
     * @param mGrid map width
     * @param nGrid map height
     * @return one line for csv output
     */
    private String parseResult(SearchAlgorithm searchAlgorithm, int nodeCount, int pathCount, int mGrid, int nGrid) {
        StringBuilder sb = new StringBuilder();
        sb.append(quoteMarks(searchAlgorithm.getInitials()));
        sb.append(quoteMarks(Integer.toString(mGrid)));
        sb.append(quoteMarks(Integer.toString(nGrid)));
        sb.append(quoteMarks(Integer.toString(nodeCount)));
        sb.append(quoteMarks(Integer.toString(pathCount)));
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Add quote mark around string and comma
     * @param content content string
     * @return quoted string
     */
    private String quoteMarks(String content) {
        return "\"" + content + "\",";
    }
}
