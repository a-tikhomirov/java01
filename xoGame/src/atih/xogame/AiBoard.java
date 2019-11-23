package atih.xogame;

import java.util.ArrayList; // для возможности использования List<int[]> possibleMoves = new ArrayList<int[]>()
import java.util.List;      // для возможности использования List<int[]>

public class AiBoard extends Board {
    // Необходимые поля родительского класса продублированы для сохранения уровня доступа private
    private byte[][] field;     // массив, содержащий числовое представление игрового поля
    private int fieldSizeX;     // размер поля для игры по горизонтали
    private int fieldSizeY;     // размер поля для игры по вертикали
    private int seedsToWin;     // число символов подряд для выигрыша
    private int turnsCounter;   // число возможных ходов
    private byte winnerIndex;   // 0 - победил первый игрок, 1 - победил второй игрок, -1 - никто не победил
    private int[] lastTurn;     // хранит последний сделанный ход

    private byte ai_seed;       // индекс ИИ - используется для подсчета веса хода
    private byte human_seed;    // индекс игрока - используется для подсчета веса хода
    private int[] koeff;        // массив коэффициентов - используется для подсчета веса хода

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
    public AiBoard(Board board, byte ai_seed, byte human_seed){
        super(board);
        fieldSizeX = super.getFieldSizeX();
        fieldSizeY = super.getFieldSizeY();
        field = super.getField();
        seedsToWin = super.getSeedsToWin();
        winnerIndex = -1;
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
        initKoefficients();
        //copyData(board);
    }

    /**
     * Копирование данных поля для игры доски Board в
     * текущее поле для игры
     * @param board поле для игры доски Board
     */
    public void copyData(Board board, byte[][] field) {
        turnsCounter = board.getTurnsCounter();
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                this.field[i][j] = field[i][j];
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
        List<int[]> possibleMoves = new ArrayList<int[]>();
        for (int i = 0; i < fieldSizeY; ++i) {
            for (int j = 0; j < fieldSizeX; ++j) {
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
     *  В переопределении метода убрано запоминание последнего хода
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
        if (isEmptyCell(x, y)) {
            field[y][x] = player;
            --turnsCounter;             // уменьшение числа возможных ходов
            winnerIndex = getWinner(x, y, player);
            return true;
        } else
            return false;
    }

    public void undoTurn(int x, int y) {
        winnerIndex = -1;
        field[y][x] = EMPTY_SEED_I;
    }
}
