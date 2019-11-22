package ru.geekbrains.javaone;

import java.util.InputMismatchException;
import java.util.Scanner;   // используется для считывания пользовательского ввода в консоли

public class TicTacToe {
    private static final Scanner SCANNER =
            new Scanner(System.in);  // используется для считывания пользовательского ввода в консоли
    private static final String PROMPT = "или нажмите Enter/любой символ для значения по умполчанию";
    private static final String HUMAN = "Игрок";  // имя игрока, отображается при победе
    private static final String AI = "Компьютер"; // имя ИИ, отображается при победе

    private static final int ANSWER_YES = 1;    // ответ для продолжения игры
    private static final int ANSWER_NO = 0;     // ответ для завершения игры

    private static Board gameBoard;             // поле для игры в крестики нолики
    private static AIPlayer ai;                 // класс, содержащий методы для поведения ИИ в игре
    private static boolean isHumanTurn;         // true - если текущий ход игрока, false - текущий ход ИИ
    private static byte humanIndex;             // индекс/символ игрока в массиве Board.P_SEED={'X', '0', '.'}
    private static byte aiIndex;                // индекс/символ ИИ в массиве Board.P_SEED={'X', '0', '.'}
    private static String[] players;            // массив для упрощения вывода имени победителя, содержит HUMAN и AI

    /**
     *  Инициализация игровых параметров
     */
    private static void initGameParams() {
        initField();
        initSeedsToWin();
        initTurnsOrder();
        initDifficulty();
    }

    /**
     *  Инициализация размера поля для игры - запрос ввода
     */
    private static void initField() {
        String str;
        String[] input;
        int x, y;
        do {
            System.out.printf("Введите размеры поля по X и по Y (не менее %d) через пробел %s (%d на %d)>>> ",
                    Board.MIN_SIZE, PROMPT, Board.MIN_SIZE, Board.MIN_SIZE);
            str = SCANNER.nextLine();
            if (str.equalsIgnoreCase("")) {
                gameBoard = new Board();
                return;
            }
            try {
                input = str.split(" ");
                x = Integer.parseInt(input[0]);
                y = Integer.parseInt(input[1]);
            } catch (NumberFormatException e) {
                gameBoard = new Board();
                return;
            }

        } while (!Board.isValidSize(x, y));
        gameBoard = new Board(x, y);
    }

    /**
     *  Инициализация числа символов подряд для выигрыша - запрос ввода
     */
    private static void initSeedsToWin() {
        int seedsToWin;
        do {
            System.out.printf("Введите необходимое для победы число символов идущих подряд " +
                            "(не менее %d и не более %d) %s (%d)>>> ", Board.MIN_SIZE,
                    gameBoard.getMaxSeedsCount(), PROMPT, gameBoard.getMaxSeedsCount());
            seedsToWin = parseToInt(SCANNER.nextLine());
            if (seedsToWin == -1) {
                gameBoard.setSeedsToWin();              // по умолчанию задется максимальное значение
                return;
            }
        } while (!gameBoard.setSeedsToWin(seedsToWin));
    }

    /**
     *  Инициализация порядка ходов, запрос ввода
     */
    private static void initTurnsOrder() {
        int order;
        do {
            System.out.printf("Выберите порядок ходов (%d - первый ход игрока, %d - первый ход ИИ) %s (%s)>>> ",
                    Board.P1_SEED_I, Board.P2_SEED_I, PROMPT, HUMAN);
            order = parseToInt(SCANNER.nextLine());
            if (order == -1) break;
        } while (order != Board.P1_SEED_I && order != Board.P2_SEED_I);
        if (order == -1 || order == Board.P1_SEED_I) {
            players = new String[] {HUMAN, AI};
            isHumanTurn = true;
            humanIndex = Board.P1_SEED_I;           // используется для выполнения ходя на поле Board
            aiIndex = Board.P2_SEED_I;              // используется для выполнения ходя на поле Board
        } else {
            players = new String[] {AI, HUMAN};
            isHumanTurn = false;
            humanIndex = Board.P2_SEED_I;           // используется для выполнения ходя на поле Board
            aiIndex = Board.P1_SEED_I;              // используется для выполнения ходя на поле Board
        }
    }

