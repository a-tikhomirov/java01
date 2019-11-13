package ru.geekbrains.javaone;

public class Board {
    private static final int MIN_SIZE = 3;          // минимальный замер поля по горизонтали/вертикали
    private final char[] P_SEED = {'X', '0', '.'};  // символы для отображения на поле ходя 2 игрка, 2 игрока и пустой клетки
    public static final byte P1_SEED_I = 0;         // индекс символа 1 игрока массива P_SEED
    public static final byte P2_SEED_I = 1;         // индекс символа 2 игрока массива P_SEED
    private final byte EMPTY_SEED_I = 2;            // индекс символа пустой клетки массива P_SEED

    private byte[][] field;     // массив, содержащий числовое представление игрового поля
    private int fieldSizeX;     // размер поля для игры по горизонтали
    private int fieldSizeY;     // размер поля для игры по вертикали
    private int seedsToWin;     // число символов подряд для выигрыша
    private int turnsCounter;   // число возможных ходов
    private byte winnerIndex;   // 0 - победил первый игрок, 1 - победил второй игрок, -1 - никто не победил

    public Board() {
        this(MIN_SIZE, MIN_SIZE);
    }

    public Board(int fieldSizeX, int fieldSizeY) {
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        field = new byte[fieldSizeY][fieldSizeX];
        turnsCounter = fieldSizeX * fieldSizeY;
        winnerIndex = -1;
        initField();
    }

