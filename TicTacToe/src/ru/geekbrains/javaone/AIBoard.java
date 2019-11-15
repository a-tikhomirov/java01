package ru.geekbrains.javaone;

public class AIBoard extends Board {
    // Необхидимые поля родительского класса продублированы для сохранения уровня доступа private
    private byte[][] field;     // массив, содержащий числовое представление игрового поля
    private int fieldSizeX;     // размер поля для игры по горизонтали
    private int fieldSizeY;     // размер поля для игры по вертикали
    private int seedsToWin;     // число символов подряд для выигрыша
    private int turnsCounter;   // число возможных ходов
    private byte winnerIndex;   // 0 - победил первый игрок, 1 - победил второй игрок, -1 - никто не победил

    private byte ai_seed;       // индекс ИИ - используется для подсчета веса хода
    private byte human_seed;    // индекс игрока - используется для подсчета веса хода
    private int[] koeff;        // массив коэффициентов - используется для подсчета веса хода
    private int turnScore;      // текущий просчитанный вес хода

    /**
     *  Создание экземпляра класса AIBoard на основе Board
     *
     * @param board         игровая доска, на основе которой
     *                      создается экспериментальная доска для ИИ
     * @param ai_seed       индекс ИИ, принимает значения
     *                      <code>Board.P1_SEED_I</code> или <code>Board.P2_SEED_I</code>
     * @param human_seed    индекс игрока, принимает значения
     *                      <code>Board.P1_SEED_I</code> или <code>Board.P2_SEED_I</code>
     */
    public AIBoard(Board board, byte ai_seed, byte human_seed){
        super(board);
        this.fieldSizeX = super.getFieldSizeX();
        this.fieldSizeY = super.getFieldSizeY();
        this.field = super.getField();
        this.turnsCounter = fieldSizeX * fieldSizeY;
        copyField(board.getField());
        this.seedsToWin = super.getSeedsToWin();
        this.winnerIndex = -1;
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
        initKoefficients();
    }

    /**
     * Копирование содержимого поля для игры доски Board в
     * текущее поле для игры
     * @param field поле для игры доски Board
     */
    private void copyField(byte[][] field) {
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                this.field[i][j] = field[i][j];
                if (this.field[i][j] != EMPTY_SEED_I)
                    --turnsCounter;
            }
        }
    }

    /**
     *  Инициализация массива коэффициентов для расчета
     *  веса хода
     */
    private void initKoefficients() {
        koeff = new int[seedsToWin];
        for (int i = seedsToWin - 1; i >= 0; --i) {
            koeff[i] = (int) Math.pow(10, i + 1);
        }
    }

    /**
     * Вовзращает массива возможных ходов
     * @return  возвращает <code>int[turnsCounter][2]</code>
     *          где turnsCounter - число вомзожных ходов
     */
    public int[][] getPossibleTurns() {
        if (winnerIndex != -1 || turnsCounter == 0) return null;
        int[][] possibleMoves = new int[turnsCounter][2];
        int movesCount = 0;
        for (int i = 0; i < fieldSizeY; ++i) {
            for (int j = 0; j < fieldSizeX; ++j) {
                if (field[i][j] == EMPTY_SEED_I) {
                    possibleMoves[movesCount][0] = j;
                    possibleMoves[movesCount][1] = i;
                    ++movesCount;
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Возвращает значение веса последнего хода
     * @return  значение веса последнего сделанного хода
     */
    public int getTurnScore() {
        return turnScore;
    }

    /**
     *  Перегруженный метод родителя. Перегрузка требуется
     *  в связи с необходимостью отмены ход и сброса счетчика веса
     *
     * @param x         координата хода по горизонтали
     * @param y         координата хода по вертикали
     * @param player    индекс для присвоения эдементу массива <code>field[][]</code>
     *                  принимает значения <code>Board.P1_SEED_I</code>
     *                  или <code>Board.P2_SEED_I</code> или <code>Board.EMPTY_SEED_I</code>
     * @return          true    ход валиден
     *                  false   ход невалиден
     */
    @Override
    public boolean makeTurn(int x, int y, byte player) {
        if (isEmptyCell(x, y) || player == EMPTY_SEED_I) {
            field[y][x] = player;
            if (player == EMPTY_SEED_I) {
                ++turnsCounter;             // если отмена хода, число возможных ходов увеличивается на 1
            } else {
                --turnsCounter;             // уменьшение числа возможных ходов
                // сброс значения веса предыдущего хода для дальнейшего расчета в ходе выполнения метода getWinner
                turnScore = 0;
                // проверка на выигрыш текущего игрока и просчет веса текущего хода благодаря перегрузкам (см.ниже)
                winnerIndex = getWinner(x, y,  player);
            }
            return true;
        } else
            return false;
    }

    @Override
    protected boolean rowLineCheck(int y, int xStart, byte player) {
        boolean rowCombo = true;
        int aiScore = 0;
        int humanScore = 0;
        for (int i = 0; i < seedsToWin; ++i) {
            rowCombo &= field[y][xStart + i] == player;
            if (field[y][xStart + i] == ai_seed) ++aiScore;
            if (field[y][xStart + i] == human_seed) ++humanScore;
        }
        turnScore = turnScore + countScore(aiScore) - countScore(humanScore);
        return rowCombo;
    }

    @Override
    protected boolean colLineCheck(int x, int yStart, byte player) {
        boolean colCombo = true;
        int aiScore = 0;
        int humanScore = 0;
        for (int i = 0; i < seedsToWin; ++i) {
            colCombo &= field[yStart + i][x] == player;
            if (field[yStart + i][x] == ai_seed) ++aiScore;
            if (field[yStart + i][x] == human_seed) ++humanScore;
        }
        turnScore = turnScore + countScore(aiScore) - countScore(humanScore);
        return colCombo;
    }

    @Override
    protected boolean ulDiagLineCheck(int xStart, int yStart, byte player) {
        boolean diagCombo = true;
        int aiScore = 0;
        int humanScore = 0;
        for (int i = 0; i < seedsToWin; ++i) {
            diagCombo &= field[yStart + i][xStart + i] == player;
            if (field[yStart + i][xStart + i] == ai_seed) ++aiScore;
            if (field[yStart + i][xStart + i] == human_seed) ++humanScore;
        }
        turnScore = turnScore + countScore(aiScore) - countScore(humanScore);
        return diagCombo;
    }

    @Override
    protected boolean dlDiagLineCheck(int xStart, int yStart, byte player) {
        boolean diagCombo = true;
        int aiScore = 0;
        int humanScore = 0;
        for (int i = 0; i < seedsToWin; ++i) {
            diagCombo &= field[yStart - i][xStart + i] == player;
            if (field[yStart - i][xStart + i] == ai_seed) ++aiScore;
            if (field[yStart - i][xStart + i] == human_seed) ++humanScore;
        }
        turnScore = turnScore + countScore(aiScore) - countScore(humanScore);
        return diagCombo;
    }

    /**
     * Подсчет веса для строки/столбца/диагонали
     * @param toCount количество идексов в строке/столбце/диагонали
     *                размером <code>seedsToWin</code>
     *                Например, для выигрыша нужно 3 X подряд,
     *                проверяемая строка была вида: X . X - в этом
     *                случае значение <code>toCount</code> равно 2
     * @return  подсчиьанное значение веса
     */
    private int countScore(int toCount) {
        int score = 0;
        for (int i = 0; i < koeff.length; ++i)
            if (toCount == i + 1) score += koeff[i];
        return score;
    }
}
