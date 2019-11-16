package ru.geekbrains.javaone;

public class Board {
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
    private int[] lastTurn = new int[2];

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
        field = new byte[fieldSizeY][fieldSizeX];
        turnsCounter = fieldSizeX * fieldSizeY;
        winnerIndex = -1;
        initField();
    }

    /**
     * Заполнение игрового поля значениями "пустой клетки"
     * <code>P_SEED[EMPTY_SEED_I] = '.'</code>
     */
    private void initField() {
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                field[i][j] = EMPTY_SEED_I;
            }
        }
    }

    /**
     * Конструктор класса на основе имеющегося игрового поля
     * крестики-нолики
     * @param board экземпляр класса <code>Board</code>
     */
    protected Board(Board board ) {
        fieldSizeX = board.getFieldSizeX();
        fieldSizeY = board.getFieldSizeY();
        field = new byte[fieldSizeY][fieldSizeX];
        winnerIndex = -1;
        seedsToWin = board.getSeedsToWin();
    }

    public byte[][] getField() { return field; }

    public int getFieldSizeX() { return fieldSizeX; }

    public int getFieldSizeY() { return fieldSizeY; }

    public byte getWinnerIndex() {return winnerIndex; }

    public int getSeedsToWin() { return seedsToWin; }

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
            return true;
        } else
            return false;
    }

    public void setSeedsToWin() {
        seedsToWin = getMaxSeedsCount();
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
            lastTurn[0] = x;
            lastTurn[1] = y;
            field[y][x] = player;
            --turnsCounter;
            winnerIndex = getWinner(x, y,  player);
            return true;
        } else
            return false;
    }

    /**
     * Проверка на выигрыш текущего хода игрока с индексом <code>player</code>.
     * Проверка проводится только на строке, столбце и диагоналях к которым
     * принадлежит сделанный ход (т.е. проверяется не все поле)
     *
     * Строка/столбец/диагонали проверяются не полностью, а ограниченными отрезоками.
     * Например, для выигрыша нужно 3 X подряд. После хода, строка/столбец/диагональ приняла вид:
     * [. . . X . . . .] - в этом случае, проверяться будет только отрезок вида:
     * .[. . X . .]. . - то есть проверяться будут всего три комбинации, вместо шести.
     * Таким образом, максимальная длина проверяемого отрезка будет равна
     * <code>seedsToWin * 2 - 1</code>
     *
     * @param x         координата хода по горизонтали
     * @param y         координата хода по вертикали
     * @param player    индекс текущего игрока, принимает значения
     *                  <code>Board.P1_SEED_I</code> или <code>Board.P2_SEED_I</code>
     * @return          <code>player</code>  индекс текущего игрока, если был выигрыш
     *                  -1    если выигрышная комбинация не была обнаружена
     */
    protected byte getWinner(int x, int y, byte player) {
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

    /**
     * Проверка на выигрыш горизонтального отрезка строки, к которой принадлежит текущий ход.
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param player        индекс текущего игрока, принимает значения
     * @param startLater    true - отрезок начинается не с начала координат
     *                      false - отрезок начинается с начала координат
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      false - отрезок заканчивается на границе поля
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private boolean checkRow(int x, int y, byte player, boolean startLater, boolean endEarlier) {
        int xStart = (startLater) ? x - seedsToWin + 1 : 0;
        int xEnd = (endEarlier) ? x + seedsToWin - 1 : fieldSizeX - 1;
        int possCombos = xEnd - xStart - seedsToWin + 2;
//        System.out.printf("Row: x: %d to %d;\ty: %d;\t" +
//                "Row possible combos = %d\n", xStart, xEnd, y, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            // Для отрезка вида: [X X X . .] проверка завершится на первой итерации (если нужно 3 X подряд)
            if (rowLineCheck(y, xStart + i, player)) return true;
        }
        return false;
    }

    /**
     * Проверка на выигрыш горизонтального отрезка длиной <code>seedsToWin</code>
     * Модификатор доступа объявлен как protected в связи с необходимостью
     * переопределения метода в классе-наследнике (для расчета веса хода)
     *
     * @param y         координата отрезка по вертикали
     * @param xStart    начало отрезка по горизонтали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    protected boolean rowLineCheck(int y, int xStart, byte player) {
        boolean rowCombo = true;
        for (int i = 0; i < seedsToWin; ++i) {
            rowCombo &= field[y][xStart + i] == player;
            // для отрезка вида [. X .] - проверка завершится на первой итерации
            if (!rowCombo) break;
        }
        return rowCombo;
    }

    /**
     * Проверка на выигрыш вертикального отрезка строки, к которой принадлежит текущий ход.
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param player        индекс текущего игрока, принимает значения
     * @param startLater    true - отрезок начинается не с начала координат
     *                      false - отрезок начинается с начала координат
     * @param endEarlier    true - отрезок заканчивается не на границе поля
     *                      false - отрезок заканчивается на границе поля
     * @return              true - выигрышная комбинация была найдена
     *                      false - выигрышная комбинация не была найдена
     */
    private boolean checkCol(int x, int y, byte player, boolean startLater, boolean endEarlier) {
        int yStart = (startLater) ? y - seedsToWin + 1 : 0;
        int yEnd = (endEarlier) ? y + seedsToWin - 1 : fieldSizeY - 1;
        int possCombos = yEnd - yStart - seedsToWin + 2;
//        System.out.printf("Col: x: %d;\ty: %d to %d;\t" +
//                "Col possible combos = %d\n", x, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            if (colLineCheck(x, yStart + i, player)) return true;
        }
        return false;
    }

    /**
     * Проверка на выигрыш вертикального отрезка длиной <code>seedsToWin</code>
     * Модификатор доступа объявлен как protected в связи с необходимостью
     * переопределения метода в классе-наследнике (для расчета веса хода)
     *
     * @param x         координата отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    protected boolean colLineCheck(int x, int yStart, byte player) {
        boolean colCombo = true;
        for (int i = 0; i < seedsToWin; ++i) {
            colCombo &= field[yStart + i][x] == player;
            if (!colCombo) break; // если есть хоть одно несовпадение - можно прекращать проверку
        }
        return colCombo;
    }

    /**
     * Проверка на выигрыш диагонального отрезка строки, к которой принадлежит текущий ход.
     * Диагональ лево-верх(UpLeft)- право-низ
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param player        индекс текущего игрока, принимает значения
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
    private boolean checkDiagUL(int x, int y, byte player, boolean startLater, boolean endEarlier) {
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
//        System.out.printf("UL Diag: x: %d to %d;\ty: %d " +
//                "to %d;\tDiag possible combos = %d\n",
//                xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            if (ulDiagLineCheck(xStart + i, yStart + i, player)) return true;
        }
        return false;
    }

    /**
     * Проверка на выигрыш диагонального отрезка длиной <code>seedsToWin</code>
     * Диагональ лево-верх(UpLeft)- право-низ
     * Модификатор доступа объявлен как protected в связи с необходимостью
     * переопределения метода в классе-наследнике (для расчета веса хода)
     *
     * @param xStart    начало отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    protected boolean ulDiagLineCheck(int xStart, int yStart, byte player) {
        boolean diagCombo = true;
        for (int i = 0; i < seedsToWin; ++i) {
            diagCombo &= field[yStart + i][xStart + i] == player;
            if (!diagCombo) break; // если есть хоть одно несовпадение - можно прекращать проверку
        }
        return diagCombo;
    }

    /**
     * Проверка на выигрыш диагонального отрезка строки, к которой принадлежит текущий ход.
     * Диагональ лево-низ(DownLeft)- право-верх
     *
     * @param x             координата хода по горизонтали
     * @param y             координата хода по вертикали
     * @param player        индекс текущего игрока, принимает значения
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
    private boolean checkDiagDL(int x, int y, byte player, boolean startLater, boolean endEarlier) {
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
//        System.out.printf("DL Diag: x: %d to %d;\ty: %d " +
//                "to %d;\tDiag possible combos = %d\n",
//                xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            if (dlDiagLineCheck(xStart + i, yStart - i, player)) return true;
        }
        return false;
    }

    /**
     * Проверка на выигрыш диагонального отрезка длиной <code>seedsToWin</code>
     * Диагональ лево-низ(DownLeft)- право-верх
     * Модификатор доступа объявлен как protected в связи с необходимостью
     * переопределения метода в классе-наследнике (для расчета веса хода)
     *
     * @param xStart    начало отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    protected boolean dlDiagLineCheck(int xStart, int yStart, byte player) {
        boolean diagCombo = true;
        for (int i = 0; i < seedsToWin; ++i) {
            diagCombo &= field[yStart - i][xStart + i] == player;
            if (!diagCombo) break; // если есть хоть одно несовпадение - можно прекращать проверку
        }
        return diagCombo;
    }

    /**
     * Возвращает текущее состояние игрового поля в удоном
     * для понимания виде
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
     * для понимания виде. Добавлена табуляция и отступы между строками
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
}
