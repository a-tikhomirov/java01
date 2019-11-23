package atih.xogame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    private final int WIDTH = 614;
    private final int HEIGTH = 688;
    public final int W_DELTA = 14;
    public final int H_DELTA = 88;

    private JLabel lblLastTurn;
    private JLabel lblGameInfo;             // для отображения информации об игре
    private JButton btnRestart;
    private JButton btnStart;
    private JButton btnSettings;            // кнопка старт
    private JButton btnExit;                // кнопка выход

    private GameMap gameMap;                // поле для игры крестики-нолики
    private SettingsWindow settingsWindow;  // окно выбора параметров игры

    GameWindow() {
        setWindowParams();
        setPosition();
        makeElements();
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
        getRootPane().setBorder(BorderFactory.createEmptyBorder(4, 4, 3, 4));
    }

    /**
     * Устновка размеров и положения окна
     */
    private void setPosition() {
        // получаем размеры экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // устанавливаем положение (посередине экрана) и размеры окна
        setBounds((int)(screenSize.getWidth() / 2 - WIDTH / 2), (int)(screenSize.getHeight() / 2 - HEIGTH / 2), WIDTH, HEIGTH);
    }

    private void makeElements() {
        lblLastTurn = new JLabel(" ");
        lblGameInfo = new JLabel(" ");
        btnRestart = new JButton("Abort");
        btnStart = new JButton("Start");
        btnSettings = new JButton("Setting");
        btnExit = new JButton("Exit");
    }

    /**
     * Добавление элементов, отображаемых/вызываемых в окне
     */
    private void addElements() {
        // создаем верхнюю панель
        JPanel pnlTop = new JPanel(new GridLayout(1, 3, 1, 0));
        // добавляем на верхнюю панель JLabel "информация об игре"
        pnlTop.add(lblLastTurn);
        pnlTop.add(lblGameInfo);
        pnlTop.add(btnRestart);
        btnRestart.setEnabled(false);

        // создаем экземпляр поля для игры
        gameMap = new GameMap(this);

        // создаем экземпляр окна настроек игры
        settingsWindow = new SettingsWindow(this);

        // создаем нижнюю панль
        JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 4, 4));
        // добавляем на нижнюю панель кнопки
        pnlBottom.add(btnSettings);
        pnlBottom.add(btnExit);

        add(pnlTop, BorderLayout.NORTH);    // добавляем верхнюю панель в верхнюю часть окна
        add(btnStart, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH); // добавляем нижнюю панель в нижнюю часть окна
    }

    /**
     * Добавление действий для элементов окна (кнопок)
     */
    private void addControls() {
        btnRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnRestartClick();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStartClick();
            }
        });
        btnSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSettingsClick();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExitClick();
            }
        });
    }

    private void btnRestartClick() {
        switchControls(gameMap, btnStart, false);
        gameMap.stopGame();
    }

    private void btnStartClick() {
        switchControls(btnStart, gameMap, true);
        gameMap.startGame();
    }

    /**
     * Вызывается при нажатии на кнопку <code>btnStart</>
     */
    private void btnSettingsClick() {
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
     * @param sizeX    размер по горизонтали
     * @param sizeY    размер по вертикали
     */
    public void reSize(int sizeX, int sizeY) {
        setSize(sizeX, sizeY);
        repaint();
    }

    /**
     * Вызов метода начала игры
     * @param mode          режим игры
     * @param fieldSizeX    число ячеек по горизонтали
     * @param fieldSizeY    число ячеек по вертикали
     * @param winLength     число символов подряд для выигрыша
     * @param ai1Difficulty сложность игры ИИ 1
     * @param ai2Difficulty сложность игры ИИ 2
     * @param turnsOrder    порядок ходов
     */
    public void applySettings(int mode, int fieldSizeX, int fieldSizeY, int winLength, int ai1Difficulty, int ai2Difficulty, int turnsOrder) {
        gameMap.applySettings(mode, fieldSizeX, fieldSizeY, winLength, ai1Difficulty, ai2Difficulty, turnsOrder);
        switchControls(gameMap, btnStart, false);
    }

    private void switchControls(Component toRemove, Component toAdd, boolean restartEnabled) {
        getContentPane().remove(toRemove);
        add(toAdd, BorderLayout.CENTER);
        btnRestart.setEnabled(restartEnabled);
        revalidate();
        repaint();
    }
}