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

public abstract class SearchAlgorithm {

    private String[] searchAlgorithmName;

    public abstract ArrayList<Direction> executeSearch(Grid gridMap, Robot robot);

    private int nodeCount;

    private MapController viewController;
    private long msDelay = 5;

    private static List<SearchAlgorithm> searchAlgorithms;

    static {
        searchAlgorithms = new ArrayList<>();
        searchAlgorithms.add(new BreadthFirstSearch());
        searchAlgorithms.add(new DepthFirstSearch());
        searchAlgorithms.add(new BidirectionalSearch());
        searchAlgorithms.add(new GreedyBestFirst());
        searchAlgorithms.add(new AStarSearch());
        searchAlgorithms.add(new IterativeDeepeningAStarSearch());
    }

    public String generateOutput(Grid gridMap, Robot robot) {
        return solutionToString(executeSearch(gridMap, robot));
    }

    protected ArrayList<Direction> deriveSolutionFromNode(Node<ActionNode> goalNode) {
        ArrayList<Direction> result = new ArrayList<>();
        Node<ActionNode> selectedNode = goalNode;

        ArrayList<ActionNode> tempNodeList = new ArrayList<>();

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

    public String[] getSearchAlgorithmName() {
        return searchAlgorithmName;
    }

    public void setSearchAlgorithmName(String[] searchAlgorithmName) {
        this.searchAlgorithmName = searchAlgorithmName;
    }

    public static SearchAlgorithm getSearchAlgorithm(String name, MapController viewController) {
        for (SearchAlgorithm sa : searchAlgorithms) {
            if (sa.isName(name)) {
                sa.setViewController(viewController);
                return sa;
            }
        }
        return null;
    }

    public boolean isName(String name) {
        String upperName = name.toUpperCase();

        for (String saName : searchAlgorithmName) {
            if (upperName.equals(saName.toUpperCase())) return true;
        }

        return false;
    }

    public void setViewController(MapController viewController) {
        this.viewController = viewController;
    }

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

    protected void resetGrid() {
        if (viewController != null)
            viewController.resetGrid();
    }

    protected void delaySearch() {
        if (msDelay > 0) {

            try {
                TimeUnit.MILLISECONDS.sleep(msDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected int calculateDistance(int x1, int y1, int x2, int y2) {
        int result = (Math.abs(x2 - x1) + Math.abs(y2 - y1));
        return result;
    }

    protected void assignHeuristicValue(Grid gridMap) {
        int goalXCoordinate = gridMap.getGoalX();
        int goalYCoordinate = gridMap.getGoalY();

        for (int i = 0; i < gridMap.getWidth(); i++) {
            for (int j = 0; j < gridMap.getHeight(); j++) {
                gridMap.getCell(i, j).setHeuristicValue(calculateDistance(i, j, goalXCoordinate, goalYCoordinate));
            }
        }
    }

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

    public static List<SearchAlgorithm> getSearchAlgorithms() {
        return searchAlgorithms;
    }

    public String getFirstName() {
        if (searchAlgorithmName.length > 0) return searchAlgorithmName[0];
        else return "Nameless Search Algorithm";
    }

    public String getInitials() {
        if (searchAlgorithmName.length >= 2) return searchAlgorithmName[1];
        else return getFirstName();
    }

    public int getNodeCount() { return nodeCount; }

    protected void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }

    protected void incrementNodeCount() { nodeCount++; }

    //TODO: consider whether we're doing the additional cost
    public int moveCost(Direction d, Node<ActionNode> node) {
        return 1;
    }

    public void setMsDelay(long delay) { msDelay = delay; }
    public long getMsDelay() { return msDelay; }
}
