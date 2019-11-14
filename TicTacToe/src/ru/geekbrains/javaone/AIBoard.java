package ru.geekbrains.javaone;

public class AIBoard extends Board {
    private static final int K_MAX = 1000;
    private static final int K_MID = 100;
    private static final int K_LOW = 10;

    //public int score;
    private byte ai_seed;
    private byte human_seed;
    private int turnScore;

    public AIBoard(Board board, byte ai_seed, byte human_seed){
        this(board);
        this.ai_seed = ai_seed;
        this.human_seed = human_seed;
    }

    public AIBoard(Board board) {
        super(board.getFieldSizeX(), board.getFieldSizeY());
        this.seedsToWin = board.getSeedsToWin();
        copyField(board.field);
    }

    @Override
    public boolean makeTurn(int x, int y, byte player) {
        if (isEmptyCell(x, y) || player == EMPTY_SEED_I) {
            field[y][x] = player;
            if (player == EMPTY_SEED_I) {
                ++turnsCounter;
            } else {
                --turnsCounter;
            }
            winnerIndex = getWinner(x, y,  player);
            turnScore = getScore(x, y);
            return true;
        } else
            return false;
    }

    private void copyField(byte[][] field) {
        for (int i = 0; i < fieldSizeY; ++i){
            for (int j = 0; j < fieldSizeX; ++j) {
                this.field[i][j] = field[i][j];
                if (this.field[i][j] != EMPTY_SEED_I) --turnsCounter;
            }
        }
    }

