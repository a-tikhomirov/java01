package atih.xogame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMap extends JPanel {
    public static final int GM_HVA = 0; // режим игры Игрок против ИИ
    public static final int GM_HVH = 1; // режим игры Игрок против Игрока

    private GameWindow gameWindow;      // ссылка на основное окно для возможности обращения к нему

    private int originalWidth;          // первоначальная ширина окна
    private int originalHeight;         // первоначальная высота окна
    private int cellSize;               // размер ячейки поля для игры

    private int fieldSizeX;             // число ячеек по горизонтали
    private int fieldSizeY;             // число ячеек по вертикали
    private int winLength;              // число символов подряд для выигрыша
    private int difficulty;             // сложность игры против ИИ

    private boolean isIntialized = false;   // были ли инициализированы необходимые для отрисовки поля

    GameMap(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setBackground(Color.GRAY);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                getClickCoords(e);
            }
        });
    }

    /**
     * Получение координат клика мыши на поле
     * @param e
     */
    private void getClickCoords(MouseEvent e) {
        if (isIntialized) {
            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;
            System.out.printf("Click coordinates:\tx: %d;\ty: %d\n", x, y);
        }
    }

    /**
     * Переопределение метода отрисовки компонента
     * Необходимо для рисования линий игрового поля
     * @param g the <code>Graphics</code> object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
    }

    /**
     * Пока только инициализация игровых параметров
     * @param mode          режим игры
     * @param fieldSizeX    число ячеек по горизонтали
     * @param fieldSizeY    число ячеек по вертикали
     * @param winLength     число символов подряд для выигрыша
     * @param difficulty    сложность игры против ИИ
     */
    public void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength, int difficulty) {
        System.out.printf("mode = %d\nfieldSizeX = %d\nfieldSizeY = %d\nwinLength = %d\ndifficulty = %d\n\n",
                mode, fieldSizeX, fieldSizeY, winLength, difficulty);
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        this.difficulty = difficulty;
        initMap();
        isIntialized = true;
        repaint();
        // добавить методы для обеспечения игры
    }

    /**
     * Инициализация размеров ячеек игрового поля
     */
    private void initMap() {
        int cellWidth = originalWidth / fieldSizeX;
        int cellHeigth = originalHeight / fieldSizeY;
        // Если размеры ячеек по вертикали/горизонтали не совпадают - необходимо
        // изменить размеры основного окна, чтобы было красиво)
        if (cellWidth > cellHeigth) {
            gameWindow.reSize((cellHeigth - cellWidth)*fieldSizeX, 0);
            cellSize = cellHeigth;
        } else if (cellHeigth > cellWidth) {
            gameWindow.reSize(0, (cellWidth - cellHeigth)*fieldSizeY);
            cellSize = cellWidth;
        } else
            cellSize = cellWidth;
    }

    /**
     * Отрисовка линий игрового поля
     * @param g the <code>Graphics</code> object
     */
    private void drawMap(Graphics g) {
        // данный метод вызывается во время отрисовки основного окна, поэтому
        // добавлена переменная для контроля необходимости отрисовки линиий поля
        if (!isIntialized) {
            originalWidth = getWidth();
            originalHeight = getHeight();
            return;
        }
        for (int i = 0; i < fieldSizeY; ++i) {
            int y = i * cellSize;
            g.drawLine(0,y, getWidth(), y);
        }
        for (int i = 0; i < fieldSizeX; ++i) {
            int x = i * cellSize;
            g.drawLine(x,0, x, getHeight());
        }
    }
}