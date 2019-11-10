package ru.geekbrains.javaone.lesson03.HW;

import java.util.Random;    // использвуется для генерации псевдослучайных значений
import java.util.Scanner;   // используется для считывания пользовательского ввода в консоли

public class TicTacToe {
    private static char[][] field;              // поле для игры
    private static char[][] fieldAI;            // поле для алгоритма поиска хода ИИ
    private static int fieldSizeX;              // размер поля для игры по горизонтали
    private static int fieldSizeY;              // размер поля для игры по вертикали
    private static int possibleMoves;           // количество возможных ходов
    private static int symbolsToWin;            // число символов подряд для выигрыша
    private static int gameDifficulty;          // сложность игры (1 - случайные ходы ИИ, 3 - ИИ мешает игроку и старается выиграть)
    private static boolean isHumanTurn = true;  // кому принадлежит текущий ход

    private static final int MIN_SIZE = 3;      // минимальный размер поля по вертикали и горизонтали
    private static final int MID_ALG_DEPTH = 2; // параметра для алгоритма минимакс
    private static final int HIGH_ALG_DEPTH = 5; // параметра для алгоритма минимакс
    private static final int K_MAX = 1000;
    private static final int K_MID = 100;
    private static final int K_LOW = 10;
    private static final int ANSWER_YES = 1;    // ответ для продолжения игры
    private static final int ANSWER_NO = 0;     // ответ для завершения игры

    private static final Scanner SCANNER = new Scanner(System.in);  // используется для считывания пользовательского ввода в консоли
    private static final Random RANDOM = new Random();              // используется для генерации псевдослучайных значений
    private static final char DOT_HUMAN = 'X';  // символ используемый для хода игрока
    private static final char DOT_AI = 'O';     // символ используемый для хода ИИ
    private static final char DOT_EMPTY = '.';  // символ обозначающий пустое поле

    /**
     *  Инициализация игровых параметров
     */
    private static void initGameParams() {
        initFieldSize();
        initSymToWin();
        initGameDifficulty();
    }
    /**
     *  Инициализация размера поля для игры - запрос ввода
     */
    private static void initFieldSize() {
        do {
            System.out.printf("Введите размеры поля по X и по Y (не менее %d)>>> ", MIN_SIZE);
            fieldSizeX = SCANNER.nextInt();
            fieldSizeY = SCANNER.nextInt();
        } while ((fieldSizeX < 3) || (fieldSizeY < 3));
        possibleMoves = fieldSizeX * fieldSizeY;
    }
    /**
     *  Инициализация числа символов подряд для выигрыша - запрос ввода
     */
    private static void initSymToWin() {
        do {
            System.out.printf("Введите необходимое для победы число символов идущих подряд (не менее %d и не более %d)>>> ", MIN_SIZE, ((fieldSizeX < fieldSizeY) ? fieldSizeX : fieldSizeY));
            symbolsToWin = SCANNER.nextInt();
        } while (!isSymbolsToWinValid(symbolsToWin));
    }
    /**
     *  Инициализация сложности игры - запрос ввода
     */
    private static void initGameDifficulty() {
        do {
            System.out.print("Введите сложность игры (от 1 - самый легкий, до 3 - самый сложный)>>> ");
            gameDifficulty = SCANNER.nextInt();
        } while ((gameDifficulty < 1) || (gameDifficulty > 3));
    }

