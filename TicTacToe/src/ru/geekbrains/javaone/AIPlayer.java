package ru.geekbrains.javaone;

import java.util.Random;    // используется для генерации псевдослучайных значений
import java.util.List;      // для возможности использования List<int[]>

public class AIPlayer {
    private static final Random RANDOM = new Random();  // используется для генерации псевдослучайных значений
    private static final int[] DIFF = {0, 1, 2, 4, 5};  // массив обозначающий сложность алг-ма выбора оптимального хода

    public static final int MIN_DIF = 0;                // минимальная сложность ИИ
    public static final int MAX_DIF = DIFF.length - 1;  // максимальная сложность ИИ
    public static final int DEF_DIF = 2;                // сложность ИИ по умолчанию

    private byte ai_seed;       // индекс ИИ - используется для подсчета веса хода
    private byte human_seed;    // индекс игрока - используется для подсчета веса хода
    private int difficulty;     // сложность алг-ма выбора оптимального хода (индекс элемента массива DIFF)
                                // (0 - случайные ходы, > 0 - различная глубина дерева выбора оптимального хода)
    private AIBoard aiBoard;    // экспериментальная игровая доска для построения дерева возможных ходов и выбора оптимального

    /**
     * В случае вызова конструктора без параметра Сложность,
     * сложность задается по умолчанию <code>difficulty = DEF_DIF</code>
     * @param ai_seed       индекс ИИ для игры
     * @param human_seed    индекс игрока
     */
    public AIPlayer(byte ai_seed, byte human_seed) {
        this(ai_seed, human_seed, DEF_DIF);
    }

    /**
     * Конструктор класса ИИ. Класс создан для выбора хода ИИ в игре крестики-нолики
     * @param ai_seed       индекс ИИ для игры
     * @param human_seed    индекс игрока
     * @param difficulty    сложность ИИ, принимает значения
     *                      от <code>AIPlayer.MIN_DIF</code> до <code>AIPlayer.MAX_DIF</code>
     */
    public AIPlayer(byte ai_seed, byte human_seed, int difficulty) {
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
        this.difficulty = difficulty;
    }

    /**
     * Проверка на валидность заданного значения уровня сложности
     * @param difficulty    уровень сложности для проверки
     * @return              true - заданное значение валидно
     *                      false - заданное значение не валидно
     */
    public static boolean isValidDifficluty(int difficulty) {
        return difficulty >= MIN_DIF && difficulty <= MAX_DIF;
    }

    /**
     * Метод возвращает координаты хода ИИ для заданной игровой доски
     * @param board экземпляр класса <code>Board</code>
     * @return      {x, y} - координаты хода ИИ
     */
    public int[] getXY(Board board) {
        long startTime = System.nanoTime();
        int[] move;
        // если сложность нулевая - случайный ход, иначе - различная глубина дерева выбора оптимального хода
        if (difficulty == MIN_DIF) {
            move = randomTurn(board);
        } else {
            aiBoard = new AIBoard(board, ai_seed, human_seed);
            move = minimax(DIFF[difficulty], ai_seed, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //aiBoard = null;
            //System.gc();
        }
        float deltaTime = (System.nanoTime() - startTime) * 0.000000001f;
        System.out.printf("time: %f\n", deltaTime);
        return move;
    }

    /**
     * Генерация случайного хода для заданной игровой доски
     * @param board экземпляр класса <code>Board</code>
     * @return      {x, y} - координаты хода ИИ
     */
    private int[] randomTurn(Board board) {
        int x, y;
        do {
            x = RANDOM.nextInt(board.getFieldSizeX());
            y = RANDOM.nextInt(board.getFieldSizeY());
        } while (!board.isEmptyCell(x, y));
        return new int[] {x, y};
    }

    /**
     * Рекурсивный алгоритм выбора оптимального хода.
     *
     *
     * @param depth глубина построения дерева возможных ходов
     * @param seed  индекс текущего игрока (при первом вызове передается всегда <code>ai_seed</code>)
     * @param alpha расчитанный вес последнего хода для ИИ, также используется для отсечки дерева решений
     * @param beta  расчитанный вес последнего хода для игрока, также используется для отсечки дерева решений
     * @return      массив <code>{bestCol, bestRow, (seed == ai_seed) ? alpha : beta}</code>
     *              В конечном итоге, метод возвращает координты оптимального для ИИ хода (bestCol, bestRow)
     */
    private int[] minimax(int depth, byte seed, int alpha, int beta) {
        List<int[]> nextMoves = aiBoard.getPossibleTurns();
        int score;
        int bestCol = -1;
        int bestRow = -1;
        if (nextMoves == null || depth == 0) {
            score = aiBoard.getTurnScore();
            return new int[] {bestCol, bestRow, score};
        } else {
            for (int[] move : nextMoves) {
                aiBoard.makeTurn(move[0], move[1], seed);
                if (seed == ai_seed) {  // (computer) is maximizing player
                    score = minimax(depth - 1, human_seed, alpha, beta)[2];
                    if (score > alpha) {
                        alpha = score;
                        bestCol = move[0];
                        bestRow = move[1];
                    }
                } else {  // (human) is minimizing player
                    score = minimax(depth - 1, ai_seed, alpha, beta)[2];
                    if (score < beta) {
                        beta = score;
                        bestCol = move[0];
                        bestRow = move[1];
                    }
                }
                // undo move
                aiBoard.makeTurn(move[0], move[1], aiBoard.EMPTY_SEED_I);
                if (alpha >= beta) break;
            }
        }
        return new int[] {bestCol, bestRow, (seed == ai_seed) ? alpha : beta};
    }
}
