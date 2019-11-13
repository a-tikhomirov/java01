package ru.geekbrains.javaone;

import java.util.Random;    // использвуется для генерации псевдослучайных значений
import java.util.Scanner;   // используется для считывания пользовательского ввода в консоли

public class TicTacToe {
    private static final Scanner SCANNER = new Scanner(System.in);  // используется для считывания пользовательского ввода в консоли
    private static final Random RANDOM = new Random();              // используется для генерации псевдослучайных значений
    private static final String HUMAN = "Human";
    private static final String AI = "AI";

    private static final int ANSWER_YES = 1;    // ответ для продолжения игры
    private static final int ANSWER_NO = 0;     // ответ для завершения игры

    private static Board gameBoard;             // поле для игры в крестики нолики
    private static boolean isHumanTurn;         // true - если текущий ход игрока, false - текущий ход ИИ
    private static byte humanIndex;
    private static byte aiIndex;
    private static String[] players;

    /**
     *  Инициализация игровых параметров
     */
    private static void initGameParams() {
        initField();
        initSeedsToWin();
        initTurnsOrder();
    }

    /**
     *  Инициализация размера поля для игры - запрос ввода
     */
    private static void initField() {
        String str;
        String[] input;
        int x, y;
        do {
            System.out.printf("Введите размеры поля по X и по Y (не менее %d) " +
                    "через пробел или нажмите Enter для значений по умолчанию (3 на 3)>>> ", Board.getMinSize());
            str = SCANNER.nextLine();
            if (str.equalsIgnoreCase("")) {
                x = -1;
                y = -1;
                break;
            }
            input = str.split(" ");
            x = Integer.parseInt(input[0]);
            y = Integer.parseInt(input[1]);
        } while (!Board.isValidSize(x, y));
        if (x == -1) {
            gameBoard = new Board();
        } else {
            gameBoard = new Board(x, y);
        }
    }

    /**
     *  Инициализация числа символов подряд для выигрыша - запрос ввода
     */
    private static void initSeedsToWin() {
        String str;
        int seedsToWin;
        do {
            System.out.printf("Введите необходимое для победы число символов идущих подряд " +
                            "(не менее %d и не более %d) или нажмите Enter для значения по умолчанию (%d)>>> ",
                    gameBoard.getMinSeedsCount(), gameBoard.getMaxSeedsCount(), gameBoard.getMaxSeedsCount());
            str = SCANNER.nextLine();
            if (str.equalsIgnoreCase("")) {
                seedsToWin = -1;
                break;
            }
            seedsToWin = Integer.parseInt(str);
        } while (!gameBoard.setSeedsToWin(seedsToWin));
        if (seedsToWin == -1) {
            gameBoard.setSeedsToWin();
        } else {
            gameBoard.setSeedsToWin(seedsToWin);
        }
    }

    private static void initTurnsOrder() {
        String str;
        int order;
        do {
            System.out.printf("Выберите порядок ходов (%d - первый ход игрока, %d - первый ход ИИ) " +
                            "или нажмите Enter для значения по умолчанию (%s)>>> ",
                    Board.P1_SEED_I, Board.P2_SEED_I, HUMAN);
            str = SCANNER.nextLine();
            if (str.equalsIgnoreCase("")) {
                order = -1;
                break;
            }
            order = Integer.parseInt(str);
        } while (order != Board.P1_SEED_I && order != Board.P2_SEED_I);
        if (order == -1 || order == Board.P1_SEED_I) {
            players = new String[] {HUMAN, AI};
            isHumanTurn = true;
            humanIndex = Board.P1_SEED_I;
            aiIndex = Board.P2_SEED_I;
        } else {
            players = new String[] {AI, HUMAN};
            isHumanTurn = false;
            humanIndex = Board.P2_SEED_I;
            aiIndex = Board.P1_SEED_I;
        }
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
            System.out.printf("Введите координаты X (от 1 до %d) и Y (от 1 до %d) через пробел>>> ", gameBoard.getFieldSizeX(), gameBoard.getFieldSizeY());
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
            SCANNER.nextLine();
        } while (!gameBoard.makeTurn(x, y, humanIndex));
    }

    /**
     *   Ход ИИ
     */
    private static void aiTurn() {
        int x;
        int y;
        do {
            x = RANDOM.nextInt(gameBoard.getFieldSizeX());
            y = RANDOM.nextInt(gameBoard.getFieldSizeY());
        } while (!gameBoard.makeTurn(x, y, aiIndex));
    }

    private static boolean promptToContinue() {
        int answer = -1;
        do {
            System.out.printf("Желает сыграть еще раз? (%d - Да или %d - Нет)>>> ", ANSWER_YES, ANSWER_NO);
            answer = SCANNER.nextInt();
            SCANNER.nextLine();
        } while (!isValidAnswer(answer));
        isHumanTurn = true;
        return answer == ANSWER_YES;
    }

    private static boolean isValidAnswer(int answer) {
        return answer == ANSWER_NO || answer == ANSWER_YES;
    }

    public static void main(String[] args) {
        int winnerIndex;
        while (true) {
            initGameParams();
            System.out.println(gameBoard.toStringBS());
            while (true) {
                nextTurn(isHumanTurn);
                System.out.println(gameBoard.toStringBS());
                //gameBoard.makeTurn(x, y, Board.P1_SEED_I);
                winnerIndex = gameBoard.getWinnerIndex();
                if (winnerIndex != -1) {
                    System.out.printf("%s wins!\n", players[winnerIndex]);
                    break;
                }
                if (gameBoard.isDraw()) {
                    System.out.println("Draw!");
                    break;
                }
                isHumanTurn = !isHumanTurn;
            }
            if (!promptToContinue()) break;
        }
        //System.out.println(gameBoard.getSeedsToWin());
    }
}
