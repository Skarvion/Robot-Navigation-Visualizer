package org.robotnav.SearchAlgorithm;

import javafx.application.Platform;
import org.robotnav.Robot.Direction;
import org.robotnav.Robot.Robot;
import org.robotnav.SearchAlgorithm.Data.ActionNode;
import org.robotnav.SearchAlgorithm.Tree.Node;
import org.robotnav.SearchAlgorithm.informed.AStarSearch;
import org.robotnav.SearchAlgorithm.informed.GreedyBestFirst;
import org.robotnav.SearchAlgorithm.informed.IterativeDeepeningAStarSearch;
import org.robotnav.SearchAlgorithm.uninformed.BidirectionalSearch;
import org.robotnav.SearchAlgorithm.uninformed.BreadthFirstSearch;
import org.robotnav.SearchAlgorithm.uninformed.DepthFirstSearch;
import org.robotnav.Utilities.Utilities;
import org.robotnav.gui.controller.MapController;
import org.robotnav.map.Cell;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An abstract super class for all search algorithm with the common information
 */
public abstract class SearchAlgorithm {

    // List of IDs used by each search algorithm
    private String[] searchAlgorithmName;

    // Number of node in the whole tree, incrementing
    private int nodeCount;

    // Used in conjunction with the GUI controller
    private MapController viewController;
    private long msDelay = 0;

    // Search Algoorithm static list keeps track of all the Search Algorithm implemented
    private static List<SearchAlgorithm> searchAlgorithms;

    /**
     * Abstract method to conduct search by the map and robot starting position
     * @param gridMap map to be traversed
     * @param robot robot in map
     * @return ArrayList of directions taken to get to solution, null if no solution found
     */
    public abstract ArrayList<Direction> executeSearch(Grid gridMap, Robot robot);

    // Add created search algorithm to the static list
    static {
        searchAlgorithms = new ArrayList<>();
        searchAlgorithms.add(new BreadthFirstSearch());
        searchAlgorithms.add(new DepthFirstSearch());
        searchAlgorithms.add(new BidirectionalSearch());
        searchAlgorithms.add(new GreedyBestFirst());
        searchAlgorithms.add(new AStarSearch());
        searchAlgorithms.add(new IterativeDeepeningAStarSearch());
    }

    /**
     * Generate output based on the grid map and robot position
     * @param gridMap map
     * @param robot robot position
     * @return string of resulting path
     */
    public String generateOutput(Grid gridMap, Robot robot) {
        return solutionToString(executeSearch(gridMap, robot));
    }

    /**
     * Used by child classes to derive solution from the goal node to the root node
     * @param goalNode final goal node
     * @return list of direction based on the node in the result path
     */
    protected ArrayList<Direction> deriveSolutionFromNode(Node<ActionNode> goalNode) {
        ArrayList<Direction> result = new ArrayList<>();
        Node<ActionNode> selectedNode = goalNode;

        ArrayList<ActionNode> tempNodeList = new ArrayList<>();

        // Loop while there's parent and take the direction data in it
        while (selectedNode.getParentNode() != null) {
            result.add(((ActionNode) selectedNode.getData()).getMovement());
            tempNodeList.add(selectedNode.getData());
            selectedNode = selectedNode.getParentNode();
        }

        for (int i = tempNodeList.size() - 1; i >= 0; i--) {
            ActionNode temp = tempNodeList.get(i);
            updateCellState(temp.getCoordinate().getX(), temp.getCoordinate().getY(), CellType.Result);
        }

        // Used to reverse result array order
        Utilities.ReverseArray(result);

        return result;
    }

    /**
     * Convert the list of direction to a string
     * @param directionList list of solution / semi-solution direction
     * @return string of direction
     */
    private String solutionToString(ArrayList<Direction> directionList) {
        if (directionList == null) {
            return "No solution found";
        }

        String result = "";

        for (Direction d : directionList) {
            switch (d) {
                case Up:
                    result += "up; ";
                    break;
                case Left:
                    result += "left; ";
                    break;
                case Down:
                    result += "down; ";
                    break;
                case Right:
                    result += "right; ";
                    break;
            }
        }
        result += "\n";

        return result;
    }

    /**
     * List of adjacent direction possible from a state
     * @param gridMap grid map
     * @param x x position
     * @param y y position
     * @param sourceDirection source direction that won't be included to prevent looping through self
     * @return list of visitable directions
     */
    protected ArrayList<Direction> visitableDirection(Grid gridMap, int x, int y, Direction sourceDirection) {
        ArrayList<Direction> result = new ArrayList<>();

        if (sourceDirection != Direction.Down) {
            Cell upCell = gridMap.getCell(x, y - 1);
            if (upCell != null && upCell.getType() != CellType.Wall)
                result.add(Direction.Up);
        }

        if (sourceDirection != Direction.Right) {
            Cell leftCell = gridMap.getCell(x - 1, y);
            if (leftCell != null && leftCell.getType() != CellType.Wall)
                result.add(Direction.Left);
        }

        if (sourceDirection != Direction.Up) {
            Cell downCell = gridMap.getCell(x, y + 1);
            if (downCell != null && downCell.getType() != CellType.Wall)
                result.add(Direction.Down);
        }

        if (sourceDirection != Direction.Left) {
            Cell rightCell = gridMap.getCell(x + 1, y);
            if (rightCell != null && rightCell.getType() != CellType.Wall)
                result.add(Direction.Right);
        }

        return result;
    }

