package bge.game.sudoku;

import bge.game.IPosition;
import bge.game.MoveList;

public class SudokuPosition implements IPosition<SudokuMove>, SudokuConstants {
    final SudokuCell[] cells;
    final int[] undecidedCells;
    int numUndecided;
    final int[] decidedCells;
    int numDecided;
    final int[] undecidedCellIndexes;

    public SudokuPosition() {
        this(SudokuConstants.newCells(), SudokuConstants.newUndecidedCells(), TOTAL_CELLS, new int[TOTAL_CELLS], 0, SudokuConstants.newUndecidedCells());
    }

    public SudokuPosition(SudokuCell[] cells, int[] undecidedCells, int numUndecided, int[] decidedCells, int numDecided, int[] undecidedCellIndexes) {
        this.cells = cells;
        this.undecidedCells = undecidedCells;
        this.numUndecided = numUndecided;
        this.decidedCells = decidedCells;
        this.numDecided = numDecided;
        this.undecidedCellIndexes = undecidedCellIndexes;
        SudokuBox.initBox(new SudokuCell[] { cells[0], cells[1], cells[2], cells[9], cells[10], cells[11], cells[18], cells[19], cells[20] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[3], cells[4], cells[5], cells[12], cells[13], cells[14], cells[21], cells[22], cells[23] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[6], cells[7], cells[8], cells[15], cells[16], cells[17], cells[24], cells[25], cells[26] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[27], cells[28], cells[29], cells[36], cells[37], cells[38], cells[45], cells[46], cells[47] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[30], cells[31], cells[32], cells[39], cells[40], cells[41], cells[48], cells[49], cells[50] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[33], cells[34], cells[35], cells[42], cells[43], cells[44], cells[51], cells[52], cells[53] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[54], cells[55], cells[56], cells[63], cells[64], cells[65], cells[72], cells[73], cells[74] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[57], cells[58], cells[59], cells[66], cells[67], cells[68], cells[75], cells[76], cells[77] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[60], cells[61], cells[62], cells[69], cells[70], cells[71], cells[78], cells[79], cells[80] }, BOX);
        SudokuBox.initBox(new SudokuCell[] { cells[0], cells[1], cells[2], cells[3], cells[4], cells[5], cells[6], cells[7], cells[8] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[9], cells[10], cells[11], cells[12], cells[13], cells[14], cells[15], cells[16], cells[17] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[18], cells[19], cells[20], cells[21], cells[22], cells[23], cells[24], cells[25], cells[26] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[27], cells[28], cells[29], cells[30], cells[31], cells[32], cells[33], cells[34], cells[35] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[36], cells[37], cells[38], cells[39], cells[40], cells[41], cells[42], cells[43], cells[44] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[45], cells[46], cells[47], cells[48], cells[49], cells[50], cells[51], cells[52], cells[53] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[54], cells[55], cells[56], cells[57], cells[58], cells[59], cells[60], cells[61], cells[62] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[63], cells[64], cells[65], cells[66], cells[67], cells[68], cells[69], cells[70], cells[71] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[72], cells[73], cells[74], cells[75], cells[76], cells[77], cells[78], cells[79], cells[80] }, ROW);
        SudokuBox.initBox(new SudokuCell[] { cells[0], cells[9], cells[18], cells[27], cells[36], cells[45], cells[54], cells[63], cells[72] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[1], cells[10], cells[19], cells[28], cells[37], cells[46], cells[55], cells[64], cells[73] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[2], cells[11], cells[20], cells[29], cells[38], cells[47], cells[56], cells[65], cells[74] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[3], cells[12], cells[21], cells[30], cells[39], cells[48], cells[57], cells[66], cells[75] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[4], cells[13], cells[22], cells[31], cells[40], cells[49], cells[58], cells[67], cells[76] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[5], cells[14], cells[23], cells[32], cells[41], cells[50], cells[59], cells[68], cells[77] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[6], cells[15], cells[24], cells[33], cells[42], cells[51], cells[60], cells[69], cells[78] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[7], cells[16], cells[25], cells[34], cells[43], cells[52], cells[61], cells[70], cells[79] }, COLUMN);
        SudokuBox.initBox(new SudokuCell[] { cells[8], cells[17], cells[26], cells[35], cells[44], cells[53], cells[62], cells[71], cells[80] }, COLUMN);
    }

    @Override
    public void getPossibleMoves(MoveList<SudokuMove> possibleMoves) {
        int i = 0;
        while (i < numUndecided) {
            int cellIndex = undecidedCells[i++];
            int[] possibleDigits = cells[cellIndex].getPossibleDigits();
            int j = 0;
            if (possibleDigits.length == 1) {
                possibleMoves.clear();
                possibleMoves.addDynamicMove(SudokuMove.valueOf(cellIndex, possibleDigits[0]), this);
                return;
            }
            while (j < possibleDigits.length) {
                possibleMoves.addQuietMove(SudokuMove.valueOf(cellIndex, possibleDigits[j++]), this);
            }
        }
    }

    @Override
    public int getCurrentPlayer() {
        return 1;
    }

    @Override
    public void makeMove(SudokuMove move) {
        // Set value
        cells[move.location].setDigit(move.digit);
        // Replace with last element of undecided
        int undecidedIndex = undecidedCellIndexes[move.location]; // find index of cell in undecided
        int lastUndecidedBoardIndex = undecidedCells[--numUndecided]; // find index of last undecided on board
        undecidedCells[undecidedIndex] = lastUndecidedBoardIndex; // replace
        undecidedCellIndexes[lastUndecidedBoardIndex] = undecidedIndex; // update index
        // Add to decided
        decidedCells[numDecided++] = move.location; // add as last element
    }

    @Override
    public void unmakeMove(SudokuMove move) {
        // Assume we are unmaking the moves in order
        int decidedIndex = decidedCells[--numDecided]; // find the index of the last decided cell
        undecidedCells[numUndecided] = decidedIndex;
        undecidedCellIndexes[decidedIndex] = numUndecided++;
        cells[decidedIndex].unsetDigit();
    }

    @Override
    public SudokuPosition createCopy() {
        SudokuCell[] cellsCopy = new SudokuCell[TOTAL_CELLS];
        int[] undecidedCellsCopy = new int[TOTAL_CELLS];
        int[] decidedCellsCopy = new int[TOTAL_CELLS];
        int[] undecidedIndexesCopy = new int[TOTAL_CELLS];
        int i = 0;
        do {
            cellsCopy[i] = cells[i].createCopy();
        } while (++i < TOTAL_CELLS);
        System.arraycopy(undecidedCells, 0, undecidedCellsCopy, 0, numUndecided);
        System.arraycopy(decidedCells, 0, decidedCellsCopy, 0, numDecided);
        System.arraycopy(undecidedCellIndexes, 0, undecidedIndexesCopy, 0, TOTAL_CELLS);
        return new SudokuPosition(cellsCopy, undecidedCellsCopy, numUndecided, decidedCellsCopy, numDecided, undecidedIndexesCopy);
    }
}
