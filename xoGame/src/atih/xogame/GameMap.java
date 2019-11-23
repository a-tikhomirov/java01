package atih.xogame;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMap extends JPanel {
    public static final int GM_HVA = 0; // режим игры Игрок против ИИ
    public static final int GM_HVH = 1; // режим игры Игрок против Игрока
    public static final int GM_AVA = 2; // режим игры ИИ против ИИ
    public static final int STRAIGTH_ORDER = 0;
    public static final int REVERSE_ORDER = 1;
    private static final int GM_DEF = GM_HVA;

    private static final int DOT_PADDING = 5;

    public static final String[][] PLAYERS = {{"HUMAN", "AI"}, {"PLAYER 1", "PLAYER 2"}, {"AI 1", "AI 2"}};

    public static final int MIN_SIZE = Board.MIN_SIZE;
    public static final int MAX_SIZE = Board.MAX_SIZE;

    public static final int MIN_DIFF = AiPlayer.MIN_DIF;
    public static final int MAX_DIFF = AiPlayer.MAX_DIF;
    public static final int DEF_DIF = AiPlayer.DEF_DIF;

    private GameWindow gameWindow;      // ссылка на основное окно для возможности обращения к нему

    private boolean gotDimesions;
    private int originalWidth;          // первоначальная ширина окна
    private int originalHeight;         // первоначальная высота окна
    private int cellSize;               // размер ячейки поля для игры

    private Board gameBoard;
    private AiPlayer ai1Palyer;
    private AiPlayer ai2Player;

    private int mode;
    private int fieldSizeX;             // число ячеек по горизонтали
    private int fieldSizeY;             // число ячеек по вертикали
    private int winLength;              // число символов подряд для выигрыша
    private int ai1Difficulty;          // сложность игры против ИИ
    private int ai2Difficulty;
    private int turnsOrder;

    private String[] players;
    private byte human1Index;
    private byte human2Index;
    private byte ai1Index;
    private byte ai2Index;
    private boolean isHumanTurn;
    private byte currentSeed;
    private int winnerIndex;

    private boolean isGameOver;

    private int gameState;              //
    private final int STATE_NOT_STARTED = 0;
    private final int STATE_STARTED = 1;
    private final int STATE_WIN = 2;
    private final int STATE_DRAW = 3;

    Image imgX = null;
    Image imgO = null;

    public static int getMaxWinLength(int sizeX, int sizeY) {
        return Board.getMaxSeedsCount(sizeX, sizeY);
    }

    GameMap(GameWindow gameWindow) {
        gotDimesions = false;
        this.gameWindow = gameWindow;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.black));
        applySettings(GM_DEF, Board.MIN_SIZE, Board.MIN_SIZE, Board.MIN_SIZE, AiPlayer.DEF_DIF, -1, 0);
        try {
            imgX = ImageIO.read(Map.class.getResourceAsStream("./src/tic.png"));
            imgO = ImageIO.read(Map.class.getResourceAsStream("./src/tac.png"));
        } catch (IOException | IllegalArgumentException x) {
            System.out.println(x.getMessage());
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                update(e);
            }
        });
    }

    /**
     * Переопределение метода отрисовки компонента
     * Необходимо для рисования линий игрового поля
     * @param g the <code>Graphics</code> object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    public void startGame() {
        setGraphic();
        setParams();
        nextTurn();
    }

    /**
     *   Следующий ход, игрок или ИИ
     */
    private void nextTurn() {
        switch (mode) {
            case GM_HVA:
                if (isHumanTurn) {
                    currentSeed = human1Index;
                } else {
                    aiTurn(ai1Palyer, ai1Index);
                    isHumanTurn = true;
                }
                break;
            case GM_HVH:
                currentSeed = (currentSeed == human1Index) ? human2Index : human1Index;
                break;
            case GM_AVA:
                while (true) {
                    if (currentSeed == ai1Index) {
                        aiTurn(ai1Palyer, ai1Index);
                    } else {
                        aiTurn(ai2Player, ai2Index);
                    }
                    currentSeed = (currentSeed == ai1Index) ? ai2Index : ai1Index;
                    repaint();
                    if (checkGameOver()) {
                        //stopGame();
                        break;
                    }
                }
                break;
        }
        repaint();
    }

    /**
     *   Ход ИИ
     */
    private void aiTurn(AiPlayer ai, byte currentSeed) {
        int[] coords = ai.getXY(gameBoard);
        gameBoard.makeTurn(coords[0], coords[1], currentSeed);
    }

    /**
     * Получение координат клика мыши на поле
     * @param e
     */
    private void update(MouseEvent e) {
        if (gameState == STATE_STARTED) {
            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;
            if (isHumanTurn) gameBoard.makeTurn(x, y, currentSeed);
            //System.out.printf("Click coordinates:\tx: %d;\ty: %d\n", x, y);
            if (mode == GM_HVA) isHumanTurn = !isHumanTurn;
            if (checkGameOver()) {
                return;
            }
            nextTurn();
            if (checkGameOver()) {
                return;
            }
            repaint();
        }
    }

    private boolean checkGameOver() {
        int winnerIndex = gameBoard.getWinnerIndex();
        if (winnerIndex != -1) {
            System.out.printf("%s выиграл!\n", players[winnerIndex]);
            setGameOver(STATE_WIN);
            return true;
        }
        if (gameBoard.isDraw()) {
            System.out.println("Ничья!");
            setGameOver(STATE_DRAW);
            return true;
        }
        return false;
    }

    public void stopGame() {
        gameState = STATE_NOT_STARTED;
        isGameOver = false;
    }

    private void setGraphic() {
        if (!gotDimesions) {
            originalWidth = getWidth();
            originalHeight = getHeight();
            gotDimesions = true;
        }
        gameState = STATE_STARTED;
        initMap();
        repaint();
    }

    private void setParams() {
        gameBoard = new Board(fieldSizeX, fieldSizeY, winLength);
        if (mode == GM_HVA) {
            if (turnsOrder == STRAIGTH_ORDER) {
                human1Index = Board.P1_SEED_I;
                ai1Index = Board.P2_SEED_I;
                players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
                isHumanTurn = true;
            } else if (turnsOrder == REVERSE_ORDER) {
                human1Index = Board.P2_SEED_I;
                ai1Index = Board.P1_SEED_I;
                players = new String[] {PLAYERS[mode][REVERSE_ORDER], PLAYERS[mode][STRAIGTH_ORDER]};
                isHumanTurn = false;
            }
            ai1Palyer = new AiPlayer(gameBoard, ai1Index, human1Index, ai1Difficulty);
        } else if (mode == GM_HVH) {
            human1Index = Board.P1_SEED_I;
            human2Index = Board.P2_SEED_I;
            players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
            isHumanTurn = true;
            currentSeed = human2Index;
        } else if (mode == GM_AVA) {
            ai1Index = Board.P1_SEED_I;
            ai2Index = Board.P2_SEED_I;
            players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
            ai1Palyer = new AiPlayer(gameBoard, ai1Index, ai2Index, ai1Difficulty);
            ai1Palyer = new AiPlayer(gameBoard, ai2Index, ai1Index, ai2Difficulty);
            isHumanTurn = false;
            currentSeed = ai2Index;
        } else {
            throw new RuntimeException("Unexpected game mode!");
        }
    }

    /**
     * ...
     * @param mode          режим игры
     * @param fieldSizeX    число ячеек по горизонтали
     * @param fieldSizeY    число ячеек по вертикали
     * @param winLength     число символов подряд для выигрыша
     * @param ai1Difficulty сложность игры ИИ 1
     * @param ai2Difficulty сложность игры ИИ 2
     * @param turnsOrder    порядок ходов
     */
    public void applySettings(int mode, int fieldSizeX, int fieldSizeY, int winLength, int ai1Difficulty, int ai2Difficulty, int turnsOrder) {
        this.mode = mode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        this.ai1Difficulty = ai1Difficulty;
        this.ai2Difficulty = ai2Difficulty;
        this.turnsOrder = turnsOrder;
        gameState = STATE_NOT_STARTED;
        isGameOver = false;
    }

    /**
     * Инициализация размеров ячеек игрового поля
     */
    private void initMap() {
        int cellWidth = originalWidth / fieldSizeX;
        int cellHeigth = originalHeight / fieldSizeY;
        // Если размеры ячеек по вертикали/горизонтали не совпадают - необходимо
        // изменить размеры основного окна, чтобы было красиво)
        cellSize = (cellWidth > cellHeigth) ? cellHeigth : cellWidth;
        gameWindow.reSize(cellSize*fieldSizeX + gameWindow.W_DELTA,cellSize*fieldSizeY + gameWindow.H_DELTA);
    }

    /**
     * Отрисовка линий игрового поля
     * @param g the <code>Graphics</code> object
     */
    private void render(Graphics g) {
        // данный метод вызывается во время отрисовки основного окна, поэтому
        // добавлена переменная для контроля необходимости отрисовки линиий поля
        if (gameState == STATE_NOT_STARTED) return;
        g.setColor(Color.BLACK);
        for (int i = 1; i < fieldSizeY; ++i) {
            int y = i * cellSize;
            g.drawLine(0,y, getWidth(), y);
        }
        for (int i = 1; i < fieldSizeX; ++i) {
            int x = i * cellSize;
            g.drawLine(x,0, x, getHeight());
        }
        for (int y = 0; y < fieldSizeY; ++y) {
            for (int x = 0; x < fieldSizeX; ++x) {
                if (gameBoard.isSeed(x, y, Board.P1_SEED_I)) {
                    if (imgX != null) {
                        g.drawImage(imgX, x * cellSize, y * cellSize, null);
                    } else {
                        g.setColor(Color.BLUE);
                        g.fillOval(x * cellSize + DOT_PADDING,
                                y * cellSize + DOT_PADDING,
                                cellSize - DOT_PADDING * 2,
                                cellSize - DOT_PADDING * 2);
                    }
                }
                if (gameBoard.isSeed(x, y, Board.P2_SEED_I)) {
                    if (imgO != null) {
                        g.drawImage(imgO, x * cellSize, y * cellSize, null);
                    } else {
                        g.setColor(new Color(255, 1, 1));
                        g.fillOval(x * cellSize + DOT_PADDING,
                                y * cellSize + DOT_PADDING,
                                cellSize - DOT_PADDING * 2,
                                cellSize - DOT_PADDING * 2);
                    }
                }
            }
        }
        if (isGameOver) {
            showMessageGameOver(g);
        }
    }

    private void setGameOver(int gameOverState) {
        isGameOver = true;
        gameState = gameOverState;
        repaint();
    }

    private void showMessageGameOver(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 200, getWidth(), 70);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Times new roman", Font.BOLD, 48));
        switch (gameState) {
            case STATE_DRAW:
                g.drawString("DRAW", 180, getHeight() / 2);
                break;
            case STATE_WIN:
                g.drawString(players[gameBoard.getWinnerIndex()] + " WINS", 20, getHeight() / 2);
                break;
            default:
                throw new RuntimeException("Unexpected gameOver state: " + gameState);
        }
    }
}