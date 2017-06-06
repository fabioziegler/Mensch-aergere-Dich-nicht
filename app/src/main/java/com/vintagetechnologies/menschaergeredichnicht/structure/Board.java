package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public final class Board {

    /**
     * Spots of the spots is stored here.
     */
    private Spot[] spots = new Spot[72];

    /**
     * Board object.
     */
    private static Board boardItself;

    /**
     * returns boardItself
     *
     * @return
     */
    public static Board get() {
        if (boardItself == null) {
            boardItself = new Board();
        }

        return boardItself;
    }

    public static void resetBoard() {
        boardItself = new Board();
    }


    /**
     * Constructor of Board
     * private: Is a Singleton
     * <p>
     * Sets all Spots to the correct position and links
     */
    private Board() {

        spots[0] = new RegularSpot(4, 0, null);


        spots[5] = new RegularSpot(4, 1, (RegularSpot) spots[0]);
        spots[6] = new RegularSpot(4, 2, (RegularSpot) spots[5]);
        spots[7] = new RegularSpot(4, 3, (RegularSpot) spots[6]);
        spots[8] = new RegularSpot(4, 4, (RegularSpot) spots[7]);
        spots[9] = new RegularSpot(3, 4, (RegularSpot) spots[8]);
        spots[10] = new RegularSpot(2, 4, (RegularSpot) spots[9]);
        spots[11] = new RegularSpot(1, 4, (RegularSpot) spots[10]);
        spots[12] = new RegularSpot(0, 4, (RegularSpot) spots[11]);
        spots[13] = new RegularSpot(0, 5, (RegularSpot) spots[12]);
        spots[14] = new RegularSpot(0, 6, (RegularSpot) spots[13]);
        spots[15] = new RegularSpot(1, 6, (RegularSpot) spots[14]);
        spots[16] = new RegularSpot(2, 6, (RegularSpot) spots[15]);
        spots[17] = new RegularSpot(3, 6, (RegularSpot) spots[16]);
        spots[18] = new RegularSpot(4, 6, (RegularSpot) spots[17]);
        spots[19] = new RegularSpot(4, 7, (RegularSpot) spots[18]);
        spots[20] = new RegularSpot(4, 8, (RegularSpot) spots[19]);
        spots[21] = new RegularSpot(4, 9, (RegularSpot) spots[20]);
        spots[22] = new RegularSpot(4, 10, (RegularSpot) spots[21]);
        spots[23] = new RegularSpot(5, 10, (RegularSpot) spots[22]);
        spots[24] = new RegularSpot(6, 10, (RegularSpot) spots[23]);
        spots[25] = new RegularSpot(6, 9, (RegularSpot) spots[24]);
        spots[26] = new RegularSpot(6, 8, (RegularSpot) spots[25]);
        spots[27] = new RegularSpot(6, 7, (RegularSpot) spots[26]);
        spots[28] = new RegularSpot(6, 6, (RegularSpot) spots[27]);
        spots[29] = new RegularSpot(7, 6, (RegularSpot) spots[28]);
        spots[30] = new RegularSpot(8, 6, (RegularSpot) spots[29]);
        spots[31] = new RegularSpot(9, 6, (RegularSpot) spots[30]);
        spots[32] = new RegularSpot(10, 6, (RegularSpot) spots[31]);
        spots[33] = new RegularSpot(10, 5, (RegularSpot) spots[32]);
        spots[34] = new RegularSpot(10, 4, (RegularSpot) spots[33]);
        spots[35] = new RegularSpot(9, 4, (RegularSpot) spots[34]);
        spots[36] = new RegularSpot(8, 4, (RegularSpot) spots[35]);
        spots[37] = new RegularSpot(7, 4, (RegularSpot) spots[36]);
        spots[38] = new RegularSpot(6, 4, (RegularSpot) spots[37]);
        spots[39] = new RegularSpot(6, 3, (RegularSpot) spots[38]);
        spots[40] = new RegularSpot(6, 2, (RegularSpot) spots[39]);
        spots[41] = new RegularSpot(6, 1, (RegularSpot) spots[40]);
        spots[42] = new RegularSpot(6, 0, (RegularSpot) spots[41]);
        spots[43] = new RegularSpot(5, 0, (RegularSpot) spots[42]);

        ((RegularSpot) (spots[0])).setNextSpot((RegularSpot) spots[43]);

        spots[1] = new StartingSpot(0, 0, PlayerColor.RED, spots[12]);
        spots[2] = new StartingSpot(1, 0, PlayerColor.RED, spots[12]);
        spots[3] = new StartingSpot(0, 1, PlayerColor.RED, spots[12]);
        spots[4] = new StartingSpot(1, 1, PlayerColor.RED, spots[12]);

        spots[44] = new StartingSpot(10, 0, PlayerColor.BLUE, spots[42]);
        spots[45] = new StartingSpot(10, 1, PlayerColor.BLUE, spots[42]);
        spots[46] = new StartingSpot(9, 0, PlayerColor.BLUE, spots[42]);
        spots[47] = new StartingSpot(9, 1, PlayerColor.BLUE, spots[42]);

        spots[48] = new StartingSpot(0, 10, PlayerColor.YELLOW, spots[22]);
        spots[49] = new StartingSpot(0, 9, PlayerColor.YELLOW, spots[22]);
        spots[50] = new StartingSpot(1, 10, PlayerColor.YELLOW, spots[22]);
        spots[51] = new StartingSpot(1, 9, PlayerColor.YELLOW, spots[22]);

        spots[52] = new StartingSpot(10, 10, PlayerColor.GREEN, spots[32]);
        spots[53] = new StartingSpot(9, 10, PlayerColor.GREEN, spots[32]);
        spots[54] = new StartingSpot(10, 9, PlayerColor.GREEN, spots[32]);
        spots[55] = new StartingSpot(9, 9, PlayerColor.GREEN, spots[32]);

        spots[56] = new EndSpot(4, 5, PlayerColor.RED, null);
        spots[57] = new EndSpot(3, 5, PlayerColor.RED, (EndSpot) spots[56]);
        spots[58] = new EndSpot(2, 5, PlayerColor.RED, (EndSpot) spots[57]);
        spots[59] = new EndSpot(1, 5, PlayerColor.RED, (EndSpot) spots[58]);

        spots[60] = new EndSpot(5, 4, PlayerColor.BLUE, null);
        spots[61] = new EndSpot(5, 3, PlayerColor.BLUE, (EndSpot) spots[60]);
        spots[62] = new EndSpot(5, 2, PlayerColor.BLUE, (EndSpot) spots[61]);
        spots[63] = new EndSpot(5, 1, PlayerColor.BLUE, (EndSpot) spots[62]);

        spots[64] = new EndSpot(6, 5, PlayerColor.GREEN, null);
        spots[65] = new EndSpot(7, 5, PlayerColor.GREEN, (EndSpot) spots[64]);
        spots[66] = new EndSpot(8, 5, PlayerColor.GREEN, (EndSpot) spots[65]);
        spots[67] = new EndSpot(9, 5, PlayerColor.GREEN, (EndSpot) spots[66]);

        spots[68] = new EndSpot(5, 6, PlayerColor.YELLOW, null);
        spots[69] = new EndSpot(5, 7, PlayerColor.YELLOW, (EndSpot) spots[68]);
        spots[70] = new EndSpot(5, 8, PlayerColor.YELLOW, (EndSpot) spots[69]);
        spots[71] = new EndSpot(5, 9, PlayerColor.YELLOW, (EndSpot) spots[70]);


        ((RegularSpot) spots[43]).setEndSpot((EndSpot) spots[63]); //Blue
        ((RegularSpot) spots[23]).setEndSpot((EndSpot) spots[71]); //Yellow
        ((RegularSpot) spots[13]).setEndSpot((EndSpot) spots[59]); //Red
        ((RegularSpot) spots[33]).setEndSpot((EndSpot) spots[67]); //Green

    }


    /**
     * Returns the Spot Array
     *
     * @return
     */
    public static Spot[] getBoard() {
        return boardItself.spots;
    }


    /**
     * Returns a single Spot with given index.
     *
     * @param p
     * @return
     */
    public static Spot getBoard(int p) {
        return boardItself.spots[p];
    }


    /**
     * Needs:   -DiceNumber
     * -GamePiece
     * <p>
     * Checks if GamePiece can move a given number of Spots. The number is given as a DiceNumber.
     * <p>
     * Returns the Spot, on which the GamePiece will be.
     * Returns null if the GamePiece can't move the given number of Spots (occupied, end)
     *
     * @param dn
     * @param piece
     * @return
     */
    public static Spot checkSpot(DiceNumber dn, GamePiece piece) {
        int steps = dn.getNumber();
        Spot targetSpot = piece.getSpot();
        for (int i = 0; i < steps; i++) {
            if (targetSpot instanceof RegularSpot) {
                targetSpot = checkRegularSpot(targetSpot, piece);
            } else if (targetSpot instanceof EndSpot) {
                targetSpot = checkEndSpot(targetSpot, piece);
            } else if (targetSpot instanceof StartingSpot) {
                targetSpot = ((StartingSpot) targetSpot).getEntrance();
            }
            if (targetSpot == null) {// Fehler
                return null;
            }
        }


        if (targetSpot != null && targetSpot.getGamePiece() != null && targetSpot.getGamePiece().getPlayerColor() == piece.getPlayerColor()) {
            return null;
        }

        return targetSpot;
    }

    private static Spot checkEndSpot(Spot targetSpot, GamePiece piece) {
        EndSpot es = (EndSpot) targetSpot;
        if (es.getNextEndSpot() != null && es.getNextEndSpot().getGamePiece() == null) {
            return es.getNextEndSpot(); //nächster Spot ist nächster freier EndSpot
        }
        return null;
    }

    private static Spot checkRegularSpot(Spot targetSpot, GamePiece piece) {
        RegularSpot tr = (RegularSpot) targetSpot;
        if (tr.getEndSpot() == null || (tr.getEndSpot() != null && tr.getEndSpot().getColor() != piece.getPlayerColor())) {
            return tr.getNextSpot(); //nächster Spot ist ein RegularSpot
        } else if (((RegularSpot) targetSpot).getEndSpot().getColor() == piece.getPlayerColor()) {
            return ((RegularSpot) targetSpot).getEndSpot(); //nächster Spot ist erster EndSpot
        }
        return null;
    }


    /**
     * Returns a starting spot with a given color.
     * Needed for returning a GamePiece to a StartingSpot.
     *
     * @param color
     * @return
     */
    public static Spot getStartingSpot(PlayerColor color) {
        for (Spot s : getBoard()) {
            if (s instanceof StartingSpot && ((StartingSpot) s).getColor() == color && s.getGamePiece() == null) {
                return s;
            }
        }
        return null;
    }

    public static Spot getEntrance(PlayerColor c) {
        for (Spot s : getBoard()) {
            if (s instanceof StartingSpot && ((StartingSpot) s).getColor() == c) {
                return ((StartingSpot) s).getEntrance();
            }
        }
        return null;
    }
}
