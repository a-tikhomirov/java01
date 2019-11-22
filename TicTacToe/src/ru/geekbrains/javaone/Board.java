package ru.geekbrains.javaone;

public class Board  {
    public static final int MIN_SIZE = 3;          // минимальный замер поля по горизонтали/вертикали
    public static final char[] P_SEED = {'X', '0', '.'};   // символы для отображения на поле ходя 1, 2 игрока и пустой клетки
    public static final byte P1_SEED_I = 0;         // индекс символа 1 игрока массива P_SEED
    public static final byte P2_SEED_I = 1;         // индекс символа 2 игрока массива P_SEED
    public static final byte EMPTY_SEED_I = 2;      // индекс символа пустой клетки массива P_SEED

    private byte[][] field;     // массив, содержащий числовое представление игрового поля
    private int fieldSizeX;     // размер поля для игры по горизонтали
    private int fieldSizeY;     // размер поля для игры по вертикали
    private int seedsToWin;     // число символов подряд для выигрыша
    private int turnsCounter;   // число возможных ходов
    private byte winnerIndex;   // 0 - победил первый игрок, 1 - победил второй игрок, -1 - никто не победил
    private int[] lastTurn;     // хранит последний сделанный ход
    private final int LT_X = 0;
    private final int LT_Y = 1;

    private int[][][][] winMap; // массив используемый для проверки выигрыша
    // массив "направлений" линий на поле игры, для проверки на наличие выигрышной комбинации
    private final int W_POS_COMBOS = 0;
    private final int W_X_START = 1;
    private final int W_Y_START = 2;

    public final int[][] DIRS = {{1, 0}, {0, 1},{1, 1}, {1, -1}};
    private final int DIR_ROW = 0;
    private final int DIR_COL = 1;
    private final int DIR_ULD = 2;
    private final int DIR_DLD = 3;
    private final int DIR_X = 0;
    private final int DIR_Y = 1;

    public byte[][] getField() { return field; }

    public int getFieldSizeX() { return fieldSizeX; }

    public int getFieldSizeY() { return fieldSizeY; }

    public int getSeedsToWin() { return seedsToWin; }

    public int getTurnsCounter() { return turnsCounter; }

    public byte getWinnerIndex() {return winnerIndex; }

    public int[] getLastTurn() { return lastTurn; }

    /**
     * Установка параметра <code>seedsToWin</code>
     * @param seedsToWin    число символов подряд для выигрыша
     * @return              true - число валидно, параметр установлен
     *                      false - число не валидно, параметр не установлен
     */
    public boolean setSeedsToWin(int seedsToWin) {
        if (isSeedsNumberValid(seedsToWin)) {
            this.seedsToWin = seedsToWin;
            initWinMap();
            return true;
        } else
            return false;
    }

    public void setSeedsToWin() {
        seedsToWin = getMaxSeedsCount();
        initWinMap();
    }

    /**
     * Вовзращает максимальное число символов подряд для выигрыша
     * для заданной игровой доски
     * @return максимальное число символов подряд для выигрыша
     */
    public int getMaxSeedsCount() {
        return fieldSizeX > fieldSizeY ? fieldSizeY : fieldSizeX;
    }

    /**
     * Проверка заданного числа символов подряд для выигрыша на валидность
     * @param seedsNumber   числа символов подряд для выигрыша для проверки
     * @return              true - заданное число валидно
     *                      false - заданное число не валидно
     */
    public boolean isSeedsNumberValid(int seedsNumber) {
        return seedsNumber >= MIN_SIZE && seedsToWin <= getMaxSeedsCount();
    }

    /**
     * Проверка заданных размеров игрового поля на валидность
     * @param sizeX размер поля по горизонтали для проверки
     * @param sizeY размер поля по вертикали для проверки
     * @return      true - заданные размеры валидны
     *              false - заданные размеры не валидны
     */
    public static boolean isValidSize(int sizeX, int sizeY) {
        return sizeX >= MIN_SIZE && sizeY >= MIN_SIZE;
    }

    /**
     * Конструктор по умолчанию
     */
    public Board() {
        this(MIN_SIZE, MIN_SIZE);
    }

    /**
     * Конструктор класса игрового поля для крестики-нолики
     * @param fieldSizeX величина игрового поля по горизонтали
     * @param fieldSizeY величина игрового поля по вертикали
     */
    public Board(int fieldSizeX, int fieldSizeY) {
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        turnsCounter = fieldSizeX * fieldSizeY;
        winnerIndex = -1;
        lastTurn = new int[2];
        initField();
    }

