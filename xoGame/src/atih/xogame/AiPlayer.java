package atih.xogame;

import java.util.Random;    // используется для генерации псевдослучайных значений
import java.util.List;      // для возможности использования List<int[]>

public class AiPlayer {
    private static final Random RANDOM = new Random();  // используется для генерации псевдослучайных значений
    private static final int[] DIFF = {0, 1, 2};  // массив обозначающий сложность алг-ма выбора оптимального хода

    public static final int MIN_DIF = 0;                // минимальная сложность ИИ
    public static final int MED_DIF = 1;
    public static final int MAX_DIF = DIFF.length - 1;  // максимальная сложность ИИ
    public static final int DEF_DIF = 2;                // сложность ИИ по умолчанию

    private byte mySeed;        // индекс ИИ - используется для подсчета веса хода
    private byte oppSeed;       // индекс игрока - используется для подсчета веса хода
    private int difficulty;     // сложность алг-ма выбора оптимального хода (индекс элемента массива DIFF)

    private AiBoard aiBoard;    // экспериментальная игровая доска для построения дерева возможных ходов и выбора оптимального

    /**
     * В случае вызова конструктора без параметра Сложность,
     * сложность задается по умолчанию <code>difficulty = DEF_DIF</code>
     * @param ai_seed       индекс ИИ для игры
     * @param human_seed    индекс игрока
     */
    public AiPlayer(Board board, byte ai_seed, byte human_seed) {
        this(board, ai_seed, human_seed, DEF_DIF);
    }

    /**
     * Конструктор класса ИИ. Класс создан для выбора хода ИИ в игре крестики-нолики
     * @param mySeed        индекс ИИ для игры
     * @param oppSeed       индекс игрока
     * @param difficulty    сложность ИИ, принимает значения
     *                      от <code>AIPlayer.MIN_DIF</code> до <code>AIPlayer.MAX_DIF</code>
     */
    public AiPlayer(Board board, byte mySeed, byte oppSeed, int difficulty) {
        aiBoard = new AiBoard(board, mySeed, oppSeed);
        this.mySeed = mySeed;
        this.oppSeed = oppSeed;
        this.difficulty = difficulty;
    }

    /**
     * Метод возвращает координаты хода ИИ для заданной игровой доски
     * @param board экземпляр класса <code>Board</code>
     * @return      {x, y} - координаты хода ИИ
     */
    public int[] getXY(Board board) {
//        long startTime = System.nanoTime();
        int[] move;
        // если сложность нулевая - случайный ход, иначе - различная глубина дерева выбора оптимального хода
        if (difficulty == MIN_DIF) {
            move = randomTurn(board);
        } else if (difficulty == MED_DIF) {
            aiBoard.copyData(board, board.getField());
            move = aiBoard.isAboutToWin(mySeed);
            if (move[0] != -1) return move;
            move = aiBoard.isAboutToWin(oppSeed);
            if (move[0] != -1) return move;
            move = randomTurn(board);
        } else  {
            aiBoard.copyData(board, board.getField());
            move = minimax(DIFF[difficulty], mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
//        float deltaTime = (System.nanoTime() - startTime) * 0.000000001f;
//        System.out.printf("time: %f\n", deltaTime);
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
     * Рекурсивный алгоритм выбора оптимального хода. ИИ на эксперименатальной доске
     * поочередно делает ходы (сначал за себя, потом за игрока) до того момента, пока не
     * на доске не образуется выигрышная комбинация/ничья или не будет достигнута глубина
     * расчета дерева ходов.
     *
     * Суть алгоритма в том, что для хода "за себя" АИ выбирает ход, который приводит к максимальному
     * показателю веса, при ходе за игрока - ход, который приводит к минимальному показателю веса.
     * Это обусловлено тем, что вес рассчитывается приближенно как
     * "комбинация АИ" * "коэффициент комбинации" - "комбинация игрока" * "коэффициент комбинации" -
     * т.е. если вес большой - ситуация на доске выигрышная для ИИ, вес маленький - выигрышная для игрока.
     *
     * Дерево просчитывается не полностью - некоторые его ветви могут отсекаться в зависимости
     * от рассчитанных весов ситуации на доске (компьютер считает, что "игрок" будет делать максимально
     * невыгодные для "ИИ" ходы - если будет начата ветвь, которая будет приводить к показателям веса
     * для ИИ выше расчитнных ранее - то расчет этой ветви будет прекращен, т.к. подразумевается, что игрок
     * не будет давать шанса ИИ выиграть)
     *
     * @param depth глубина построения дерева возможных ходов
     * @param seed  индекс текущего игрока (при первом вызове передается всегда <code>ai_seed</code>)
     * @param alpha расчитанный вес ситуции на доске для ИИ, также используется для отсечки дерева решений
     * @param beta  расчитанный вес ситуции на доске для игрока, также используется для отсечки дерева решений
     * @return      массив <code>{координата лучшего хода по X, координата лучшего хода по Y, вес ситуации на доске}</code>
     */
    private int[] minimax(int depth, byte seed, int alpha, int beta) {
        // получение списка возможных ходов в окрестностях последнего хода
        List<int[]> nextMoves = aiBoard.getPossibleTurns();

        int score;          // переменная для хранения рассчитанного значения веса
        int bestCol = -1;   // переменная для хранения лучшей координаты по x
        int bestRow = -1;   // переменная для хранения лучшей координаты по y

        // nextMoves == null в том случае, если на доске есть выигрыш или нет доступных ходов
        if (nextMoves == null || depth == 0) {
            score = aiBoard.getScore(); // посчитать вес ситуации на доске
            return new int[] {bestCol, bestRow, score};
        } else {
            for (int[] move : nextMoves) {
                // очередной ход за игрока с индексом seed
                aiBoard.makeTurn(move[0], move[1], seed);
                if (seed == mySeed) {
                    // рекурсивный вызов метода для имитации возможожных ходов игрока
                    score = minimax(depth - 1, oppSeed, alpha, beta)[2];
                    // если индекс текущего игрока - это индекс ИИ - выбираеются ходы,
                    // которые приводят к наибольшему показателю веса ситуации на доске
                    if (score > alpha) {
                        alpha = score;
                        bestCol = move[0];
                        bestRow = move[1];
                    }
                } else {
                    // рекурсивный вызов метода для имитации возможожных ходов ИИ
                    score = minimax(depth - 1, mySeed, alpha, beta)[2];
                    // если индекс текущего игрока - это индекс игрока - выбираеются ходы,
                    // которые приводят к наименьшему показателю веса ситуации на доске
                    if (score < beta) {
                        beta = score;
                        bestCol = move[0];
                        bestRow = move[1];
                    }
                }
                // отмена очередного провернного ход, для экперимента со следующим из списка возможных ходов
                aiBoard.undoTurn(move[0], move[1]);
                // если лучший показатель веса ситуации на доске для ИИ превышает или равен таковому для игрока
                // то расчет этой ветви прекращается (т.к. получается что эта ветвь потенциально лучше для ИИ, а игрок
                // не будет давать возможность такую ветвь выбрать, т.е. будет мешать наилучгим ходам ИИ и выбирать лучшие для себя)
                if (alpha >= beta) break;
            }
        }
        // имитация всех ходов из получаенного списка возможных завершена - можно вовращать результаты
        return new int[] {bestCol, bestRow, (seed == mySeed) ? alpha : beta};
    }
}
