package ru.geekbrains.javaone;

import java.util.ArrayList; // для возможности использования List<int[]> possibleMoves = new ArrayList<int[]>()
import java.util.List;      // для возможности использования List<int[]>

public class AIBoard extends Board {


    // Необходимые поля родительского класса продублированы для сохранения уровня доступа private
    private byte[][] field;     // массив, содержащий числовое представление игрового поля
    private int fieldSizeX;     // размер поля для игры по горизонтали
    private int fieldSizeY;     // размер поля для игры по вертикали
    private int seedsToWin;     // число символов подряд для выигрыша
    private int turnsCounter;   // число возможных ходов
    private byte winnerIndex;   // 0 - победил первый игрок, 1 - победил второй игрок, -1 - никто не победил

    private byte ai_seed;       // индекс ИИ - используется для подсчета веса хода
    private byte human_seed;    // индекс игрока - используется для подсчета веса хода
    private int[] koeff;        // массив коэффициентов - используется для подсчета веса хода
    private int[] lastTurn = new int[2];    // хранит последний сделанный ход

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
        this.lastTurn[0] = board.getLastTurn()[0];
        this.lastTurn[1] = board.getLastTurn()[1];
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
        int lastTurnX = getLastTurn()[0];
        int lastTurnY = getLastTurn()[1];
        int xStart = (lastTurnX - seedsToWin + 1 > 0) ?
                lastTurnX - seedsToWin + 1 : 0;
        int yStart = (lastTurnY - seedsToWin + 1 > 0) ?
                lastTurnY - seedsToWin + 1 : 0;
        int xEnd = (lastTurnX + seedsToWin < fieldSizeX) ?
                lastTurnX + seedsToWin - 1 : fieldSizeX - 1;
        int yEnd = (lastTurnY + seedsToWin < fieldSizeY) ?
                lastTurnY + seedsToWin - 1 : fieldSizeY - 1;
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
     * Возвращает значение веса ситуации на доске
     * @return  значение веса ситуации на доске
     */
    public int getScore() {
        int[] scores = {0, 0};
        byte oppSeed = 1;                               // индекс противоположного игрока для первой итерации цикла k
        for (int k = 0; k < 2; ++k) {                   // k меняется от 0 - индекс 1 игрока, до 1 - индекс 2 игрока
            for (int i = 0; i < fieldSizeY; ++i) {      // i - координата ячейкм по вертикали
                for (int j = 0; j < fieldSizeX; ++j) {  // j - координата ячейкм по горизонтали
                    for (int m = 0; m < DIRS.length; ++m)   // перебор массива направлений
                        scores[k] += getLineScore(j, i, DIRS[m][0], DIRS[m][1], seedsToWin, (byte)k, oppSeed);
                }
            }
            oppSeed = 0;                                // индекс противоположного игрока для второй итерации цикла k
        }
        return scores[ai_seed] - scores[human_seed];    // вес ситуации на всей доске
    }

    /**
     * Расчет веса комбинации заданного отрезка для игрока с индексом seed
     * @param x         координата начала отрезка по горизонтали
     * @param y         координата начала отрезка по вертикали
     * @param dirX      множитель смещения отрезка по горизонтали
     * @param dirY      множитель смещения отрезка по вертикали
     * @param lienLen   длина отрезка
     * @param seed      индекс игрока для расчета веса комбинации
     * @param oppSeed   индекс противополжного игрока для прекращения расчета,
     *                  т.к. наличие на отрезке хода противоположного игрока значит что
     *                  эта комбинация бесперспективна (не приведет к выигрышу)
     * @return          знаенчие веса комбинации заданного отрезка для заданного игрока
     */
    private int getLineScore(int x, int y, int dirX, int dirY, int lienLen, byte seed, byte oppSeed) {
        int lengthX = x + (lienLen - 1) * dirX;
        int lengthY = y + (lienLen - 1) * dirY;
        if (!isValidCoords(lengthX, lengthY)) {
            return 0;
        }
        int score = 0;
        for (int i = 0; i < lienLen; ++i) {
            if (field[y + i * dirY][x + i * dirX] == seed) ++score;
            if (field[y + i * dirY][x + i * dirX] == oppSeed) {
                score = 0;
                break;
            }
        }
        return (score == 0) ? 0 : countScore(score);
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

    /**
     *  Переопределнный метод родителя.
     *  В переопределении метода добавлена отмена хода
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
                winnerIndex = getWinner(x, y,  player);
            }
            return true;
        } else
            return false;
    }
}
