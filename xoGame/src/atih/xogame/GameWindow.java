package atih.xogame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    private JLabel lblGameInfo = new JLabel(" ");      // для отображения информации об игре
    private JButton btnStart = new JButton("Start");   // кнопка старт
    private JButton btnExit = new JButton("Exit");     // кнопка выход

    private GameMap gameMap;                                // поле для игры крестики-нолики
    private SettingsWindow settingsWindow;                  // окно выбора параметров игры

    private int originalWidth;                              // первоначальная ширина окна
    private int originalHeight;                             // первоначальная высота окна

    GameWindow() {
        setWindowParams();
        setPosition();
        addElements();
        addControls();
        setVisible(true);
    }

    /**
     * Установка основных параметров окна <code>GameWindow</>
     */
    private void setWindowParams() {
        // задаем действие для закрытия окна - завершение работы приложения
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // задаем заголовок окна
        setTitle("Tic Tac Toe");

        // запрещаем изменение размеров окна
        setResizable(false);

        // задаем отступы от границ окна внутрь окна
        getRootPane().setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    /**
     * Устновка размеров и положения окна
     */
    private void setPosition() {
        // получаем размеры экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        originalWidth = (int) screenSize.getWidth() / 4;    // ширина - четверть ширины экрана
        originalHeight = (int) screenSize.getHeight() / 2;  // высота - половина высоты экрана
        // устанавливаем положение (посередине экрана) и размеры окна
        setBounds((int)(screenSize.getWidth() / 2 - originalWidth / 2), (int)(screenSize.getHeight() / 2 - originalHeight / 2), originalWidth, originalHeight);
    }

    /**
     * Добавление элементов, отображаемых/вызываемых в окне
     */
    private void addElements() {
        // создаем верхнюю панель
        JPanel pnlTop = new JPanel(new GridLayout(1, 1, 4, 4));
        // добавляем на верхнюю панель JLabel "информация об игре"
        pnlTop.add(lblGameInfo);

        // создаем экземпляр поля для игры
        gameMap = new GameMap(this);

        // создаем экземпляр окна настроек игры
        settingsWindow = new SettingsWindow(this);

        // создаем нижнюю панль
        JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 4, 4));
        // добавляем на нижнюю панель кнопки
        pnlBottom.add(btnStart);
        pnlBottom.add(btnExit);

        add(pnlTop, BorderLayout.NORTH);    // добавляем верхнюю панель в верхнюю часть окна
        add(gameMap, BorderLayout.CENTER);  // добавляем поле для игры в центр окна
        add(pnlBottom, BorderLayout.SOUTH); // добавляем нижнюю панель в нижнюю часть окна
    }

    /**
     * Добавление действий для элементов окна (кнопок)
     */
    private void addControls() {
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStartClick();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExitClick();
            }
        });
    }

    /**
     * Вызывается при нажатии на кнопку <code>btnStart</>
     */
    private void btnStartClick() {
        settingsWindow.setVisible(true);
    }

    /**
     * Вызывается при нажатии на кнопку <code>btnExit</>
     */
    private void btnExitClick() {
        System.exit(0);
    }

    /**
     * Изменение размеров окна
     * @param deltaX    изменение размера по горизонтали
     * @param deltaY    изменение размера по вертикали
     */
    public void reSize(int deltaX, int deltaY) {

        setSize(originalWidth + deltaX, originalHeight + deltaY);
    }

    /**
     * Вызов метода начала игры
     * @param mode          режим игры
     * @param fieldSizeX    число ячеек по горизонтали
     * @param fieldSizeY    число ячеек по вертикали
     * @param winLength     число символов подряд для выигрыша
     * @param difficulty    сложность игры против ИИ
     */
    public void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength, int difficulty) {
        gameMap.startNewGame(mode, fieldSizeX, fieldSizeY, winLength, difficulty);
    }
}