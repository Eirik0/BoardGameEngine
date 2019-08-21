package bge.game.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SudokuBoxTest implements SudokuConstants {
    private static SudokuBox setBoxes(SudokuCell cell1, SudokuCell cell2) {
        SudokuCell[] cells = new SudokuCell[] {
                cell1, cell2, new SudokuCell(), new SudokuCell(), new SudokuCell(), new SudokuCell(), new SudokuCell(), new SudokuCell(), new SudokuCell()
        };
        SudokuBox.initBox(cells, BOX);
        SudokuBox.initBox(cells, ROW);
        SudokuBox.initBox(cells, COLUMN);
        assertEquals(9, cell1.boxes[0].numUndetermined);
        assertEquals(0, cell1.boxes[0].numDetermined);
        assertEquals(9, cell1.boxes[1].numUndetermined);
        assertEquals(0, cell1.boxes[1].numDetermined);
        assertEquals(9, cell1.boxes[2].numUndetermined);
        assertEquals(0, cell1.boxes[2].numDetermined);
        return cell1.boxes[0];
    }

    @Test
    public void testSetDigit() {
        SudokuCell cell1 = new SudokuCell();
        SudokuCell cell2 = new SudokuCell();
        SudokuBox exampleBox = setBoxes(cell1, cell2);
        assertEquals(9, cell2.getPossibleDigits().length);
        cell1.setDigit(DIGIT_1);
        assertEquals(8, cell2.getPossibleDigits().length);
        assertEquals(8, exampleBox.numUndetermined);
        assertEquals(1, exampleBox.numDetermined);
    }

    @Test
    public void testUnsetDigit() {
        SudokuCell cell1 = new SudokuCell();
        SudokuCell cell2 = new SudokuCell();
        SudokuBox exampleBox = setBoxes(cell1, cell2);
        assertEquals(9, cell2.getPossibleDigits().length);
        cell1.setDigit(DIGIT_1);
        cell1.unsetDigit();
        assertEquals(9, cell2.getPossibleDigits().length);
        assertEquals(9, exampleBox.numUndetermined);
        assertEquals(0, exampleBox.numDetermined);
    }
}
