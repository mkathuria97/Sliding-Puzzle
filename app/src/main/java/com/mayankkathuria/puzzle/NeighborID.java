package com.mayankkathuria.puzzle;

import java.util.ArrayList;

public class NeighborID {

    //stores the row of the empty tile
    private int emptyTileRow;

    //stores the column of the empty tile
    private int emptyTileColumn;

    public NeighborID(int row, int column) {
        emptyTileRow = row;
        emptyTileColumn = column;
    }

    /**
     *
     * @return the Id of the tile on the top of the empty tile
     */
    public int getTopTileId(){
        return Integer.parseInt((emptyTileRow - 1) + "" + (emptyTileColumn));
    }

    /**
     *
     * @return the Id of the tile on the left of the empty tile
     */
    public int getLeftTileId(){
        if (emptyTileColumn >= 1) {
            return Integer.parseInt((emptyTileRow) + "" + (emptyTileColumn - 1));
        }
        return -1;
    }

    /**
     *
     * @return the Id of the tile on the right of the empty tile
     */
    public int getRightTileId(){
        return Integer.parseInt((emptyTileRow) + "" + (emptyTileColumn + 1));
    }

    /**
     *
     * @return the Id of the tile on the bottom of the empty tile
     */
    public int getBottomTileId(){
        return Integer.parseInt((emptyTileRow + 1) + "" + (emptyTileColumn));
    }

    /**
     *
     * @return the Ids of all the neighboring tiles
     */
    public ArrayList<Integer> getNeighboringTilesIds(){
        ArrayList<Integer> neighboringTileIds = new ArrayList<>();
        neighboringTileIds.add(getTopTileId());
        neighboringTileIds.add(getLeftTileId());
        neighboringTileIds.add(getRightTileId());
        neighboringTileIds.add(getBottomTileId());
        return neighboringTileIds;
    }
}