    public int[][] getPossibleTurns() {
        int[][] nextMoves = new int[turnsCounter][2]; // allocate List
        int movesCount = 0;

        // If gameover, i.e., no next move
        if (winnerIndex != -1) {
            return null;   // return empty list
        }

        // Search for empty cells and add to the List
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == EMPTY_SEED_I) {
                    nextMoves[movesCount][0] = j;
                    nextMoves[movesCount][1] = i;
                    movesCount++;
                }
            }
        }
        return nextMoves;
    }

    public int getTurnScore() {
        return turnScore;
    }

    private int getScore(int x, int y) {
        boolean xStartLater = x - seedsToWin + 1 > 0;
        boolean yStartLater = y - seedsToWin + 1 > 0;
        boolean xEndEarlier = x + seedsToWin < fieldSizeX;
        boolean yEndEarlier = y + seedsToWin < fieldSizeY;
        int score = 0;
        //System.out.printf("x = %d;\ty = %d;\n", x, y);
        score += getRowScore(x, y, xStartLater, xEndEarlier);
        score += getColScore(x, y, yStartLater, yEndEarlier);
        score += getDiagULScore(x, y, xStartLater && yStartLater, xEndEarlier && yEndEarlier);
        score += getDiagDLScore(x, y, xStartLater && yEndEarlier, xEndEarlier && yStartLater);
        return score;
    }

    private int getScore(int toCount) {
        int score = 0;
        if (toCount == seedsToWin) {
            score += K_MAX;
        } else if (toCount == seedsToWin - 1) {
            score += K_MID;
        } else if (toCount == seedsToWin - 2) {
            score += K_LOW;
        } else if (toCount != 0)
            score++;
        return score;
    }


    private int getRowScore(int x, int y, boolean startLater, boolean endEarlier) {
        int aiScore, humanScore;
        int score = 0;
        int xStart = (startLater) ? x - seedsToWin + 1 : 0;
        int xEnd = (endEarlier) ? x + seedsToWin - 1 : fieldSizeX - 1;
        int possCombos = xEnd - xStart - seedsToWin + 2;
//        System.out.printf("Row: x: %d to %d;\ty: %d;\t" +
//                "Row possible combos = %d\n", xStart, xEnd, y, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            aiScore = 0;
            humanScore = 0;
            for (int j = 0; j < seedsToWin; ++j) {
                if (field[y][j + xStart + i] == ai_seed) ++aiScore;
                if (field[y][j + xStart + i] == human_seed) ++humanScore;
            }
            score = score + getScore(aiScore) - getScore(humanScore);
        }
        return score;
    }

    private int getColScore(int x, int y, boolean startLater, boolean endEarlier) {
        int aiScore, humanScore;
        int score = 0;
        int yStart = (startLater) ? y - seedsToWin + 1 : 0;
        int yEnd = (endEarlier) ? y + seedsToWin - 1 : fieldSizeY - 1;
        int possCombos = yEnd - yStart - seedsToWin + 2;
//        System.out.printf("Col: x: %d;\ty: %d to %d;\t" +
//                "Col possible combos = %d\n", x, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            aiScore = 0;
            humanScore = 0;
            for (int j = 0; j < seedsToWin; ++j) {
                if (field[j + yStart + i][x] == ai_seed) ++aiScore;
                if (field[j + yStart + i][x] == human_seed) ++humanScore;
            }
            score = score + getScore(aiScore) - getScore(humanScore);
        }
        return score;
    }

    private int getDiagULScore(int x, int y, boolean startLater, boolean endEarlier) {
        int aiScore, humanScore;
        int score = 0;
        int xStart = x;
        int yStart = y;
        int xEnd = x;
        int yEnd = y;
        int possCombos;
        if (startLater) {
            xStart = x - seedsToWin + 1;
            yStart = y - seedsToWin + 1;
        } else
            while (xStart != 0 && yStart != 0) {
                --xStart;
                --yStart;
            }
        if (endEarlier) {
            xEnd = x + seedsToWin - 1;
            yEnd = y + seedsToWin - 1;
        } else
            while (xEnd != fieldSizeX - 1 && yEnd != fieldSizeY - 1) {
                ++xEnd;
                ++yEnd;
            }
        possCombos = yEnd - yStart - seedsToWin + 2;
//        System.out.printf("UL Diag: x: %d to %d;\ty: %d " +
//                "to %d;\tDiag possible combos = %d\n",
//                xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            aiScore = 0;
            humanScore = 0;
            for (int j = 0; j < seedsToWin; ++j) {
                if (field[j + yStart + i][j + xStart + i] == ai_seed) ++aiScore;
                if (field[j + yStart + i][j + xStart + i] == human_seed) ++humanScore;
            }
            score = score + getScore(aiScore) - getScore(humanScore);
        }
        return score;
    }

    private int getDiagDLScore(int x, int y, boolean startLater, boolean endEarlier) {
        int aiScore, humanScore;
        int score = 0;
        int xStart = x;
        int yStart = y;
        int xEnd = x;
        int yEnd = y;
        int possCombos;
        if (startLater) {
            xStart = x - seedsToWin + 1;
            yStart = y + seedsToWin - 1;
        } else
            while (xStart != 0 && yStart != fieldSizeY - 1) {
                --xStart;
                ++yStart;
            }
        if (endEarlier) {
            xEnd = x + seedsToWin - 1;
            yEnd = y - seedsToWin + 1;
        } else
            while (xEnd != fieldSizeX - 1 && yEnd != 0) {
                ++xEnd;
                --yEnd;
            }
        possCombos = yStart - yEnd - seedsToWin + 2;
//        System.out.printf("DL Diag: x: %d to %d;\ty: %d " +
//                "to %d;\tDiag possible combos = %d\n",
//                xStart, xEnd, yStart, yEnd, possCombos);
        for (int i = 0; i < possCombos; ++i) {
            aiScore = 0;
            humanScore = 0;
            for (int j = 0; j < seedsToWin; ++j) {
                if (field[yStart - i - j][j + xStart + i] == ai_seed) ++aiScore;
                if (field[yStart - i - j][j + xStart + i] == human_seed) ++humanScore;
            }
            score = score + getScore(aiScore) - getScore(humanScore);
        }
        return score;
    }
}
