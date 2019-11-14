package ru.geekbrains.javaone;

public class AI {
    private static byte ai_seed;
    private static byte human_seed;
    private static AIBoard aiBoard;
    private static int depth = 2;
    private static int lastScore;

    public AI(byte ai_seed, byte human_seed) {
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
    }

    public int[] getXY(Board board) {
        aiBoard = new AIBoard(board, ai_seed, human_seed);
        int[] move = minimax(depth, ai_seed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return move;
    }

    private static int[] minimax(int depth, byte seed, int alpha, int beta) {
        int[][] nextMoves = aiBoard.getPossibleTurns();

        // mySeed is maximizing; while oppSeed is minimizing
        //int bestScore = (symbol == DOT_AI) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves == null || depth == 0) {
            // Gameover or depth reached, evaluate score
            score = lastScore;
            return new int[] {score, bestRow, bestCol};
        } else {
            for (int[] move : nextMoves) {
                // try this move for the current "player"
                aiBoard.makeTurn(move[0], move[1], seed);
                if (seed == ai_seed) {  // mySeed (computer) is maximizing player
                    score = minimax(depth - 1, human_seed, alpha, beta)[0];
                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[1];
                        bestCol = move[0];
                        lastScore = aiBoard.getScore(move[0], move[1]);
                    }
                } else {  // oppSeed is minimizing player
                    score = minimax(depth - 1, ai_seed, alpha, beta)[0];
                    if (score < beta) {
                        beta = score;
                        bestRow = move[1];
                        bestCol = move[0];
                        lastScore = aiBoard.getScore(move[0], move[1]);
                    }
                }
                // undo move
                aiBoard.undoTurn(move[0], move[1]);
                if (alpha >= beta) break;
            }
        }
        return new int[] {(seed == ai_seed) ? alpha : beta, bestRow, bestCol};
    }
}
