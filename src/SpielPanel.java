import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SpielPanel extends JPanel implements ActionListener {
    // Настройки размеров поля
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25; // Размер одного блока (змейки/еды)
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private final int INITIAL_DELAY = 300; // Плавный старт

    // Высота верхнего табло (HUD) в пикселях
    private static final int HUD_HEIGHT = 50;

    // Массивы для хранения координат тела змейки
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private boolean specialAppleActive = false; // Активно ли сейчас супер-яблоко
    private int specialAppleTimer = 0;          // Таймер жизни яблока

    private final int NUM_OBSTACLES = 5; // Количество препятствий на поле
    private final int[] obstacleX = new int[NUM_OBSTACLES];
    private final int[] obstacleY = new int[NUM_OBSTACLES];

    private int bodyParts = 3; // Стартовая длина
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private int specialAppleX;
    private int specialAppleY;
    private Random random = new Random();

    private char direction = 'R'; // R = Right, L = Left, U = Up, D = Down
    private boolean running = false;
    private boolean paused = false; // Флаг для паузы
    private int level = 1;          // Текущий уровень
    private Timer timer;
    private int spielZeitSekunden = 0; // Переменная для хранения секунд
    private Timer infoTimer;            // Отдельный таймер для времени
    private final String spielerName;
    private Color snakeFarbe;

    public SpielPanel(String spielerName, Color farbe) {
        this.spielerName = spielerName;
        this.snakeFarbe = farbe; // Сохраняем переданный из логина цвет

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        starteGefecht();
    }

    public void starteGefecht() {
        this.requestFocusInWindow(); // Гарантирует, что панель сразу ловит нажатия клавиш
        generiereHindernisse(); // Сначала создаем кирпичи, чтобы змейка их видела при спавне

        // === СЛУЧАЙНЫЙ СПАВН ЗМЕЙКИ НИЖЕ ТАБЛО ===
        Random rand = new Random();
        boolean kollisionMitHindernis;
        int startX, startY;

        int minGridY = HUD_HEIGHT / UNIT_SIZE; // Старт ниже табло (ячейка 2)
        int maxGridY = SCREEN_HEIGHT / UNIT_SIZE;

        do {
            kollisionMitHindernis = false;

            // Оставляем отступ справа, чтобы хвост поместился влево от головы
            int maxGridX = (SCREEN_WIDTH / UNIT_SIZE) - bodyParts;

            startX = rand.nextInt(maxGridX) * UNIT_SIZE;
            // Случайный Y строго в границах игрового поля (ниже 50px)
            startY = (rand.nextInt(maxGridY - minGridY) + minGridY) * UNIT_SIZE;

            // Проверяем, чтобы голова или тело змейки не легли на сгенерированные кирпичи
            for (int i = 0; i < bodyParts; i++) {
                int segmentX = startX - (i * UNIT_SIZE);
                int segmentY = startY;

                for (int j = 0; j < NUM_OBSTACLES; j++) {
                    if (obstacleX[j] == segmentX && obstacleY[j] == segmentY) {
                        kollisionMitHindernis = true;
                        break;
                    }
                }
                if (kollisionMitHindernis) break;
            }

        } while (kollisionMitHindernis);

        // Заполняем массивы координат змейки
        for (int i = 0; i < bodyParts; i++) {
            x[i] = startX - (i * UNIT_SIZE);
            y[i] = startY;
        }

        neuerApfel(); // Спавним яблоко
        running = true;

        // Инициализация и запуск основного игрового таймера движения
        if (timer == null) {
            timer = new Timer(INITIAL_DELAY, this);
        } else {
            timer.setDelay(INITIAL_DELAY);
        }
        timer.start();

        // Инициализация и запуск таймера времени (каждую 1 секунду)
        if (infoTimer == null) {
            infoTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (running && !paused) {
                        spielZeitSekunden++;
                    }
                }
            });
        } else {
            spielZeitSekunden = 0;
        }
        infoTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        zeichnen(g);
    }

    public void zeichnen(Graphics g) {
        if (running) {
            // 1. Рисуем еду (яблоко)
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // 2. Рисуем Spezial-Food, если оно активно
            if (specialAppleActive) {
                if (specialAppleTimer > 15 || specialAppleTimer % 2 == 0) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(specialAppleX, specialAppleY, UNIT_SIZE, UNIT_SIZE);

                    g.setColor(Color.ORANGE);
                    g.drawOval(specialAppleX + 2, specialAppleY + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                }
            }

            // 3. Рисуем препятствия (Hindernisse)
            g.setColor(Color.GRAY);
            for (int i = 0; i < NUM_OBSTACLES; i++) {
                g.fillRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);
                g.setColor(Color.GRAY);
            }

            // 4. Рисуем змейку
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(snakeFarbe.darker());
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(snakeFarbe);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // === 5. ПОЛУПРОЗРАЧНОЕ ТАБЛО HUD ===
            g.setColor(new Color(0, 0, 0, 180)); // Черный с прозрачностью 180 из 255
            g.fillRect(0, 0, SCREEN_WIDTH, HUD_HEIGHT);

            // Тонкая разделительная линия под табло
            g.setColor(Color.DARK_GRAY);
            g.drawLine(0, HUD_HEIGHT, SCREEN_WIDTH, HUD_HEIGHT);

            // Отрисовка датчиков на табло (сдвиг по Y на 32 для центрирования)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + applesEaten, 15, 32);
            g.drawString("Level: " + level, SCREEN_WIDTH - 110, 32);

            String zeitText = "Zeit: " + spielZeitSekunden + "s";
            int textX = (SCREEN_WIDTH - g.getFontMetrics().stringWidth(zeitText)) / 2;
            g.drawString(zeitText, textX, 32);
            // ====================================================================

            // Если игра на паузе — затемняем поле и выводим надпись с инструкцией
            if (paused) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, HUD_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT - HUD_HEIGHT);

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("PAUSED", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("PAUSED")) / 2, SCREEN_HEIGHT / 2 - 20);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                String resumeText = "Drücke 'P' zum Fortsetzen";
                g.drawString(resumeText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(resumeText)) / 2, SCREEN_HEIGHT / 2 + 30);
            }

        } else {
            // ИГРА НЕ ИДЕТ: Сразу показываем финальное окно Game Over
            gameOver(g);
        }
    }

    public void generiereHindernisse() {
        Random rand = new Random();
        int minGridY = HUD_HEIGHT / UNIT_SIZE; // Кирпичи спавнятся ниже табло

        for (int i = 0; i < NUM_OBSTACLES; i++) {
            while (true) {
                int targetX = rand.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                int targetY = (rand.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE) - minGridY) + minGridY) * UNIT_SIZE;

                obstacleX[i] = targetX;
                obstacleY[i] = targetY;
                break;
            }
        }
    }

    public void neuerApfel() {
        boolean positionBelegt;
        int minGridY = HUD_HEIGHT / UNIT_SIZE; // Яблоки спавнятся ниже табло

        // 1. Спавн обычного яблока
        do {
            positionBelegt = false;
            appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = (random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE) - minGridY) + minGridY) * UNIT_SIZE;

            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    positionBelegt = true;
                    break;
                }
            }

            if (!positionBelegt) {
                for (int i = 0; i < NUM_OBSTACLES; i++) {
                    if (obstacleX[i] == appleX && obstacleY[i] == appleY) {
                        positionBelegt = true;
                        break;
                    }
                }
            }

        } while (positionBelegt);

        // 2. Логика для Spezial-Food (Шанс 30%)
        if (!specialAppleActive && random.nextInt(10) < 3) {
            do {
                positionBelegt = false;
                specialAppleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                specialAppleY = (random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE) - minGridY) + minGridY) * UNIT_SIZE;

                if (specialAppleX == appleX && specialAppleY == appleY) {
                    positionBelegt = true;
                    continue;
                }

                for (int i = 0; i < bodyParts; i++) {
                    if (x[i] == specialAppleX && y[i] == specialAppleY) {
                        positionBelegt = true;
                        break;
                    }
                }

                if (!positionBelegt) {
                    for (int i = 0; i < NUM_OBSTACLES; i++) {
                        if (obstacleX[i] == specialAppleX && obstacleY[i] == specialAppleY) {
                            positionBelegt = true;
                            break;
                        }
                    }
                }
            } while (positionBelegt);

            specialAppleActive = true;
            specialAppleTimer = 50;
            System.out.println("Спешл-фуд заспавнился в безопасном месте!");
        }
    }

    public void bewegen() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void pruefeApfel() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            neuerApfel();
            SoundManager.playSound("eat.wav");
            checkLevelUp();
        }

        if (specialAppleActive && (x[0] == specialAppleX) && (y[0] == specialAppleY)) {
            bodyParts++;
            applesEaten += 3;
            specialAppleActive = false;
            SoundManager.playSound("eat.wav");
            System.out.println("BAM! Съел спец-фуд! +3 очка");
            checkLevelUp();
        }
    }

    private void checkLevelUp() {
        int berechnetesLevel = (applesEaten / 10) + 1;

        if (berechnetesLevel > level) {
            level = berechnetesLevel;
            int neueVerzoegerung = Math.max(300 - (level * 10), 60);
            timer.setDelay(neueVerzoegerung);
            System.out.println("Level UP! Новый уровень: " + level + ", Скорость (Delay): " + neueVerzoegerung + " ms");
        }
    }

    public void pruefeKollisionen() {
        if (running) {
            // 1. Проверка кирпичей
            for (int i = 0; i < NUM_OBSTACLES; i++) {
                if ((x[0] == obstacleX[i]) && (y[0] == obstacleY[i])) {
                    System.out.println("СТОЛКНОВЕНИЕ С КИРПИЧОМ!");
                    running = false;
                    break;
                }
            }
        }

        // 2. Проверка столкновения с собственным телом
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        // 3. Проверка столкновения со стенами (Верхняя граница теперь HUD_HEIGHT!)
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < HUD_HEIGHT || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        // 4. Если игра окончена — останавливаем таймеры
        if (!running) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            if (infoTimer != null && infoTimer.isRunning()) {
                infoTimer.stop();
            }
            SoundManager.playSound("gameover.wav");
            HighScoreManager.saveScore(spielerName, applesEaten);
        }
    }

    public void spielZuruecksetzen() {
        spielZeitSekunden = 0;
        bodyParts = 3;
        applesEaten = 0;
        level = 1;
        direction = 'R';
        paused = false;

        if (timer != null) {
            timer.setDelay(INITIAL_DELAY);
        }

        starteGefecht();
    }

    public void gameOver(Graphics g) {
        // Очищаем экран черным фоном перед выводом Game Over
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Dein Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Dein Score: " + applesEaten)) / 2, 120);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("--- HIGHSCORES ---", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("--- HIGHSCORES ---")) / 2, 180);

        java.util.List<String> scores = HighScoreManager.loadScores();
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 16));

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Drücke 'R' für Neustart", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Drücke 'R' für Neustart")) / 2, SCREEN_HEIGHT - 30);

        int yOffset = 220;
        int rank = 1;

        for (String line : scores) {
            if (rank > 5) break;

            String[] parts = line.split(";");
            if (parts.length >= 3) {
                String name = parts[0];
                String score = parts[1];
                String date = parts[2];

                String scoreLine = String.format("%2d. %-12s Pkt: %-5s (%s)", rank, name, score, date);
                g.drawString(scoreLine, 130, yOffset);
                yOffset += 30;
                rank++;
            }
        }

        if (scores.isEmpty()) {
            g.drawString("Noch keine Highscores vorhanden.", 160, yOffset);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            bewegen();
            pruefeApfel();
            pruefeKollisionen();

            if (specialAppleActive) {
                specialAppleTimer--;
                if (specialAppleTimer <= 0) {
                    specialAppleActive = false;
                    System.out.println("Спешл-фуд исчез, не успел!");
                }
            }
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (!running && keyCode == KeyEvent.VK_R) {
                spielZuruecksetzen();
                return;
            }

            if (running && keyCode == KeyEvent.VK_P) {
                paused = !paused;
                repaint();
                return;
            }

            if (paused) return;

            switch (keyCode) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }
}