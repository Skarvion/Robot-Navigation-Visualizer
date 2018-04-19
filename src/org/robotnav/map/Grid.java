package org.robotnav.map;

import org.robotnav.Robot.Robot;

import java.util.ArrayList;

public class Grid {
    private ArrayList<ArrayList<Cell>> gridCell;

    private int width;
    private int height;
    private Robot robot;

    private int goalX;
    private int goalY;

    /**
     * Create new gridmap filled with empty cells
     *
     * @param n the width of the grid
     * @param m the height of the grid
     */
    public Grid(int n, int m) {
        gridCell = new ArrayList<>();
        width = n;
        height = m ;
        goalX = -1;
        goalY = -1;

        for (int i = 0; i < n; i++) {
            gridCell.add(new ArrayList<>());
            for (int j = 0; j < m; j++) {
                gridCell.get(i).add(new Cell());
            }
        }
    }

    public void printMapTerminal() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (gridCell.get(j).get(i).getType()) {
                    case Empty:
                        System.out.print(". ");
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

    public Robot getRobot() { return robot; }

    public void setRobot(int x, int y) {
        robot = new Robot(this, x, y);
        gridCell.get(x).get(y).setType(CellType.Robot);
    }

    public void setGoal(int x, int y) {
        goalX = x;
        goalY = y;
        gridCell.get(x).get(y).setType(CellType.Goal);
    }

    public int getGoalX() { return goalX; }

    public int getGoalY() { return goalY; }

    public void setWall(int x, int y, int width, int height) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                gridCell.get(i).get(j).setType(CellType.Wall);
            }
        }
    }

    public Cell getCell (int n, int m) {
        try {
            return gridCell.get(n).get(m);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