    /**
     *  Инициализация игрового поля -
     *  заполнение поля символами <code>DOT_EMPTY</code>
     */
    private static void initField() {
        field = new char[fieldSizeY][fieldSizeX];
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                field[i][j] = DOT_EMPTY;
            }
        }
    }

    /**
     *   Отрисовка игрового поля в консоли
     */
    private static void showField() {
        System.out.print("  ");
        for (int i = 1; i <= fieldSizeX; i++) System.out.printf(" %d", i);
        System.out.println();
        for (int i = 0; i < fieldSizeY; i++) {
            System.out.printf("%d |", i + 1);
            for (int j = 0; j < fieldSizeX; j++) {
                System.out.print(field[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println("-------");
    }

    /**
     *   Следующий ход, игрок или ИИ
     *
     *   @param isHuman  true - ход предлагается игроку,
     *                   false - ход делает ИИ
     */
    private static void nextTurn(boolean isHuman) {
        if (isHuman) {
            humanTurn();
        } else {
            aiTurn(gameDifficulty);
        }
        possibleMoves--;
    }

    /**
     *   Ход игрока - запрос координат
     */
    private static void humanTurn() {
        int x;
        int y;
        do {
            System.out.printf("Введите координаты X (от 1 до %d) и Y (от 1 до %d) через пробел>>> ", fieldSizeX, fieldSizeY);
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        } while (!isValidCell(x, y) || !isSymbolInCell(field, x, y, DOT_EMPTY));
        field[y][x] = DOT_HUMAN;
    }

    /**
     *   Ход ИИ
     *
     *  @param difficulty   сложность работы ИИ (от 1 до 3)
     */
    private static void aiTurn(int difficulty) {
        switch (difficulty) {
            case 1:
                aiRandomTurn();
                break;
            case 2:
                aiTurnToWin(MID_ALG_DEPTH);
                break;
            case 3:
                aiTurnToWin(HIGH_ALG_DEPTH);
                break;
        }
    }

    /**
     *   ИИ делает случайных ход
     */
    private static void aiRandomTurn() {
        int x;
        int y;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isSymbolInCell(field, x, y, DOT_EMPTY));
        field[y][x] = DOT_AI;
    }

    /**
     *  ИИ делает ход по алгоритму минимакс
     *
     *  @param  algDepth    глубина работы алгоритма ИИ минимакс
     */
    private static void aiTurnToWin(int algDepth) {
        copyField();
        int[] move = minimax(algDepth, DOT_AI, Integer.MIN_VALUE, Integer.MAX_VALUE, possibleMoves);
        field[move[1]][move[2]] = DOT_AI;
    }

    /**
     *  Инициализация копии игрового поля для алгоритма минимакс
     */
    private static void copyField() {
        fieldAI = new char[fieldSizeY][fieldSizeX];
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                fieldAI[i][j] = field[i][j];
            }
        }
    }

    /**
     *  Алгоритм выбора оптимального хода минимакс
     *
     * @param depth     глубина поиска
     * @param symbol    символ для имитации хода
     * @param turnsCount    возможное число ходов
     * @return  возвращает массив <code>[y, x]</code>, где
     *          x - координата хода ИИ по горизонтали
     *          y - координата хода ИИ по вертикали
     */
    private static int[] minimax(int depth, char symbol, int alpha, int beta, int turnsCount) {
        int[][] nextMoves = getPossibleTurns(turnsCount);

        // mySeed is maximizing; while oppSeed is minimizing
        //int bestScore = (symbol == DOT_AI) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves == null || depth == 0) {
            // Gameover or depth reached, evaluate score
            score = getScore(symbolsToWin);
            return new int[] {score, bestRow, bestCol};
        } else {
            for (int[] move : nextMoves) {
                // try this move for the current "player"
                fieldAI[move[1]][move[0]] = symbol;
                turnsCount--;
                if (symbol == DOT_AI) {  // mySeed (computer) is maximizing player
                    score = minimax(depth - 1, DOT_HUMAN, alpha, beta, turnsCount)[0];
                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[1];
                        bestCol = move[0];
                    }
                } else {  // oppSeed is minimizing player
                    score = minimax(depth - 1, DOT_AI, alpha, beta, turnsCount)[0];
                    if (score < beta) {
                        beta = score;
                        bestRow = move[1];
                        bestCol = move[0];
                    }
                }
                // undo move
                fieldAI[move[1]][move[0]] = DOT_EMPTY;
                turnsCount++;
                if (alpha >= beta) break;
            }
        }
        return new int[] {(symbol == DOT_AI) ? alpha : beta, bestRow, bestCol};
    }

    /**
     *  Возвращает массив возможных ходов для тестовго поля ИИ fieldAI
     *
     * @param turnsCount    число возможных ходов
     * @return  массив возможных ходов
     */
    private static int[][] getPossibleTurns(int turnsCount) {
        int[][] nextMoves = new int[turnsCount][2]; // allocate List
        int movesCount = 0;

        // If gameover, i.e., no next move
        if (checkWin(fieldAI, DOT_AI) || checkWin(fieldAI, DOT_HUMAN)) {
            return null;   // return empty list
        }

        // Search for empty cells and add to the List
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isSymbolInCell(fieldAI, j, i, DOT_EMPTY)) {
                    nextMoves[movesCount][0] = j;
                    nextMoves[movesCount][1] = i;
                    movesCount++;
                }
            }
        }
        return nextMoves;
    }

    /**
     *  Метод производит подсчет веса комбинаций на поле fieldAI
     *
     * @param maxSymbols    число символов для выигрышной комбинации
     * @return  результат вычисления веса комбинаций на поле
     */
    private static int getScore(int maxSymbols) {
        int sqrX = fieldSizeX - symbolsToWin + 1; // число квадратов по горизонтали
        int sqrY = fieldSizeY - symbolsToWin + 1; // число квадратов по вертикали
        int score = 0;

        for (int i = 0; i < sqrY; i++) {
            for (int j = 0; j < sqrX; j++) {
                /*for (int k = (maxSymbols - 1); k <= maxSymbols ; k++) {
                    score += 19 * (k - 1) * checkSqr(fieldAI, j, i, k, DOT_AI);
                    score -= 20 * (k - 1) * checkSqr(fieldAI, j, i, k, DOT_HUMAN);
                }*/
                score += checkDiags(fieldAI, j, i, maxSymbols, DOT_AI)[1];
                score += checkLines(fieldAI, j, i, maxSymbols, DOT_AI)[1];
                score -= checkDiags(fieldAI, j, i, maxSymbols, DOT_HUMAN)[1];
                score -= checkLines(fieldAI, j, i, maxSymbols, DOT_HUMAN)[1];
            }
        }
        return score;
    }

    /**
     *  Проверка на выигрыш
     *
     *  @param  symbol  символ для проверки
     *                  DOT_HUMAN - для проверки выигрыша игрока
     *                  DOT_AI - для проверки выигрыша ИИ
     *  @return true - выигрышная комбинация была найдена
     *          false - выигрышная комбинация не была найдена
     */
    private static boolean checkWin(char[][] field, char symbol) {
        // для проверки выигрыша разобьем игровое поле на квадраты со стороной размером symbolsToWin
        int sqrX = fieldSizeX - symbolsToWin + 1; // число квадратов по горизонтали
        int sqrY = fieldSizeY - symbolsToWin + 1; // число квадратов по вертикали

        // проверка всех возможных квадратов поля на выигрышную комбинацию
        for (int i = 0; i < sqrY; i++) {
            for (int j = 0; j < sqrX; j++) {
                if (checkSqr(field, j, i, symbolsToWin, symbol) > 0) return true;
            }
        }
        return false;
    }

    /**
     *  Проверка квадрата на выигрышную комбинацию
     *
     *  @param field    поле для проверки выигрыщной комбинации
     *  @param offsetX  сдвиг координаты X левого верхнего угла квадрата
     *                  относительно начала поля (от нуля)
     *  @param offsetY  сдвиг координаты Y левого верхнего угла квадрата
     *                  относительно начала поля (от нуля)
     *  @param sqrSize  размер стороны квадрата
     *  @param symbol   символ для проверки
     *  @return > 0 - выигрышная комбинация была найдена
     *          = 0 - выигрышная комбинация не была найдена
     */
    private static int checkSqr(char[][] field, int offsetX, int offsetY, int sqrSize, char symbol) {
        return checkDiags(field, offsetX, offsetY, sqrSize, symbol)[0] + checkLines(field, offsetX, offsetY, sqrSize, symbol)[0];
    }

    /**
     *  Проверка диагоналей квадрата на выигрышную комбинацию
     */
    private static int[] checkDiags(char[][] field, int offsetX, int offsetY, int sqrSize, char symbol) {
        boolean diag1 = true;   // инициализация проверки диагонали с левого верхнего угла
        boolean diag2 = true;   // инициализация проверки диагонали с левого нижнего угла
        int d1score = 0;
        int d2score = 0;
        int score = 0;
        for (int i = 0; i < sqrSize; i++)
        {
            if (isSymbolInCell(field,i + offsetX, i + offsetY, symbol)) {
                diag1 &= true;
                d1score++;
            } else
                diag1 &= false;
            if (isSymbolInCell(field,sqrSize - i - 1 + offsetX, i + offsetY, symbol)) {
                diag2 &= true;
                d2score++;
            } else
                diag2 &= false;
        }
        score += getScore(d1score, sqrSize);
        score += getScore(d2score, sqrSize);
        return new int[]{(diag1 || diag2) ? 1 : 0, score};
    }

    /**
     *  Проверка строк и столбцов квадрата на выигрышную комбинацию
     */
    private static int[] checkLines(char[][] field, int offsetX, int offsetY, int sqrSize, char symbol) {
        boolean col, row;       // переменные, которые хранят проверку столбца, колонки
        int win = 0;
        int cScore, rScore;
        int score = 0;
        for (int i = 0; i < sqrSize; i++) {
            col = true;
            row = true;
            cScore = 0;
            rScore = 0;
            for (int j = 0; j < sqrSize; j++) {
                if (isSymbolInCell(field,i + offsetX, j + offsetY, symbol)) {
                    col &= true;
                    cScore++;
                } else
                    col &= false;
                if (isSymbolInCell(field,j + offsetX, i + offsetY, symbol)) {
                    row &= true;
                    rScore++;
                } else
                    row &= false;
            }
            if (col || row) win++;
            score += getScore(cScore, sqrSize);
            score += getScore(rScore, sqrSize);
            //System.out.printf("col = %d; row = %d\n", cScore, rScore);
        }
        //System.out.printf("score = %d\n", score);
        return new int[]{win, score};
    }

    private static int getScore(int toCount, int toCompare) {
        int score = 0;
        if (toCount == toCompare) {
            score += K_MAX;
        } else if (toCount == toCompare - 1) {
            score += K_MID;
        } else if (toCount == toCompare - 2) {
            score += K_LOW;
        } else if (toCount != 0)
            score++;
        return score;
    }

    /**
     *   Проверка на ничью
     *
     *  @param  field   поле для проверки на ничью
     *  @return true    все клектки заданного поля заняты
     *          false   на заданном поле есть свободные клетки
     */
    private static boolean isDraw(char[][] field) {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isSymbolInCell(field, j, i, DOT_EMPTY))
                    return false;
            }
        }
        return true;
    }

    /**
     *   Проверка коррдинат на валидность
     *
     *   @param  x   координата по горизонтали
     *   @param  y   координата по вертикали
     */
    private static boolean isValidCell(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     *   Проверка числа символов для выигрыша на валидность
     *
     *   @param  symbolsNumber   число символов для выигрыша
     */
    private static boolean isSymbolsToWinValid(int symbolsNumber) {
        return symbolsNumber >= MIN_SIZE && ((fieldSizeX > fieldSizeY) ? (symbolsNumber <= fieldSizeY) : (symbolsNumber <= fieldSizeX));
    }

    /**
     *   Проверка клетки поля на определенный символ или пустое поле
     *
     *  @param  field поле для проверки клекти
     *  @param  x   координата поля по горизонтали
     *  @param  y   координата поля по вертикали
     *  @param  symbol   символ для проверки
     *  @return true    в заданной клектке поля <code>field</code>
     *                  есть символ <code>symbol</code>
     */
    private static boolean isSymbolInCell(char[][] field, int x, int y, char symbol) {
        return field[y][x] == symbol;
    }

    private static boolean promptToContinue() {
        int answer = -1;
        do {
            System.out.printf("Желает сыграть еще раз? (%d - Да или %d - Нет)>>> ", ANSWER_YES, ANSWER_NO);
            answer = SCANNER.nextInt();
        } while (!isValidAnswer(answer));
        isHumanTurn = true;
        return answer == ANSWER_YES;
    }

    private static boolean isValidAnswer(int answer) {
        return answer == ANSWER_NO || answer == ANSWER_YES;
    }

    // Точка входа в программу
    public static void main(String[] args) {
        while (true) {
            initGameParams();
            initField();
            showField();
            while (true) {
                nextTurn(isHumanTurn);
                showField();
                if (checkWin(field, (isHumanTurn) ? DOT_HUMAN : DOT_AI)) {
                    System.out.printf("%s win!\n", ((isHumanTurn) ? "Human" : "AI"));
                    break;
                }
                if (isDraw(field)) {
                    System.out.println("Draw!");
                    break;
                }
                isHumanTurn = !isHumanTurn;
            }
            if (!promptToContinue()) break;
        }
    }
}