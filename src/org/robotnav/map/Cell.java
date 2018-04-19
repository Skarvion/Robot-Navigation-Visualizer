package org.robotnav.map;

public class Cell {
    private CellType type;
    private int heuristicValue;

    public Cell() {
        this.type = CellType.Empty;
        this.heuristicValue = 0;
    }

    public Cell(CellType type) {
        this.type = type;
        this.heuristicValue = 0;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }
}
