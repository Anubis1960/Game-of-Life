import java.awt.*;

public class Board {
    private int rows;
    private int columns;
    private boolean[][] grid;
    public static final int CELL_SIZE = 30;


    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new boolean[rows][columns];
    }

    public int countNeighbors(int row, int column) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < columns) {
                    if (grid[i][j]) {
                        count++;
                    }
                }
            }
        }
        if (grid[row][column]) {
            count--;
        }
        return count;
    }

    public boolean willBeAlive(int row, int column) {
        int neighbors = countNeighbors(row, column);
        if (grid[row][column]) {
            return neighbors == 2 || neighbors == 3;
        }

        return neighbors == 3;
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public void updateGrid() {
        boolean[][] newBoard = new boolean[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newBoard[i][j] = willBeAlive(i, j);
            }
        }
        this.grid = newBoard;
    }

    public void drawGrid(Graphics2D g2d, int xOffset, int yOffset) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int x = xOffset + j * CELL_SIZE;
                int y = yOffset + i * CELL_SIZE;

                if (grid[i][j]) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }

                g2d.setColor(Color.GRAY);
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

}
