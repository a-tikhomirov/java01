package ru.geekbrains.javaone.lesson03.HW;

import java.util.Random;    // использвуется для генерации псевдослучайных значений
import java.util.Scanner;   // используется для считывания пользовательского ввода в консоли

public class TicTacToe {
    private static char[][] field;              // поле для игры
    private static int fieldSizeX;              // размер поля для игры по горизонтали
    private static int fieldSizeY;              // размер поля для игры по вертикали
    private static boolean isHumanTurn = true;  // кому принадлежит текущий ход

    private static final Scanner SCANNER = new Scanner(System.in);  // используется для считывания пользовательского ввода в консоли
    private static final Random RANDOM = new Random();              // используется для генерации псевдослучайных значений
    private static final char DOT_HUMAN = 'X';  // символ используемый для хода игрока
    private static final char DOT_AI = 'O';     // символ используемый для хода ИИ
    private static final char DOT_EMPTY = '.';  // символ обозначающий пустое поле

    /**
     *  Инициализация игрового поля -
     *  заполнение поля символами <code>DOT_EMPTY</code>
      */
    private static void initField() {
        fieldSizeX = 3;
        fieldSizeY = 3;
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
    *   Проверка на выигрыш
    *
    *   @param c   символ для проверки
    *              DOT_HUMAN - для проверки выигрыша игрока
    *              DOT_AI - для проверки выигрыша ИИ
     */
    private static boolean checkWin(char c) {
        if (field[0][0] == c && field[0][1] == c && field[0][2] == c) return true;
        if (field[1][0] == c && field[1][1] == c && field[1][2] == c) return true;
        if (field[2][0] == c && field[2][1] == c && field[2][2] == c) return true;

        if (field[0][0] == c && field[1][0] == c && field[2][0] == c) return true;
        if (field[0][1] == c && field[1][1] == c && field[2][1] == c) return true;
        if (field[0][2] == c && field[1][2] == c && field[2][2] == c) return true;

        if (field[0][0] == c && field[1][1] == c && field[2][2] == c) return true;
        if (field[0][2] == c && field[1][1] == c && field[2][0] == c) return true;
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