    /**
     * Конструктор класса на основе имеющегося игрового поля
     * крестики-нолики
     * @param board экземпляр класса <code>Board</code>
     */
    public Board(Board board) {
        fieldSizeX = board.getFieldSizeX();
        fieldSizeY = board.getFieldSizeY();
        field = new byte[fieldSizeY][fieldSizeX];
        turnsCounter = board.getTurnsCounter();
        seedsToWin = board.getSeedsToWin();
        initWinMap();
    }

    /**
     *  Метод осуществляет проверку валидности хода и сам ход если все Ок
     *
     * @param x         координата хода по горизонтали
     * @param y         координата хода по вертикали
     * @param player    индекс для присвоения эдементу массива <code>field[][]</code>
     *                  принимает значения <code>Board.P1_SEED_I</code>
     *                  или <code>Board.P2_SEED_I</code>
     * @return          true    ход валидени совершен
     *                  false   ход не валиден и не совершен
     */
    public boolean makeTurn(int x, int y, byte player) {
        if (isEmptyCell(x, y)) {
            lastTurn[LT_X] = x;
            lastTurn[LT_Y] = y;
            field[y][x] = player;
            --turnsCounter;
            winnerIndex = getWinner(x, y, player);
            return true;
        } else
            return false;
    }

    /**
     * Проверка на ничью. Если число возможных ходов равно нулю
     * и индекс победителя не определен - ничья
     * @return  true - ничья
     *          false - еще есть возможные ходы
     */
    public boolean isDraw() {
        return turnsCounter == 0 && winnerIndex == -1;
    }

    /**
     * Возвращает текущий ход
     * @return  номер текущего хода
     */
    public int getTurnNumber() {
        return fieldSizeX * fieldSizeY - turnsCounter;
    }

    /**
     * Метод проверяет свободна ли ячейка с заданными координатами
     * @param x координата ячейки по горизонтали
     * @param y координата ячейки по вертикали
     * @return  true - координаты валидны и ячейка свободна
     *          false - координаты не валидны или ячейка занята
     */
    public boolean isEmptyCell(int x, int y) {
        return (isValidCoords(x, y)) && field[y][x] == EMPTY_SEED_I;
    }

    /**
     * Проверка заданных координат на валидность
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @return  true - координаты валидны
     *          false - координаты не валидны
     */
    public boolean isValidCoords(int x, int y) {
        return  (x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY);
    }

    /**
     * Возвращает текущее состояние игрового поля в удобном
     * для отображения виде
     * @return  отформатированное содержимое игрового
     *          поля <code>field[][]</code>
     */
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