    private void initField() {
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                field[i][j] = EMPTY_SEED_I;
            }
        }
    }

    public void setSeedsToWin() {
        seedsToWin = getMaxSeedsCount();
    }

    public boolean setSeedsToWin(int seedsToWin) {
        if (isSeedsNumberValid(seedsToWin)) {
            this.seedsToWin = seedsToWin;
            return true;
        } else
            return false;
    }

    public boolean makeTurn(int x, int y, byte player) {
        if (isEmptyCell(x, y)) {
            field[y][x] = player;
            --turnsCounter;
            winnerIndex = getWinner(x, y,  player);
            return true;
        } else
            return false;
    }

    private byte getWinner(int x, int y, byte player) {
        boolean xStartLater = x - seedsToWin + 1 > 0;
        boolean yStartLater = y - seedsToWin + 1 > 0;
        boolean xEndEarlier = x + seedsToWin < fieldSizeX;
        boolean yEndEarlier = y + seedsToWin < fieldSizeY;
        if (checkRow(x, y, player, xStartLater, xEndEarlier) || checkCol(x, y, player, yStartLater, yEndEarlier)
                || checkDiagUL(x, y, player, xStartLater && yStartLater, xEndEarlier && yEndEarlier)
                || checkDiagDL(x, y, player, xStartLater && yEndEarlier, xEndEarlier && yStartLater)){
            return player;
        } else
            return -1;
    }

    private boolean checkRow(int x, int y, byte player, boolean startLater, boolean endEarlier) {
        boolean rowCombo;
        int xStart = (startLater) ? x - seedsToWin + 1 : 0;
        int xEnd = (endEarlier) ? x + seedsToWin - 1 : fieldSizeX - 1;
        int possCombos = xEnd - xStart - seedsToWin + 2;
        System.out.printf("Row: x: %d to %d;\t y: %d;\t" +
                "Row possible combos = %d\n", xStart, xEnd, y, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            rowCombo = true;
            for (int j = 0; j < seedsToWin; ++j) {
                rowCombo &= field[y][j + xStart + i] == player;
                if (!rowCombo) break;
            }
            if (rowCombo) return true;
        }
        return false;
    }

    private boolean checkCol(int x, int y, byte player, boolean startLater, boolean endLess) {
        boolean colCombo;
        int yStart = (startLater) ? y - seedsToWin + 1 : 0;
        int yEnd = (endLess) ? y + seedsToWin - 1 : fieldSizeY - 1;
        int possCombos = yEnd - yStart - seedsToWin + 2;
        System.out.printf("Col: x: %d;\t y: %d to %d;\t" +
                "Row possible combos = %d\n", x, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            colCombo = true;
            for (int j = 0; j < seedsToWin; ++j) {
                colCombo &= field[j + yStart + i][x] == player;
                if (!colCombo) break;
            }
            if (colCombo) return true;
        }
        return false;
    }

    private boolean checkDiagUL(int x, int y, byte player, boolean startLater, boolean endEarlier) {
        int xStart = x;
        int yStart = y;
        int xEnd = x;
        int yEnd = y;
        int possCombos;
        boolean diagCombo;
        if (startLater) {
            xStart = x - seedsToWin + 1;
            yStart = y - seedsToWin + 1;
        } else
            while (xStart != 0 && yStart != 0) {
                --xStart;
                --yStart;
            }
        if (endEarlier) {
            xEnd = x + seedsToWin - 1;
            yEnd = y + seedsToWin - 1;
        } else
            while (xEnd != fieldSizeX - 1 && yEnd != fieldSizeY - 1) {
                ++xEnd;
                ++yEnd;
            }
        possCombos = yEnd - yStart - seedsToWin + 2;
        System.out.printf("UL Diag: x: %d to %d;\t y: %d " +
                "to %d;\t Diag possible combos = %d\n",
                xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            diagCombo = true;
            for (int j = 0; j < seedsToWin; ++j) {
                diagCombo &= field[j + yStart + i][j + xStart + i] == player;
                if (!diagCombo) break;
            }
            if (diagCombo) return true;
        }
        return false;
    }

    private boolean checkDiagDL(int x, int y, byte player, boolean startLater, boolean endEarlier) {
        int xStart = x;
        int yStart = y;
        int xEnd = x;
        int yEnd = y;
        int possCombos;
        boolean diagCombo;
        if (startLater) {
            xStart = x - seedsToWin + 1;
            yStart = y + seedsToWin - 1;
        } else
            while (xStart != 0 && yStart != fieldSizeY - 1) {
                --xStart;
                ++yStart;
            }
        if (endEarlier) {
            xEnd = x + seedsToWin - 1;
            yEnd = y - seedsToWin + 1;
        } else
            while (xEnd != fieldSizeX - 1 && yEnd != 0) {
                ++xEnd;
                --yEnd;
            }
        possCombos = yStart - yEnd - seedsToWin + 2;
        System.out.printf("DL Diag: x: %d to %d;\t y: %d " +
                "to %d;\t Diag possible combos = %d\n", xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            diagCombo = true;
            for (int j = 0; j < seedsToWin; ++j) {
                diagCombo &= field[yStart - i - j][j + xStart + i] == player;
                if (!diagCombo) break;
            }
            if (diagCombo) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  \t");
        for (int i = 1; i <= fieldSizeX; ++i) sb.append(" " + i);
        sb.append("\n");
        for (int i = 0; i < fieldSizeY; ++i) {
            sb.append((i + 1) + "\t|");
            for (int j = 0; j < fieldSizeX; ++j) {
                sb.append(P_SEED[field[i][j]] + "|");
            }
            sb.append("\n");
        }
        sb.append("---");
        for (int i = 1; i <= fieldSizeX; ++i) sb.append("--");
        return sb.toString();
    }

    public String toStringBS() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = 0; i < fieldSizeX; ++i) sb.append("\t" + i);
        sb.append("\n");
        for (int i = 0; i < fieldSizeY; ++i) {
            sb.append(i);
            for (int j = 0; j < fieldSizeX; ++j) {
                sb.append("\t" + P_SEED[field[i][j]]);
            }
            sb.append("\n\n");
        }
        sb.append("---");
        for (int i = 1; i <= fieldSizeX; ++i) sb.append("--");
        return sb.toString();
    }

    public static int getMinSize() { return MIN_SIZE; }

    public int getFieldSizeX() {
        return fieldSizeX;
    }

    public int getFieldSizeY() {
        return fieldSizeY;
    }

    public int getSeedsToWin() {
        return seedsToWin;
    }

    public byte getWinnerIndex() {
        return winnerIndex;
    }

    public static boolean isValidSize(int sizeX, int sizeY) {
        return sizeX >= MIN_SIZE && sizeY >= MIN_SIZE;
    }

    public int getMinSeedsCount() {
        return MIN_SIZE;
    }

    public int getMaxSeedsCount() {
        return fieldSizeX > fieldSizeY ? fieldSizeY : fieldSizeX;
    }

    public boolean isSeedsNumberValid(int seedsNumber) {
        return seedsNumber >= MIN_SIZE && ((fieldSizeX > fieldSizeY) ? (seedsNumber <= fieldSizeY) : (seedsNumber <= fieldSizeX));
    }

    public boolean isValidCoords(int x, int y) {
        return  (x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY);
    }

    public boolean isEmptyCell(int x, int y) {
        if (isValidCoords(x, y))
            return field[y][x] == EMPTY_SEED_I;
        else
            return false;
    }

    public boolean isDraw() {
        return turnsCounter == 0;
    }

}