    /**
     *  Инициализация сложности ИИ, запрос ввода
     *  Сложность 0 - случаные ходы, иные варианты - по возрастающей -
     *  увеличение глубины построения дерева возможных ходов для ИИ
     */
    private static void initDifficulty() {
        int difficulty;
        do {
            System.out.printf("Выберите сложность игры (от %d до %d) %s (%d)>>> ",
                    AIPlayer.MIN_DIF, AIPlayer.MAX_DIF, PROMPT, AIPlayer.DEF_DIF);
            difficulty = parseToInt(SCANNER.nextLine());
            if (difficulty == -1) {
                ai = new AIPlayer(gameBoard, aiIndex, humanIndex);
                return;
            }
        } while (!AIPlayer.isValidDifficluty(difficulty));
        ai = new AIPlayer(gameBoard, aiIndex, humanIndex, difficulty);
    }

    /**
     * Предложение сыграть снова, запрос ввода
     *
     * @return  true    согласие на продолжения игры
     *          false   отказ от продолэения игры
     */
    private static boolean promptToContinue() {
        int answer;
        do {
            System.out.printf("Желает сыграть еще раз? (%d - Да или %d - Нет)>>> ", ANSWER_YES, ANSWER_NO);
            answer = parseToInt(SCANNER.nextLine());
        } while (!isValidAnswer(answer));
        isHumanTurn = true;
        return answer == ANSWER_YES;
    }

    /**
     * Проверка на валидность значения <code>answer</code>
     *
     * @param answer    ответ на запрос продожения
     *                  игры метода <code>promptToContinue()</code>
     * @return  true    ответ валиден
     *          false   ответ не валиден
     */
    private static boolean isValidAnswer(int answer) {
        return answer == ANSWER_NO || answer == ANSWER_YES;
    }

    /**
     * Вспомогательный метод для обработки ввода из консоли
     * @param   str строковое представление ввода
     * @return      числовое представление, если в строке есть число
     *              -1 в случае ошибки преобразования в <code>int</code>
     */
    private static int parseToInt(String str) {
        int result;
        if (str.equalsIgnoreCase(""))
            result = -1;
        else
            try {
                result = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                result = -1;
            }
        return result;
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
            System.out.printf("Введите координаты X (от 1 до %d) и Y (от 1 до %d) через пробел>>> ",
                    gameBoard.getFieldSizeX(), gameBoard.getFieldSizeY());
            try {
                x = SCANNER.nextInt() - 1;
                y = SCANNER.nextInt() - 1;
            } catch (InputMismatchException e) {
                System.out.println("Будьте внимательнее при вводе...");
                x = -1;
                y = -1;
            }
            SCANNER.nextLine();
        } while (!gameBoard.makeTurn(x, y, humanIndex));
    }

    /**
     *   Ход ИИ
     */
    private static void aiTurn() {
        int[] coords = ai.getXY(gameBoard);
        gameBoard.makeTurn(coords[0], coords[1], aiIndex);
    }

    /**
     * Вывод информацмм в консоль о последнем сделанном ходе
     * @param isHuman   true - последний ход приналдежал человеку
     *                  false -  последний ход приналдежал ИИ
     */
    private static void printTurnInfo(boolean isHuman) {
        byte playerIndex = isHuman ? humanIndex : aiIndex;
        int[] lastTurn = gameBoard.getLastTurn();
        System.out.printf("%s ставит %s по координатам: %d, %d\n",
                players[playerIndex], String.valueOf(Board.P_SEED[playerIndex]),
                (lastTurn[0] + 1), lastTurn[1] + 1);
    }

    /**
     *  Точка входа в программу
     *
     * @param args  обработка агрументов не предусмотрена
     */
    public static void main(String[] args) {
        int winnerIndex;
        while (true) {
            initGameParams();
            System.out.println(gameBoard.toStringBS());
            while (true) {
                System.out.printf("Ход %d:\n", gameBoard.getTurnNumber() + 1);
                nextTurn(isHumanTurn);
                printTurnInfo(isHumanTurn);
                //System.out.println(gameBoard.toString());
                System.out.print(gameBoard.toStringBS());
                winnerIndex = gameBoard.getWinnerIndex();
                if (winnerIndex != -1) {
                    System.out.printf("%s выиграл!\n", players[winnerIndex]);
                    break;
                }
                if (gameBoard.isDraw()) {
                    System.out.println("Ничья!");
                    break;
                }
                isHumanTurn = !isHumanTurn;
            }
            if (!promptToContinue()) break;
        }
    }
}