    /**
     * Возвращает текущее состояние игрового поля в удоном
     * для отображения виде. Добавлена табуляция и отступы между строками
     * @return  отформатированное содержимое игрового
     *          поля <code>field[][]</code>
     */
    public String toStringBS() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = 1; i <= fieldSizeX; ++i) sb.append("\t" + i);
        sb.append("\n");
        for (int i = 0; i < fieldSizeY; ++i) {
            sb.append(i + 1);
            for (int j = 0; j < fieldSizeX; ++j) {
                sb.append("\t" + P_SEED[field[i][j]]);
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

    public byte getWinner(int x, int y, byte player) {
        for (int i = 0; i < DIRS.length; ++i) {
            if (checkLine(winMap[y][x][i][W_POS_COMBOS], winMap[y][x][i][W_X_START], winMap[y][x][i][W_Y_START],
                    DIRS[i][DIR_X], DIRS[i][DIR_Y], player)) return player;
        }
        return -1;
    }

    private boolean checkLine(int possCombos, int xStart, int yStart, int dirX, int dirY, byte seed) {
        if (possCombos <= 0) return false;
        boolean lineCombo;
        for (int i = 0; i < possCombos; ++i) {
            lineCombo = true;
            for (int j = 0; j < seedsToWin; ++j) {
                lineCombo &= field[yStart + (i + j) * dirY][xStart + (i + j) * dirX] == seed;
                if (!lineCombo) break;
            }
            if (lineCombo) return true;
        }
        return false;
    }

    /**
     * Заполнение игрового поля значениями "пустой клетки"
     * <code>P_SEED[EMPTY_SEED_I] = '.'</code>
     */
    private void initField() {
        field = new byte[fieldSizeY][fieldSizeX];
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                field[i][j] = EMPTY_SEED_I;
            }
        }
    }

    /**
     * Заполнение массива карты проверки выигрыша
     */
    private void initWinMap() {
        winMap = new int[fieldSizeY][fieldSizeX][DIRS.length][3];
        int[] lineData;
        boolean xStartLater;
        boolean yStartLater;
        boolean xEndEarlier;
        boolean yEndEarlier;
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                xStartLater = j - seedsToWin + 1 > 0;
                yStartLater = i - seedsToWin + 1 > 0;
                xEndEarlier = j + seedsToWin < fieldSizeX;
                yEndEarlier = i + seedsToWin < fieldSizeY;
                lineData = getRowCheckData(j, i, xStartLater, xEndEarlier);
                winMap[i][j][DIR_ROW] = new int[] {lineData[W_POS_COMBOS], lineData[W_X_START], lineData[W_Y_START]};
                lineData = getColCheckData(j, i, yStartLater, yEndEarlier);
                winMap[i][j][DIR_COL] = new int[] {lineData[W_POS_COMBOS], lineData[W_X_START], lineData[W_Y_START]};
                lineData = getDiagULCheckData(j, i, xStartLater && yStartLater, xEndEarlier && yEndEarlier);
                winMap[i][j][DIR_ULD] = new int[] {lineData[W_POS_COMBOS], lineData[W_X_START], lineData[W_Y_START]};
                lineData = getDiagDLCheckData(j, i, xStartLater && yEndEarlier, xEndEarlier && yStartLater);
                winMap[i][j][DIR_DLD] = new int[] {lineData[W_POS_COMBOS], lineData[W_X_START], lineData[W_Y_START]};
            }
        }
    }

    /**
     * Дописать
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param startLater    true - отрезок начинается не с начала координат
     *                      false - отрезок начинается с начала координат
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      false - отрезок заканчивается на границе поля
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private int[] getRowCheckData(int x, int y, boolean startLater, boolean endEarlier) {
        int xStart = (startLater) ? x - seedsToWin + 1 : 0;
        int xEnd = (endEarlier) ? x + seedsToWin - 1 : fieldSizeX - 1;
        int possCombos = xEnd - xStart - seedsToWin + 2;
        return new int[] {possCombos, xStart, y};
    }

    /**
     * Дописать
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param startLater    true - отрезок начинается не с начала координат
     *                      false - отрезок начинается с начала координат
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      false - отрезок заканчивается на границе поля
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private int[] getColCheckData(int x, int y, boolean startLater, boolean endEarlier) {
        int yStart = (startLater) ? y - seedsToWin + 1 : 0;
        int yEnd = (endEarlier) ? y + seedsToWin - 1 : fieldSizeY - 1;
        int possCombos = yEnd - yStart - seedsToWin + 2;
        return new int[] {possCombos, x, yStart};
    }

    /**
     * Дописать.
     * Диагональ лево-верх(UpLeft)- право-низ
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param startLater    true - отрезок начинается не с начала координат
     *                      по вертикали и горизонтали
     *                      false - отрезок начинается с начала координат
     *                      по вертикали и/или по горизонтали
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      по вертикали и горизонтали
     *                      false - отрезок заканчивается на границе поля
     *                      по вертикали и/или по горизонтали
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private int[] getDiagULCheckData(int x, int y, boolean startLater, boolean endEarlier) {
        int xStart = x, xEnd = x;
        int yStart = y, yEnd = y;
        int possCombos;
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
        return new int[] {possCombos, xStart, yStart};
    }

    /**
     * Дописать.
     * Диагональ лево-низ(DownLeft)- право-верх
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param startLater    true - отрезок начинается не с начала координат
     *                      по вертикали и горизонтали
     *                      false - отрезок начинается с начала координат
     *                      по вертикали и/или по горизонтали
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      по вертикали и горизонтали
     *                      false - отрезок заканчивается на границе поля
     *                      по вертикали и/или по горизонтали
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private int[] getDiagDLCheckData(int x, int y, boolean startLater, boolean endEarlier) {
        int xStart = x, xEnd = x;
        int yStart = y, yEnd = y;
        int possCombos;
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
        return new int[] {possCombos, xStart, yStart};
    }
}