    /**
     * Get list of algorithm ID
     * @return ID list
     */
    public String[] getSearchAlgorithmName() {
        return searchAlgorithmName;
    }

    /**
     * Set this search algorithm ID
     * @param searchAlgorithmName string array of ID
     */
    public void setSearchAlgorithmName(String[] searchAlgorithmName) {
        this.searchAlgorithmName = searchAlgorithmName;
    }

    /**
     * Get search algorithm from list based on selected ID
     * @param name selected ID
     * @param viewController if GUI run, set the selected search algorithm for that controller
     * @return
     */
    public static SearchAlgorithm getSearchAlgorithm(String name, MapController viewController) {
        for (SearchAlgorithm sa : searchAlgorithms) {
            if (sa.isName(name)) {
                sa.setViewController(viewController);
                return sa;
            }
        }
        return null;
    }

    /**
     * Is this search algorithm is the name based on the list of ID
     * @param name search algorithm ID
     * @return true if this algorithm has the ID, else false
     */
    public boolean isName(String name) {
        String upperName = name.toUpperCase();

        for (String saName : searchAlgorithmName) {
            if (upperName.equals(saName.toUpperCase())) return true;
        }

        return false;
    }

    /**
     * Set this search algorithm to the controller
     * @param viewController GUI controller
     */
    public void setViewController(MapController viewController) {
        this.viewController = viewController;
    }

    /**
     * If it's connected to GUI controller, change the colour of cell in grid
     * @param x x position
     * @param y y position
     * @param cellState new cell state
     */
    protected void updateCellState(int x, int y, CellType cellState) {
        if (viewController != null) {
            delaySearch();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    viewController.updateTile(x, y, cellState);
                }
            });
        }
    }

    /**
     * Search algorithm resetting the GUI grid if it's not null
     */
    protected void resetGrid() {
        if (viewController != null)
            viewController.resetGrid();
    }

    /**
     * Delay search action in ms based on the ms delay field
     */
    protected void delaySearch() {
        if (msDelay > 0) {

            try {
                TimeUnit.MILLISECONDS.sleep(msDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Manhattan distance calculation used for heuristic assignment
     * @param x1 x position 1
     * @param y1 y positoin 1
     * @param x2 x position 2
     * @param y2 y position 2
     * @return manhattan distance between two coordinate
     */
    protected int calculateDistance(int x1, int y1, int x2, int y2) {
        int result = (Math.abs(x2 - x1) + Math.abs(y2 - y1));
        return result;
    }

    /**
     * Assign heuristic value to each cell in the grid using calculateDistance method
     * @param gridMap map
     */
    protected void assignHeuristicValue(Grid gridMap) {
        int goalXCoordinate = gridMap.getGoalX();
        int goalYCoordinate = gridMap.getGoalY();

        for (int i = 0; i < gridMap.getWidth(); i++) {
            for (int j = 0; j < gridMap.getHeight(); j++) {
                gridMap.getCell(i, j).setHeuristicValue(calculateDistance(i, j, goalXCoordinate, goalYCoordinate));
            }
        }
    }

    /**
     * Print the grid with heuristic value to terminal, currently unused, used for testing
     * @param gridMap map
     */
    private void printHeuristicGrid(Grid gridMap) {
        for (int i = 0; i < gridMap.getHeight(); i++) {
            for (int j = 0; j < gridMap.getWidth(); j++) {
                switch (gridMap.getCell(j, i).getType()) {
                    case Empty:
                        System.out.print(gridMap.getCell(j, i).getHeuristicValue() + " ");
                        break;

                    case Wall:
                        System.out.print("| ");
                        break;

                    case Robot:
                        System.out.print("R ");
                        break;

                    case Goal:
                        System.out.print("G ");
                        break;
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Get the list of search algorithm
     * @return list of search algorithm
     */
    public static List<SearchAlgorithm> getSearchAlgorithms() {
        return searchAlgorithms;
    }

    /**
     * Get the first name of the search algorithm, the convention is the first ID is the formal name of the algorithm
     * @return first ID of algorithm, also the formal name
     */
    public String getFirstName() {
        if (searchAlgorithmName.length > 0) return searchAlgorithmName[0];
        else return "Nameless Search Algorithm";
    }

    /**
     * Get the second ID of algorithm, the convention is the second ID is the initials
     * @return second ID of algorithm, also the initials
     */
    public String getInitials() {
        if (searchAlgorithmName.length >= 2) return searchAlgorithmName[1];
        else return getFirstName();
    }

    /**
     * Get the number of node count in search tree based on incrementation
     * @return number of node count in tree
     */
    public int getNodeCount() { return nodeCount; }

    /**
     * Set node count in tree
     * @param nodeCount set node count of tree in this algorithm
     */
    protected void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }

    /**
     * Increment node count of tree
     */
    protected void incrementNodeCount() { nodeCount++; }

    /**
     * Return the move cost of changing state, currently return only 1
     * @param d direction of movement, unused
     * @param node source node of movement, unused
     * @return currently 1
     */
    public int moveCost(Direction d, Node<ActionNode> node) {
        return 1;
    }

    /**
     * Set delay search
     * @param delay millisecond delay
     */
    public void setMsDelay(long delay) { msDelay = delay; }

    /**
     * Get delay in ms
     * @return millisecond delay
     */
    public long getMsDelay() { return msDelay; }
}
