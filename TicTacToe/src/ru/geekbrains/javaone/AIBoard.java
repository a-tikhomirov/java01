package ru.geekbrains.javaone;

import java.util.ArrayList; // для возможности использования List<int[]> possibleMoves = new ArrayList<int[]>()
import java.util.List;      // для возможности использования List<int[]>

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
    private int[] lastTurn = new int[2];

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
        this.lastTurn[0] = board.getLastTurn()[0];
        this.lastTurn[1] = board.getLastTurn()[1];
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
     *  веса хода. Например, если для победы нужно
     *  3 X подряд, то для строки вида:
     *  [X X X] - вес = 10^3
     *  [X . X] или [X X .] или [. X X] - вес = 10^2
     */
    private void initKoefficients() {
        koeff = new int[seedsToWin];
        for (int i = seedsToWin - 1; i >= 0; --i) {
            koeff[i] = (int) Math.pow(10, i + 1);
        }
    }

    /**
     * Вовзращает массив возможных ходов. Вовращает минимально
     * необходимое число возможных ходов, что может существенно
     * сократить время определения оптимального хода при большой
     * глубине построения дерева возможных ходов.
     *
     * Минимальный диапазон рассчитывается на основе последнего
     * сделанного хода {x, y}. Границы диапазона от
     * <code>x - seedsToWin + 1</code> и <code>y - seedsToWin + 1</code> до
     * <code>x + seedsToWin - 1</code> и <code>y + seedsToWin - 1</code>
     * @return  возвращает <code>List<int[2]></code> лист массивов возможных ходов
     */
    public List<int[]> getPossibleTurns() {
        if (winnerIndex != -1 || turnsCounter == 0) return null;
        int xStart = (lastTurn[0] - seedsToWin + 1 > 0) ?
                lastTurn[0] - seedsToWin + 1 : 0;
        int yStart = (lastTurn[1] - seedsToWin + 1 > 0) ?
                lastTurn[1] - seedsToWin + 1 : 0;
        int xEnd = (lastTurn[0] + seedsToWin < fieldSizeX) ?
                lastTurn[0] + seedsToWin - 1 : fieldSizeX - 1;
        int yEnd = (lastTurn[1] + seedsToWin < fieldSizeY) ?
                lastTurn[1] + seedsToWin - 1 : fieldSizeY - 1;
        //int[][] possibleMoves = new int[][2];
        List<int[]> possibleMoves = new ArrayList<int[]>();
        for (int i = yStart; i <= yEnd; ++i) {
            for (int j = xStart; j <= xEnd; ++j) {
                if (field[i][j] == EMPTY_SEED_I) {
                    possibleMoves.add(new int[] {j, i});
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
     *  Переопределнный метод родителя.
     *  В переопределении метода добавлена отмена хода и сброс счетчика веса
     *
     * @param x         координата хода по горизонтали
     * @param y         координата хода по вертикали
     * @param player    индекс для присвоения эдементу массива <code>field[][]</code>
     *                  принимает значения <code>Board.P1_SEED_I</code>
     *                  или <code>Board.P2_SEED_I</code> или <code>Board.EMPTY_SEED_I</code>
     * @return          true    ход валиден и выполнен
     *                  false   ход не валиден и не выполнен
     */
    @Override
    public boolean makeTurn(int x, int y, byte player) {
        if (isEmptyCell(x, y) || player == EMPTY_SEED_I) {
            field[y][x] = player;
            if (player == EMPTY_SEED_I) {
                ++turnsCounter;             // если отмена хода, число возможных ходов увеличивается на 1
            } else {
                lastTurn[0] = x;
                lastTurn[1] = y;
                --turnsCounter;             // уменьшение числа возможных ходов
                // сброс значения веса предыдущего хода для дальнейшего расчета в ходе выполнения метода getWinner
                turnScore = 0;
                // проверка на выигрыш текущего игрока и просчет веса текущего хода благодаря переопределениям (см.ниже)
                winnerIndex = getWinner(x, y,  player);
            }
            return true;
        } else
            return false;
    }

    /**
     * Проверка на выигрыш горизонтального отрезка длиной <code>seedsToWin</code>
     * В переопределении метода добавлен расчет веса на отрезке для обоих игроков
     *
     * @param y         координата отрезка по вертикали
     * @param xStart    начало отрезка по горизонтали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    @Override
    protected boolean rowLineCheck(int y, int xStart, byte player) {
        int[] scores = {0, 0};
        byte oppSeed = 1;   // индекс противника для первой итерации
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < seedsToWin; ++i) {
                if (field[y][xStart + i] == k) ++scores[k];
                // если в ячейке содержиться индекс противника - можно преккращать проверку
                if (field[y][xStart + i] == oppSeed) {
                    scores[k] = 0;
                    break;
                }
            }
            oppSeed = 0; // индекс противника для второй итерации
        }
        turnScore = turnScore + countScore(scores[ai_seed]) - countScore(scores[human_seed]);
        return scores[player] == seedsToWin;
    }

    /**
     * Проверка на выигрыш вертикального отрезка длиной <code>seedsToWin</code>
     * В переопределении метода добавлен расчет веса на отрезке для обоих игроков
     *
     * @param x         координата отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    @Override
    protected boolean colLineCheck(int x, int yStart, byte player) {
        int[] scores = {0, 0};
        byte oppSeed = 1;
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < seedsToWin; ++i) {
                if (field[yStart + i][x] == k) ++scores[k];
                if (field[yStart + i][x] == oppSeed) {
                    scores[k] = 0;
                    break;
                }
            }
            oppSeed = 0;
        }
        turnScore = turnScore + countScore(scores[ai_seed]) - countScore(scores[human_seed]);
        return scores[player] == seedsToWin;
    }

    /**
     * Проверка на выигрыш диагонального отрезка длиной <code>seedsToWin</code>
     * Диагональ лево-верх(UpLeft)- право-низ
     * В переопределении метода добавлен расчет веса на отрезке для обоих игроков
     *
     * @param xStart    начало отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    @Override
    protected boolean ulDiagLineCheck(int xStart, int yStart, byte player) {
        int[] scores = {0, 0};
        byte oppSeed = 1;
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < seedsToWin; ++i) {
                if (field[yStart + i][xStart + i] == k) ++scores[k];
                if (field[yStart + i][xStart + i] == oppSeed) {
                    scores[k] = 0;
                    break;
                }
            }
            oppSeed = 0;
        }
        turnScore = turnScore + countScore(scores[ai_seed]) - countScore(scores[human_seed]);
        return scores[player] == seedsToWin;
    }

    /**
     * Проверка на выигрыш диагонального отрезка длиной <code>seedsToWin</code>
     * Диагональ лево-низ(DownLeft)- право-верх
     * В переопределении метода добавлен расчет веса на отрезке для обоих игроков
     *
     * @param xStart    начало отрезка по горизонтали
     * @param yStart    начало отрезка по вертикали
     * @param player    индекс для проверки выигрыша
     * @return          true - выигрышная комбинация была найдена
     *                  false - выигрышная комбинация не была найдена
     */
    @Override
    protected boolean dlDiagLineCheck(int xStart, int yStart, byte player) {
        int[] scores = {0, 0};
        byte oppSeed = 1;
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < seedsToWin; ++i) {
                if (field[yStart - i][xStart + i] == k) ++scores[k];
                if (field[yStart - i][xStart + i] == oppSeed) {
                    scores[k] = 0;
                    break;
                }
            }
            oppSeed = 0;
        }
        turnScore = turnScore + countScore(scores[ai_seed]) - countScore(scores[human_seed]);
        return scores[player] == seedsToWin;
    }

    /**
     * Подсчет веса для строки/столбца/диагонали
     * @param toCount количество идексов в строке/столбце/диагонали
     *                размером <code>seedsToWin</code>
     *                Например, для выигрыша нужно 3 X подряд,
     *                проверяемая строка была вида: X . X - в этом
     *                случае значение <code>toCount</code> равно 2
     * @return  подсчитанное значение веса
     */
    private int countScore(int toCount) {
        int score = 0;
        for (int i = 0; i < koeff.length; ++i)
            if (toCount == i + 1) score += koeff[i];
        return score;
    }
}
