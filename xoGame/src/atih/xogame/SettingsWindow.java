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
    private static final int WIN_HEIGHT = 300;  // высота окна

    private static final String AI_DIFF_PREFIX = " difficulty is: ";
    private static final String TURNS_ORDER_PREFIX = "First move belongs to: ";
    private static final String FIELD_SIZEX_PREFIX = "Horizontal field size is: ";
    private static final String FIELD_SIZEY_PREFIX = "Vertical field size is: ";
    private static final String WIN_LENGTH_PREFIX = "Winning length is: ";

    private GameWindow gameWindow;              // ссылка на экземпляр основного окна

    private JLabel lblGameMode;                 // надпись выбора режима игры
    private JRadioButton rbtnHumVsAi;           // выбора режима Игрок против ИИ
    private JRadioButton rbtnHumVsHum;          // выбор режима Игрок против Игрока
    private JRadioButton rbtnAiVsAi;            // выбора режима ИИ против ИИ

    private JLabel lblAiDifficulty;             // отображение текущей сложности игры
    private JSlider sldAiDifficulty;            // слайдер выбора сложности игры

    private JLabel lblAi2Difficulty;             // отображение текущей сложности игры
    private JSlider sldAi2Difficulty;            // слайдер выбора сложности игры

    private JLabel lblTurnsOrder;               // надпись выбора кому принадлежит первый ход
    private JSlider sldTurnsOrder;              // слайдер выбора кому принадлежит первый ход

    private JLabel lblFieldSizeX;               // отображения текущего размера по горизонтали
    private JSlider sldFieldSizeX;              // слайдер выбора размера по горизонтали

    private JLabel lblFieldSizeY;               // отображения текущего размера по вертикали
    private JSlider sldFieldSizeY;              // слайдер выбора размера по вертикали

    private JLabel lblWinLength;                // отображение текущей длины выигрышной комбинации
    private JSlider sldWinLength;               // слайдер выбора длины выигрыщной комбинации

    private JLabel lblWarning;
    private JButton btnStartGame;               // кнопка старта игры

    private int gameMode;

    SettingsWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setWindowParams();
        setPosition();
        makeElements();
        gameMode = GameMap.GM_HVA;
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
        setLayout(new GridLayout(16, 1));
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

    private void makeElements() {
        // Создаем элементы выбора режима игры
        lblGameMode = new JLabel("Choose game mode:");
        // создаем radiobutton'ы
        rbtnHumVsAi = new JRadioButton("Human vs. AI", true);
        rbtnHumVsHum = new JRadioButton("Human vs. Human");
        rbtnAiVsAi = new JRadioButton("AI 1 vs. AI 2");
        // группируем radiobutton'ы
        ButtonGroup btngrGameMode = new ButtonGroup();
        btngrGameMode.add(rbtnHumVsAi);
        btngrGameMode.add(rbtnHumVsHum);
        btngrGameMode.add(rbtnAiVsAi);

        // Создаем элементы выбора сложности игра против ИИ
        lblAiDifficulty = new JLabel(GameMap.PLAYERS[0][1] + AI_DIFF_PREFIX + GameMap.DEF_DIF);
        sldAiDifficulty = new JSlider(GameMap.MIN_DIFF, GameMap.MAX_DIFF, GameMap.DEF_DIF);

        lblAi2Difficulty = new JLabel(GameMap.PLAYERS[2][1] + AI_DIFF_PREFIX + GameMap.DEF_DIF);
        sldAi2Difficulty = new JSlider(GameMap.MIN_DIFF, GameMap.MAX_DIFF, GameMap.DEF_DIF);

        lblTurnsOrder = new JLabel(TURNS_ORDER_PREFIX + GameMap.PLAYERS[0][0]);
        sldTurnsOrder = new JSlider(GameMap.STRAIGTH_ORDER, GameMap.REVERSE_ORDER, GameMap.STRAIGTH_ORDER);

        // Создаем элементы выбора размера поля по горизонтали
        lblFieldSizeX = new JLabel(FIELD_SIZEX_PREFIX + GameMap.MIN_SIZE);
        sldFieldSizeX = new JSlider(GameMap.MIN_SIZE, GameMap.MAX_SIZE, GameMap.MIN_SIZE);
        // Создаем элементы выбора размера поля по вертикали
        lblFieldSizeY = new JLabel(FIELD_SIZEY_PREFIX + GameMap.MIN_SIZE);
        sldFieldSizeY = new JSlider(GameMap.MIN_SIZE, GameMap.MAX_SIZE, GameMap.MIN_SIZE);
        // Создаем элементы выбора лдины выигрышной комбинации
        lblWinLength = new JLabel(WIN_LENGTH_PREFIX + GameMap.MIN_SIZE);
        sldWinLength = new JSlider(GameMap.MIN_SIZE, GameMap.MIN_SIZE, GameMap.MIN_SIZE);

        lblWarning = new JLabel("Warning! The Game will be restarted if Apply clicked");
        // Создаем кнопку Начать игру
        btnStartGame = new JButton("Apply");
    }

    /**
     * Добавление элементов, отображаемых в окне
     */
    private void addElements() {
        add(lblGameMode);
        add(rbtnHumVsAi);
        add(rbtnHumVsHum);
        add(rbtnAiVsAi);

        if (gameMode == GameMap.GM_HVA) {
            add(lblAiDifficulty);
            add(sldAiDifficulty);

            add(lblTurnsOrder);
            add(sldTurnsOrder);
        } else if (gameMode == GameMap.GM_AVA) {
            add(lblAiDifficulty);
            add(sldAiDifficulty);

            add(lblAi2Difficulty);
            add(sldAi2Difficulty);
        }

        add(lblFieldSizeX);
        add(sldFieldSizeX);

        add(lblFieldSizeY);
        add(sldFieldSizeY);

        add(lblWinLength);
        add(sldWinLength);

        add(lblWarning);
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
                super.componentShown(e);
            }
        });

        // В случае изменения режима игры добавляем показ/скрытие выбора сложности игрв против ИИ
        rbtnHumVsAi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setAiDiffVisible(true);
                gameMode = GameMap.GM_HVA;
                gameModeChanged();
                changeLabelText(lblAiDifficulty, GameMap.PLAYERS[gameMode][(gameMode == 0) ? 1 : 0] + AI_DIFF_PREFIX + sldAiDifficulty.getValue());
            }
        });
        rbtnHumVsHum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setAiDiffVisible(false);
                gameMode = GameMap.GM_HVH;
                gameModeChanged();
            }
        });
        rbtnAiVsAi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = GameMap.GM_AVA;
                gameModeChanged();
                changeLabelText(lblAiDifficulty, GameMap.PLAYERS[gameMode][(gameMode == 0) ? 1 : 0] + AI_DIFF_PREFIX + sldAiDifficulty.getValue());
            }
        });

        // В случае изменения значений слайдеров добавляем изменение соответствуюших надписей
        // Для изменения значений слайдеров размеров поля также добавляем изменение
        // максимального значения слайдера длины выигрышной комбинации
        sldAiDifficulty.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblAiDifficulty, GameMap.PLAYERS[gameMode][(gameMode == 0) ? 1 : 0] + AI_DIFF_PREFIX + sldAiDifficulty.getValue());
            }
        });
        sldAi2Difficulty.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblAi2Difficulty, GameMap.PLAYERS[gameMode][1] + AI_DIFF_PREFIX + sldAi2Difficulty.getValue());
            }
        });
        sldTurnsOrder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLabelText(lblTurnsOrder, TURNS_ORDER_PREFIX + GameMap.PLAYERS[gameMode][sldTurnsOrder.getValue()]);
            }
        });
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

        // Добавляем действие для нажатия на кнопку btnStart
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStartGameClick();
            }
        });
    }

    private void gameModeChanged() {
        getContentPane().removeAll();
        addElements();
        validate();
        repaint();
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
        int aiDifficulty, ai2Difficulty, turnsOrder;
        if (gameMode == GameMap.GM_HVA) {
            aiDifficulty = sldAiDifficulty.getValue();
            ai2Difficulty = -1;
            turnsOrder = sldTurnsOrder.getValue();
        } else if (gameMode == GameMap.GM_HVH) {
            aiDifficulty = -1;
            ai2Difficulty = -1;
            turnsOrder = -1;
        } else if (gameMode == GameMap.GM_AVA) {
            aiDifficulty = sldAiDifficulty.getValue();
            ai2Difficulty = sldAi2Difficulty.getValue();
            turnsOrder = -1;
        } else {
            throw new RuntimeException("Unexpected game mode!");
        }

        int fieldSizeX = sldFieldSizeX.getValue();
        int fieldSizeY = sldFieldSizeY.getValue();
        int winLength = sldWinLength.getValue();

        gameWindow.applySettings(gameMode, fieldSizeX, fieldSizeY, winLength, aiDifficulty, ai2Difficulty, turnsOrder);
        setVisible(false);
    }
}