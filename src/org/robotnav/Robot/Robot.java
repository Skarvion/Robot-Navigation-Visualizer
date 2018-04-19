package org.robotnav.Robot;

import org.robotnav.map.Cell;
import org.robotnav.map.CellType;
import org.robotnav.map.Grid;

public class Robot {

    Grid map;
    int xCoordinate;
    int yCoordinate;

    public Robot(Grid map, int xCoordinate, int yCoordinate) {
        this.map = map;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;

        try {
            map.getCell(xCoordinate, yCoordinate);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Player's coordinate is out of bound");
            ex.printStackTrace();
        }
    }

    public boolean checkMove(Direction moveDirection) {
        Cell selectedCell = new Cell();

        try {
            switch (moveDirection) {
                case Up:
                    selectedCell = map.getCell(xCoordinate, yCoordinate - 1);
                    break;
                case Left:
                    selectedCell = map.getCell(xCoordinate - 1, yCoordinate);
                    break;
                case Down:
                    selectedCell = map.getCell(xCoordinate, yCoordinate + 1);
                    break;
                case Right:
                    selectedCell = map.getCell(xCoordinate + 1, yCoordinate);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return false;
        }

        if (selectedCell.getType() != CellType.Empty) {
            return true;
        }

        return false;

    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
