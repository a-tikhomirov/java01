package atih.xogame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SettingsWindow extends JFrame {
    private static final int WIN_WIDTH = 350;   // ширина окна
    private static final int WIN_HEIGHT = 280;  // высота окна

    private static final String FIELD_SIZEX_PREFIX = "Horizontal field size is: ";
    private static final String FIELD_SIZEY_PREFIX = "Vertical field size is: ";
    private static final String WIN_LENGTH_PREFIX = "Winning length is: ";
    private static final String AI_DIFF_PREFIX = "AI difficulty is: ";

    private static final int MIN_DIF = 0;       // минимальная сложность игры
    private static final int MED_DIF = 2;       // средняя сложность игры
    private static final int HIGH_DIF = 4;      // максимальная сложность игры

    private static final int MIN_SIZE = 3;      // минимальный размер игрового поля
    private static final int MAX_SIZE = 10;     // максимальный размер игрового поля

    private GameWindow gameWindow;              // ссылка на экземпляр основного окна

    private JRadioButton rbtnHumVsAi;           // выбора режима Игрок против ИИ
    private JRadioButton rbtnHumVsHum;          // выбор режима Игрок против Игрока

    private JLabel lblFieldSizeX;               // отображения текущего размера по горизонтали
    private JSlider sldFieldSizeX;              // слайдер выбора размера по горизонтали

    private JLabel lblFieldSizeY;               // отображения текущего размера по вертикали
    private JSlider sldFieldSizeY;              // слайдер выбора размера по вертикали

    private JLabel lblWinLength;                // отображение текущей длины выигрышной комбинации
    private JSlider sldWinLength;               // слайдер выбора длины выигрыщной комбинации

    private JLabel lblAiDifficulty;             // отображение текущей сложности игры
    private JSlider sldAiDifficulty;            // слайдер выбора сложности игры

    private JButton btnStartGame;               // кнопка старта игры

    SettingsWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setWindowParams();
        setPosition();
        addElements();
        addControls();
    }

    /**
     * Установка основных параметров окна <code>GameWindow</>
     */
    private void setWindowParams() {
        // задаем заголовок окна
        setTitle("Choose game parameters");

        // запрещаем изменение размеров окна
        setResizable(false);

        // задаем отступы от границ окна внутрь окна
        getRootPane().setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // задаем способ размещения элементов в окне
        setLayout(new GridLayout(12, 1));
    }

    /**
     * Устновка размеров и положения окна
     */
    private void setPosition() {
        // получаем положение основого окна
        Rectangle gameWindowBounds = gameWindow.getBounds();
        // устанавливаем положение (посередине основого окна) и размеры окна
        int posX = (int) (gameWindowBounds.getCenterX() - WIN_WIDTH / 2);
        int posY = (int) (gameWindowBounds.getCenterY() - WIN_HEIGHT / 2);
        setBounds(posX, posY, WIN_WIDTH, WIN_HEIGHT);
    }

    /**
     * Добавление элементов, отображаемых в окне
     */
    private void addElements() {
        // Создаем элементы выбора режима игры
        JLabel lblGamemode = new JLabel("Choose game mode:");
        // создаем radiobutton'ы
        rbtnHumVsAi = new JRadioButton("Human vs. AI", true);
        rbtnHumVsHum = new JRadioButton("Human vs. Human");
        // группируем radiobutton'ы
        ButtonGroup btngrGameMode = new ButtonGroup();
        btngrGameMode.add(rbtnHumVsAi);
        btngrGameMode.add(rbtnHumVsHum);
        // Создаем элементы выбора размера поля по горизонтали
        lblFieldSizeX = new JLabel(FIELD_SIZEX_PREFIX + MIN_SIZE);
        sldFieldSizeX = new JSlider(MIN_SIZE, MAX_SIZE, MIN_SIZE);
        // Создаем элементы выбора размера поля по вертикали
        lblFieldSizeY = new JLabel(FIELD_SIZEY_PREFIX + MIN_SIZE);
        sldFieldSizeY = new JSlider(MIN_SIZE, MAX_SIZE, MIN_SIZE);
        // Создаем элементы выбора лдины выигрышной комбинации
        lblWinLength = new JLabel(WIN_LENGTH_PREFIX + MIN_SIZE);
        sldWinLength = new JSlider(MIN_SIZE, getMaxWinLength(), MIN_SIZE);
        // Создаем элементы выбора сложности игра против ИИ
        lblAiDifficulty = new JLabel(AI_DIFF_PREFIX + MED_DIF);
        sldAiDifficulty = new JSlider(MIN_DIF, HIGH_DIF, MED_DIF);
        // Создаем кнопку Начать игру
        btnStartGame = new JButton("Start Game!");

        add(lblGamemode);
        add(rbtnHumVsAi);
        add(rbtnHumVsHum);

        add(lblFieldSizeX);
        add(sldFieldSizeX);

        add(lblFieldSizeY);
        add(sldFieldSizeY);

        add(lblWinLength);
        add(sldWinLength);

        add(lblAiDifficulty);
        add(sldAiDifficulty);

        add(btnStartGame);
    }

    /**
     * Добавление действий для элементов окна
     */
    private void addControls() {
        // Добавляем для случая изменения положения основоного окна
        // чтобы текушее окно всегда открывалось посередине основного
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                setPosition();
            }
        });

        // В случае изменения режима игры добавляем показ/скрытие выбора сложности игрв против ИИ
        rbtnHumVsAi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAiDiffVisible(true);
            }
        });
        rbtnHumVsHum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAiDiffVisible(false);
            }
        });

        // В случае изменения значений слайдеров добавляем изменение соответствуюших надписей
        // Для изменения значений слайдеров размеров поля также добавляем изменение
        // максимального значения слайдера длины выигрышной комбинации
        sldFieldSizeX.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblFieldSizeX, FIELD_SIZEX_PREFIX + sldFieldSizeX.getValue());
                sldWinLength.setMaximum(getMaxWinLength());
            }
        });
        sldFieldSizeY.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblFieldSizeY, FIELD_SIZEY_PREFIX + sldFieldSizeY.getValue());
                sldWinLength.setMaximum(getMaxWinLength());
            }
        });
        sldWinLength.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblWinLength, WIN_LENGTH_PREFIX + sldWinLength.getValue());
            }
        });
        sldAiDifficulty.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblAiDifficulty, AI_DIFF_PREFIX + sldAiDifficulty.getValue());
            }
        });

        // Добавляем действие для нажатия на кнопку btnStart
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStartGameClick();
            }
        });
    }

    /**
     * Метод возвращает максимальное значение длины выигрышной комбинации
     * @return  максимальное значение длины выигрышной комбинации
     */
    private int getMaxWinLength() {
        int sizeX = sldFieldSizeX.getValue();
        int sizeY = sldFieldSizeY.getValue();
        return (sizeX > sizeY) ? sizeY : sizeX;
    }

    /**
     * Изменение текста надписи
     * @param label надпись для изменения
     * @param text  текст который нужно установить
     */
    private void changeLabelText(JLabel label, String text) {
        label.setText(text);
    }

    /**
     * Показ/скрытие выбора сложности игры против ИИ
     * @param visible   true - показать
     *                  false - скрыть
     */
    private void setAiDiffVisible(boolean visible) {
        lblAiDifficulty.setVisible(visible);
        sldAiDifficulty.setVisible(visible);
    }

    private void btnStartGameClick() {
        int gameMode;
        int aiDifficulty;
        if (rbtnHumVsAi.isSelected()) {
            gameMode = GameMap.GM_HVA;
            aiDifficulty = sldAiDifficulty.getValue();
        } else if (rbtnHumVsHum.isSelected()) {
            gameMode = GameMap.GM_HVH;
            aiDifficulty = -1;
        } else {
            throw new RuntimeException("Unexpected game mode!");
        }

        int fieldSizeX = sldFieldSizeX.getValue();
        int fieldSizeY = sldFieldSizeY.getValue();
        int winLength = sldWinLength.getValue();

        gameWindow.startNewGame(gameMode, fieldSizeX, fieldSizeY, winLength, aiDifficulty);
        setVisible(false);
    }
}