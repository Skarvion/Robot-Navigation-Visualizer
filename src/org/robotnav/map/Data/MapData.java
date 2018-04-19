package org.robotnav.map.Data;

import org.robotnav.map.CellType;

public class MapData {
    private CellType cellType;
    private int heuristicData;

    public MapData() {
        this.cellType = CellType.Empty;
        this.heuristicData = 0;
    }

    public MapData(CellType cellType, int heuristicData) {
        this.cellType = cellType;
        this.heuristicData = heuristicData;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getHeuristicData() {
        return heuristicData;
    }

    public void setHeuristicData(int heuristicData) {
        this.heuristicData = heuristicData;
    }
}
