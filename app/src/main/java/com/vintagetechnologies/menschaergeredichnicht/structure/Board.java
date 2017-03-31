package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class Board {
    private Spot[] board = new Spot[72];

    public Board() {

        board[0] = new RegularSpot(4, 0, null);
        board[1] = new StartingSpot(0, 0, PlayerColor.RED, board[0]);
        board[2] = new StartingSpot(0, 1, PlayerColor.RED, board[0]);
        board[3] = new StartingSpot(1, 0, PlayerColor.RED, board[0]);
        board[4] = new StartingSpot(1, 1, PlayerColor.RED, board[0]);

        board[5] = new RegularSpot(4, 1, board[0]);
        board[6] = new RegularSpot(4, 2, board[5]);
        board[7] = new RegularSpot(4, 3, board[6]);
        board[8] = new RegularSpot(4, 4, board[7]);
        board[9] = new RegularSpot(3, 4, board[8]);
        board[10] = new RegularSpot(2, 4, board[9]);
        board[11] = new RegularSpot(1, 4, board[10]);
        board[12] = new RegularSpot(0, 4, board[11]);
        board[13] = new RegularSpot(0, 5, board[12]);
        board[14] = new RegularSpot(0, 6, board[13]);
        board[15] = new RegularSpot(1, 6, board[14]);
        board[16] = new RegularSpot(2, 6, board[15]);
        board[17] = new RegularSpot(3, 6, board[16]);
        board[18] = new RegularSpot(4, 6, board[17]);
        board[19] = new RegularSpot(4, 7, board[18]);
        board[20] = new RegularSpot(4, 8, board[19]);
        board[21] = new RegularSpot(4, 9, board[20]);
        board[22] = new RegularSpot(4, 10, board[21]);
        board[23] = new RegularSpot(5, 10, board[22]);
        board[24] = new RegularSpot(6, 10, board[23]);
        board[25] = new RegularSpot(6, 9, board[24]);
        board[26] = new RegularSpot(6, 8, board[25]);
        board[27] = new RegularSpot(6, 7, board[26]);
        board[28] = new RegularSpot(6, 6, board[27]);
        board[29] = new RegularSpot(7, 6, board[28]);
        board[30] = new RegularSpot(8, 6, board[29]);
        board[31] = new RegularSpot(9, 6, board[30]);
        board[32] = new RegularSpot(10, 6, board[31]);
        board[33] = new RegularSpot(10, 5, board[32]);
        board[34] = new RegularSpot(10, 4, board[33]);
        board[35] = new RegularSpot(9, 4, board[34]);
        board[36] = new RegularSpot(8, 4, board[35]);
        board[37] = new RegularSpot(7, 4, board[36]);
        board[38] = new RegularSpot(6, 4, board[37]);
        board[39] = new RegularSpot(6, 3, board[38]);
        board[40] = new RegularSpot(6, 2, board[39]);
        board[41] = new RegularSpot(6, 1, board[40]);
        board[42] = new RegularSpot(6, 0, board[41]);
        board[43] = new RegularSpot(5, 0, board[42]);

        ((RegularSpot)(board[0])).setNextSpot(board[43]);

        board[44] = new StartingSpot(9, 0, PlayerColor.BLUE, board[41]);
        board[45] = new StartingSpot(10, 0, PlayerColor.BLUE, board[41]);
        board[46] = new StartingSpot(9, 1, PlayerColor.BLUE, board[41]);
        board[47] = new StartingSpot(10, 1, PlayerColor.BLUE, board[41]);

        board[48] = new StartingSpot(0, 9, PlayerColor.YELLOW, board[22]);
        board[49] = new StartingSpot(0, 10, PlayerColor.YELLOW, board[22]);
        board[50] = new StartingSpot(1, 9, PlayerColor.YELLOW, board[22]);
        board[51] = new StartingSpot(1, 10, PlayerColor.YELLOW, board[22]);

        board[52] = new StartingSpot(9, 9, PlayerColor.GREEN, board[31]);
        board[53] = new StartingSpot(10, 10, PlayerColor.GREEN, board[31]);
        board[54] = new StartingSpot(10, 9, PlayerColor.GREEN, board[31]);
        board[55] = new StartingSpot(9, 10, PlayerColor.GREEN, board[31]);

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


        ((RegularSpot)board[43]).setEndSpot(board[63]); //Blue
        ((RegularSpot)board[23]).setEndSpot(board[71]); //Yellow
        ((RegularSpot)board[13]).setEndSpot(board[59]); //Red
        ((RegularSpot)board[33]).setEndSpot(board[67]); //Green

    }

    public Spot[] getBoard() {
        return board;
    }

    public Spot getBoard(int p){
        return board[p];
    }
}
