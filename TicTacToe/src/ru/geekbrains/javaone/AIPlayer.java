package ru.geekbrains.javaone;

import java.util.Random;    // использвуется для генерации псевдослучайных значений

public class AIPlayer {
    private static final Random RANDOM = new Random();              // используется для генерации псевдослучайных значений
    private static final int[] DIFF = {0, 2, 3, 6, 7};

    public static final int MIN_DIF = 0;
    public static final int MAX_DIF = DIFF.length - 1;
    public static final int DEF_DIF = 2;

    private byte ai_seed;
    private byte human_seed;
    private int difficulty;
    private AIBoard aiBoard;

    public AIPlayer(byte ai_seed, byte human_seed) {
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
        this.difficulty = DEF_DIF;
    }

    public AIPlayer(byte ai_seed, byte human_seed, int difficulty) {
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
        this.difficulty = difficulty;
    }

    public static boolean isValidDifficluty(int difficulty) {
        return difficulty >= MIN_DIF && difficulty <= MAX_DIF;
    }

    public int[] getXY(Board board) {
        aiBoard = new AIBoard(board, ai_seed, human_seed);
        int[] move = (difficulty == MIN_DIF) ?
                randomTurn(board) : minimax(DIFF[difficulty], ai_seed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        aiBoard = null;
        //System.gc();
        return move;
    }

    private int[] randomTurn(Board board) {
        int x, y;
        do {
            x = RANDOM.nextInt(board.getFieldSizeX());
            y = RANDOM.nextInt(board.getFieldSizeY());
        } while (!board.isEmptyCell(x, y));
        return new int[] {x, y};
    }

    private int[] minimax(int depth, byte seed, int alpha, int beta) {
        int[][] nextMoves = aiBoard.getPossibleTurns();

        int score;
        int bestCol = -1;
        int bestRow = -1;

        if (nextMoves == null || depth == 0) {
            // Gameover or depth reached, evaluate score
            score = aiBoard.getTurnScore();
            return new int[] {bestCol, bestRow, score};
        } else {
            for (int[] move : nextMoves) {
                // try this move for the current "player"
                aiBoard.makeTurn(move[0], move[1], seed);
                if (seed == ai_seed) {  // mySeed (computer) is maximizing player
                    score = minimax(depth - 1, human_seed, alpha, beta)[2];
                    if (score > alpha) {
                        alpha = score;
                        bestCol = move[0];
                        bestRow = move[1];
                    }
                } else {  // oppSeed is minimizing player
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
