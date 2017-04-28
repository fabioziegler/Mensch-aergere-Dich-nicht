package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public final class Board {

    private Spot[] board = new Spot[72];

    private static Board boardItself;

    public static Board get() {
        if (boardItself == null) {
            boardItself = new Board();
        }

        return boardItself;
    }

    private Board() {

        board[0] = new RegularSpot(4, 0, null);


        board[5] = new RegularSpot(4, 1, (RegularSpot) board[0]);
        board[6] = new RegularSpot(4, 2, (RegularSpot) board[5]);
        board[7] = new RegularSpot(4, 3, (RegularSpot) board[6]);
        board[8] = new RegularSpot(4, 4, (RegularSpot) board[7]);
        board[9] = new RegularSpot(3, 4, (RegularSpot) board[8]);
        board[10] = new RegularSpot(2, 4, (RegularSpot) board[9]);
        board[11] = new RegularSpot(1, 4, (RegularSpot) board[10]);
        board[12] = new RegularSpot(0, 4, (RegularSpot) board[11]);
        board[13] = new RegularSpot(0, 5, (RegularSpot) board[12]);
        board[14] = new RegularSpot(0, 6, (RegularSpot) board[13]);
        board[15] = new RegularSpot(1, 6, (RegularSpot) board[14]);
        board[16] = new RegularSpot(2, 6, (RegularSpot) board[15]);
        board[17] = new RegularSpot(3, 6, (RegularSpot) board[16]);
        board[18] = new RegularSpot(4, 6, (RegularSpot) board[17]);
        board[19] = new RegularSpot(4, 7, (RegularSpot) board[18]);
        board[20] = new RegularSpot(4, 8, (RegularSpot) board[19]);
        board[21] = new RegularSpot(4, 9, (RegularSpot) board[20]);
        board[22] = new RegularSpot(4, 10, (RegularSpot) board[21]);
        board[23] = new RegularSpot(5, 10, (RegularSpot) board[22]);
        board[24] = new RegularSpot(6, 10, (RegularSpot) board[23]);
        board[25] = new RegularSpot(6, 9, (RegularSpot) board[24]);
        board[26] = new RegularSpot(6, 8, (RegularSpot) board[25]);
        board[27] = new RegularSpot(6, 7, (RegularSpot) board[26]);
        board[28] = new RegularSpot(6, 6, (RegularSpot) board[27]);
        board[29] = new RegularSpot(7, 6, (RegularSpot) board[28]);
        board[30] = new RegularSpot(8, 6, (RegularSpot) board[29]);
        board[31] = new RegularSpot(9, 6, (RegularSpot) board[30]);
        board[32] = new RegularSpot(10, 6, (RegularSpot) board[31]);
        board[33] = new RegularSpot(10, 5, (RegularSpot) board[32]);
        board[34] = new RegularSpot(10, 4, (RegularSpot) board[33]);
        board[35] = new RegularSpot(9, 4, (RegularSpot) board[34]);
        board[36] = new RegularSpot(8, 4, (RegularSpot) board[35]);
        board[37] = new RegularSpot(7, 4, (RegularSpot) board[36]);
        board[38] = new RegularSpot(6, 4, (RegularSpot) board[37]);
        board[39] = new RegularSpot(6, 3, (RegularSpot) board[38]);
        board[40] = new RegularSpot(6, 2, (RegularSpot) board[39]);
        board[41] = new RegularSpot(6, 1, (RegularSpot) board[40]);
        board[42] = new RegularSpot(6, 0, (RegularSpot) board[41]);
        board[43] = new RegularSpot(5, 0, (RegularSpot) board[42]);

        ((RegularSpot) (board[0])).setNextSpot((RegularSpot) board[43]);

        board[1] = new StartingSpot(0, 0, PlayerColor.RED, board[12]);
        board[2] = new StartingSpot(1, 0, PlayerColor.RED, board[12]);
        board[3] = new StartingSpot(0, 1, PlayerColor.RED, board[12]);
        board[4] = new StartingSpot(1, 1, PlayerColor.RED, board[12]);

        board[44] = new StartingSpot(10, 0, PlayerColor.BLUE, board[42]);
        board[45] = new StartingSpot(10, 1, PlayerColor.BLUE, board[42]);
        board[46] = new StartingSpot(9, 0, PlayerColor.BLUE, board[42]);
        board[47] = new StartingSpot(9, 1, PlayerColor.BLUE, board[42]);

        board[48] = new StartingSpot(0, 10, PlayerColor.YELLOW, board[22]);
        board[49] = new StartingSpot(0, 9, PlayerColor.YELLOW, board[22]);
        board[50] = new StartingSpot(1, 10, PlayerColor.YELLOW, board[22]);
        board[51] = new StartingSpot(1, 9, PlayerColor.YELLOW, board[22]);

        board[52] = new StartingSpot(10, 10, PlayerColor.GREEN, board[32]);
        board[53] = new StartingSpot(9, 10, PlayerColor.GREEN, board[32]);
        board[54] = new StartingSpot(10, 9, PlayerColor.GREEN, board[32]);
        board[55] = new StartingSpot(9, 9, PlayerColor.GREEN, board[32]);

        board[56] = new EndSpot(4, 5, PlayerColor.RED, null);
        board[57] = new EndSpot(3, 5, PlayerColor.RED, (EndSpot) board[56]);
        board[58] = new EndSpot(2, 5, PlayerColor.RED, (EndSpot) board[57]);
        board[59] = new EndSpot(1, 5, PlayerColor.RED, (EndSpot) board[58]);

        board[60] = new EndSpot(5, 4, PlayerColor.BLUE, null);
        board[61] = new EndSpot(5, 3, PlayerColor.BLUE, (EndSpot) board[60]);
        board[62] = new EndSpot(5, 2, PlayerColor.BLUE, (EndSpot) board[61]);
        board[63] = new EndSpot(5, 1, PlayerColor.BLUE, (EndSpot) board[62]);

        board[64] = new EndSpot(6, 5, PlayerColor.GREEN, null);
        board[65] = new EndSpot(7, 5, PlayerColor.GREEN, (EndSpot) board[64]);
        board[66] = new EndSpot(8, 5, PlayerColor.GREEN, (EndSpot) board[65]);
        board[67] = new EndSpot(9, 5, PlayerColor.GREEN, (EndSpot) board[66]);

        board[68] = new EndSpot(5, 6, PlayerColor.YELLOW, null);
        board[69] = new EndSpot(5, 7, PlayerColor.YELLOW, (EndSpot) board[68]);
        board[70] = new EndSpot(5, 8, PlayerColor.YELLOW, (EndSpot) board[69]);
        board[71] = new EndSpot(5, 9, PlayerColor.YELLOW, (EndSpot) board[70]);


        ((RegularSpot) board[43]).setEndSpot((EndSpot) board[63]); //Blue
        ((RegularSpot) board[23]).setEndSpot((EndSpot) board[71]); //Yellow
        ((RegularSpot) board[13]).setEndSpot((EndSpot) board[59]); //Red
        ((RegularSpot) board[33]).setEndSpot((EndSpot) board[67]); //Green

    }

    public static Spot[] getBoard() {
        return boardItself.board;
    }

    public static Spot getBoard(int p) {
        return boardItself.board[p];
    }



    public static Spot checkSpot(DiceNumber dn, GamePiece piece) {
        int steps = dn.getNumber();
        Spot targetSpot = piece.getSpot();
        for (int i = 0; i < steps; i++) {
            if (targetSpot instanceof RegularSpot) {
                if (((RegularSpot) targetSpot).getEndSpot() == null) {
                    targetSpot = ((RegularSpot) targetSpot).getNextSpot(); //nächster Spot ist ein RegularSpot
                } else if (((RegularSpot) targetSpot).getEndSpot().getColor() == piece.getPlayerColor()) {
                    targetSpot = ((RegularSpot) targetSpot).getEndSpot(); //nächster Spot ist erster EndSpot
                }
            } else if (targetSpot instanceof EndSpot) {
                if (((EndSpot) targetSpot).getNextEndSpot().getGamePiece() == null) {
                    targetSpot = ((EndSpot) targetSpot).getNextEndSpot(); //nächster Spot ist nächster freier EndSpot
                } else if (((EndSpot) targetSpot).getNextEndSpot().getGamePiece() != null) {
                    return null; //Fehler: nächster EndSpot ist nicht frei
                }
            }else if (targetSpot instanceof StartingSpot){
                targetSpot = ((StartingSpot) targetSpot).getEntrance();
            }
            if (targetSpot == null) {// Fehler
                return null;
            }
        }




        if (targetSpot != null) {
            if (targetSpot.getGamePiece().getPlayerColor() == piece.getPlayerColor()) {
                return null;
            }
        }

        return targetSpot;
    }

    public static Spot getStartingSpot(PlayerColor color){
        for(Spot s : getBoard()){
            if(s instanceof StartingSpot && ((StartingSpot) s).getColor() == color && s.getGamePiece() == null){
                return s;
            }
        }
        return null;
    }
}
