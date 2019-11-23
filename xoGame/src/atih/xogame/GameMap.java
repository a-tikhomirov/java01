package atih.xogame;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMap extends JPanel {
    public static final int GM_HVA = 0; // режим игры Игрок против ИИ
    public static final int GM_HVH = 1; // режим игры Игрок против Игрока
    public static final int GM_AVA = 2; // режим игры ИИ против ИИ

    private static final int GM_DEF = GM_HVA;
    private static final String DRAW = "DRAW";

    public static final String[][] PLAYERS = {{"HUMAN", "AI"}, {"PLAYER 1", "PLAYER 2"}, {"AI 1", "AI 2"}};

    public static final int MIN_SIZE = Board.MIN_SIZE;
    public static final int MAX_SIZE = Board.MAX_SIZE;

    public static final int MIN_DIFF = AiPlayer.MIN_DIF;
    public static final int MAX_DIFF = AiPlayer.MAX_DIF;
    public static final int DEF_DIF = AiPlayer.DEF_DIF;

    private GameWindow gameWindow;      // ссылка на основное окно для возможности обращения к нему

    private boolean gotDimesions;       // были ли получены размеры этого поля
    private int originalWidth;          // первоначальная ширина окна
    private int originalHeight;         // первоначальная высота окна
    private int cellSize;               // размер ячейки поля для игры

    private Board gameBoard;
    private AiPlayer ai1Player;
    private AiPlayer ai2Player;

    private int mode;                   // режим игры
    private int fieldSizeX;             // число ячеек по горизонтали
    private int fieldSizeY;             // число ячеек по вертикали
    private int winLength;              // число символов подряд для выигрыша
    private int ai1Difficulty;          // сложность игры ИИ 1
    private int ai2Difficulty;          // сложность игры ИИ 2
    private int turnsOrder;             // порядок ходов
    public static final int STRAIGTH_ORDER = 0;
    public static final int REVERSE_ORDER = 1;

    private String[] players;
    private byte[] seeds;

    private boolean isHumanTurn;
    private int currentSeed;
    private int lastSeed;

    private boolean isGameOver;

    private int gameState;
    private final int STATE_NOT_STARTED = 0;
    private final int STATE_STARTED = 1;
    private final int STATE_WIN = 2;
    private final int STATE_DRAW = 3;

    Image imgX = null;
    Image imgO = null;

    GameMap(GameWindow gameWindow) {
        gotDimesions = false;
        this.gameWindow = gameWindow;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.black));
        applySettings(GM_DEF, Board.MIN_SIZE, Board.MIN_SIZE, Board.MIN_SIZE, AiPlayer.DEF_DIF, -1, 0);
        try {
            imgX = ImageIO.read(this.getClass().getResource("/Resources/tic.png"));
            imgO = ImageIO.read(this.getClass().getResource("/Resources/tac.png"));
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
     * Изменение состояния поля по клику мыши
     * @param e
     */
    private void update(MouseEvent e) {
        if (gameState == STATE_STARTED) {
            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;
            if (!nextTurn(x ,y)) return;
            //if (mode == GM_HVA) isHumanTurn = !isHumanTurn;
            if (checkGameOver()) {
                return;
            }
            updateGameInfo();
            repaint();
        }
    }

    private void updateGameInfo() {
        String lastTurn = String.format("Last turn: %s at %d, %d",
                players[lastSeed], gameBoard.getLastTurn()[0] + 1, gameBoard.getLastTurn()[1] + 1);
        String gameInfo = String.format("Current turn: %s, click", players[currentSeed]);
        gameWindow.updateLastTurn(lastTurn);
        gameWindow.updateGameInfo(gameInfo);
    }

    /**
     * Метод вызывается для старта игры
     */
    public void startGame() {
        setGraphic();
        setParams();
        gameWindow.updateGameInfo(players[currentSeed]);
    }

    /**
     * Метод вызывается для остановки игры
     */
    public void stopGame() {
        gameState = STATE_NOT_STARTED;
        isGameOver = false;
    }

    /**
     *   Метод обеспечивает выполнение следующего хода в зависимости от режима игры
     */
    private boolean nextTurn(int x, int y) {
        boolean result = true;
        lastSeed = currentSeed;
        switch (mode) {
            case GM_HVA:
                if (isHumanTurn) {
                    if (!gameBoard.makeTurn(x, y, seeds[currentSeed])) return false;
                    if (checkGameOver()) return false;
                    currentSeed = (currentSeed == 0) ? 1 : 0;
                    lastSeed = currentSeed;
                    result = aiTurn(ai1Player, seeds[currentSeed]);
                    isHumanTurn = true;
                }
                break;
            case GM_HVH:
                if (!gameBoard.makeTurn(x, y, seeds[currentSeed])) return false;
                break;
            case GM_AVA:
                result = (currentSeed == 0) ?
                        aiTurn(ai1Player, seeds[currentSeed]) : aiTurn(ai2Player, seeds[currentSeed]);
                break;
        }
        currentSeed = (currentSeed == 0) ? 1 : 0;
        return result;
    }

    /**
     *   Ход ИИ
     */
    private boolean aiTurn(AiPlayer ai, byte currentSeed) {
        int[] coords = ai.getXY(gameBoard);
        return gameBoard.makeTurn(coords[0], coords[1], currentSeed);
    }

    /**
     * Проверка на окончание игры
     * @return  true - есть выигрыш или ничья
     *          false - игры не окончена
     */
    private boolean checkGameOver() {
        if (gameBoard.getWinnerIndex() != -1) {
            setGameOver(STATE_WIN);
            gameWindow.updateGameInfo(getWinText());
            return true;
        }
        if (gameBoard.isDraw()) {
            setGameOver(STATE_DRAW);
            gameWindow.updateGameInfo(DRAW);
            return true;
        }
        return false;
    }

    /**
     * Установка состояния окончания игры
     * @param gameOverState
     */
    private void setGameOver(int gameOverState) {
        isGameOver = true;
        gameState = gameOverState;
        repaint();
    }

    /**
     * Установка начальных графических параметров/отрисовка линий поля
     */
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

    /**
     * Установка параметров игры в зависимости от выбранного режима
     */
    private void setParams() {
        gameBoard = new Board(fieldSizeX, fieldSizeY, winLength);
        if (mode == GM_HVA) {
            if (turnsOrder == STRAIGTH_ORDER) {
                seeds = new byte[] {Board.P1_SEED_I, Board.P2_SEED_I};
                players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
                isHumanTurn = true;
            } else if (turnsOrder == REVERSE_ORDER) {
                seeds = new byte[] {Board.P2_SEED_I, Board.P1_SEED_I};
                players = new String[] {PLAYERS[mode][REVERSE_ORDER], PLAYERS[mode][STRAIGTH_ORDER]};
                isHumanTurn = false;
            }
            ai1Player = new AiPlayer(gameBoard, seeds[1], seeds[0], ai1Difficulty);
        } else if (mode == GM_HVH) {
            seeds = new byte[] {Board.P1_SEED_I, Board.P2_SEED_I};
            players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
            isHumanTurn = true;
        } else if (mode == GM_AVA) {
            seeds = new byte[] {Board.P1_SEED_I, Board.P2_SEED_I};
            players = new String[] {PLAYERS[mode][STRAIGTH_ORDER], PLAYERS[mode][REVERSE_ORDER]};
            ai1Player = new AiPlayer(gameBoard, seeds[0], seeds[1], ai1Difficulty);
            ai2Player = new AiPlayer(gameBoard, seeds[1], seeds[0], ai2Difficulty);
            isHumanTurn = false;
        } else {
            throw new RuntimeException("Unexpected game mode!");
        }
        currentSeed = 0;
    }

    /**
     * Применение настроек игры
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
        gameWindow.resetInfo();
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
     * Переопределение метода отрисовки компонента
     * Необходимо для рисования содержимого игрового поля
     * @param g the <code>Graphics</code> object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    /**
     * Отрисовка содержимого игрового поля
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
                    drawTicTac(g, imgX, Color.BLUE, x * cellSize, y * cellSize, cellSize);
                }
                if (gameBoard.isSeed(x, y, Board.P2_SEED_I)) {
                    drawTicTac(g, imgO, Color.GREEN, x * cellSize, y * cellSize, cellSize);
                }
            }
        }
        if (isGameOver) {
            showMessageGameOver(g);
        }
    }

    /**
     * Метод рисует заданное изображение или круг заданного цвета по заданным координатам
     * @param g     the <code>Graphics</code> object
     * @param img   изображение для рисования
     * @param color цвет для круга, если изображение не найдено
     * @param posX  координата рисунка по горизонтали
     * @param posy  координата рисунка по вертикали
     * @param size  размер рисунка
     */
    private void drawTicTac(Graphics g, Image img, Color color, int posX, int posy, int size) {
        int padding = 5;
        if (img != null) {
            g.drawImage(img, posX+ padding, posy + padding, size - padding*2, size-padding*2, null);
        } else {
            g.setColor(color);
            g.fillOval(posX + padding,posy + padding,size - padding * 2,size - padding * 2);
        }
    }

    /**
     * Метод отображает результат игры (выигрыш/ничья)
     * @param g the <code>Graphics</code> object
     */
    private void showMessageGameOver(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        int size = getWidth() > getHeight() ? getHeight() / 8 : getWidth() / 8;
        g.setColor(Color.GREEN);
        Font font = new Font("Times new roman", Font.BOLD, size);
        switch (gameState) {
            case STATE_DRAW:
                drawCenteredString(g, DRAW, g.getClipBounds(), font);
                break;
            case STATE_WIN:
                drawCenteredString(g, getWinText(), g.getClipBounds(), font);
                break;
            default:
                throw new RuntimeException("Unexpected gameOver state: " + gameState);
        }
    }

    private String getWinText() {
        return players[gameBoard.getWinnerIndex()] + " WINS";
    }

    /**
     * Метод рисует текст посередине заданной области
     * @param g     the <code>Graphics</code> object
     * @param text  текст для рисования
     * @param rect  область внутри которой будует нарисован текст
     * @param font  шрифт текста
     */
    private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Получаем метрику шрифта
        FontMetrics metrics = g.getFontMetrics(font);
        // Получаем координату x текста
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Получаем координату y текста
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Устанавливаем шрифт
        g.setFont(font);
        // Рисуем текст
        g.drawString(text, x, y);
    }
}