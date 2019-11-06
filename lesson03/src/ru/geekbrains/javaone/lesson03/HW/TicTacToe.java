package ru.geekbrains.javaone.lesson03.HW;

import java.util.Random;    // использвуется для генерации псевдослучайных значений
import java.util.Scanner;   // используется для считывания пользовательского ввода в консоли

public class TicTacToe {
    private static char[][] field;              // поле для игры
    private static int fieldSizeX;              // размер поля для игры по горизонтали
    private static int fieldSizeY;              // размер поля для игры по вертикали
    private static int symbolsToWin;            // число символов подряд для выигрыша
    private static boolean isHumanTurn = true;  // кому принадлежит текущий ход

    private static final int MIN_SIZE = 3;      // минимальный размер поля по вертикали и горизонтали
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
        for (int i = 0; i < fieldSizeY; i++) {
            System.out.print("|");
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
            aiTurn();
        }
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
        } while (!isValidCell(x, y) || !isSymbolInCell(x, y, DOT_EMPTY));
        field[y][x] = DOT_HUMAN;
    }

    /**
     *   Ход ИИ
     */
    private static void aiTurn() {
        aiRandomTurn();
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
        } while (!isSymbolInCell(x, y, DOT_EMPTY));
        field[y][x] = DOT_AI;
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
    private static boolean checkWin(char symbol) {
        // для проверки выигрыша разобьем игровое поле на квадраты со стороной размером symbolsToWin
        int sqrX = fieldSizeX - symbolsToWin + 1; // число квадратов по горизонтали
        int sqrY = fieldSizeY - symbolsToWin + 1; // число квадратов по вертикали

        // проверка всех возможных квадратов поля на выигрышную комбинацию
        for (int i = 0; i < sqrY; i++) {
            for (int j = 0; j < sqrX; j++) {
                if (checkSqr(j, i, symbolsToWin, symbol)) return true;
            }
        }
        return false;
    }

    /**
     *  Проверка квадрата на выигрышную комбинацию
     *
     *  @param offsetX  сдвиг координаты X левого верхнего улла квадрата
     *                  относительно начала поля (от нуля)
     *  @param offsetY  двиг координаты Y левого верхнего улла квадрата
     *                  относительно начала поля (от нуля)
     *  @param sqrSize  размер стороны квадрата
     *  @param symbol   символ для проверки
     *  @return true - выигрышная комбинация была найдена
     *          false - выигрышная комбинация не была найдена
     */
    private static boolean checkSqr(int offsetX, int offsetY, int sqrSize, char symbol) {
        return checkDiags(offsetX, offsetY, symbolsToWin, symbol) || checkLines(offsetX, offsetY, symbolsToWin, symbol);
    }

    /**
     *  Проверка диагоналей квадрата на выигрышную комбинацию
     */
    private static boolean checkDiags(int offsetX, int offsetY, int sqrSize, char symbol) {
        boolean diag1 = true;   // инициализация проверки диагонали с левого верхнего угла
        boolean diag2 = true;   // инициализация проверки диагонали с левого нижнего угла
        for (int i = 0; i < sqrSize; i++)
        {
            diag1 &= isSymbolInCell(i + offsetX, i + offsetY, symbol);
            diag2 &= isSymbolInCell(sqrSize - i - 1 + offsetX, i + offsetY, symbol);
            if (!diag1 && !diag2) break;
        }
        return diag1 || diag2;
    }

    /**
     *  Проверка строк и столбцов квадрата на выигрышную комбинацию
     */
    private static boolean checkLines(int offsetX, int offsetY, int sqrSize, char symbol) {
        boolean col, row;       // переменные, которые хранят проверку столбца, колонки
        for (int i = 0; i < sqrSize; i++) {
            col = true;
            row = true;
            for (int j = 0; j < sqrSize; j++) {
                col &= isSymbolInCell(i + offsetX, j + offsetY, symbol);
                row &= isSymbolInCell(j + offsetX, i + offsetY, symbol);
                if (!col && !row) break;
            }
            if (col || row) return true;
        }
        return false;
    }

    /**
     *   Проверка на ничью
     */
    private static boolean isDraw() {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isSymbolInCell(i, j, DOT_EMPTY))
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
     *   @param  x   координата поля по горизонтали
     *   @param  y   координата поля по вертикали
     *   @param  symbol   символ для проверки
     */
    private static boolean isSymbolInCell(int x, int y, char symbol) {
        return field[y][x] == symbol;
    }

    // Точка входа в программу
    public static void main(String[] args) {
        initGameParams();
        initField();
        showField();
        while (true) {
            nextTurn(isHumanTurn);
            showField();
            if (checkWin((isHumanTurn) ? DOT_HUMAN : DOT_AI)) {
                System.out.printf("%s win!\n", ((isHumanTurn) ? "Human" : "AI"));
                break;
            }
            if (isDraw()) {
                System.out.println("Draw!");
                break;
            }
            isHumanTurn = !isHumanTurn;
        }
    }
}