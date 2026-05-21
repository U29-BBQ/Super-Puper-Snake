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
    private final int INITIAL_DELAY = 300; // Было 150, ставим 200 для плавного старта

    // Массивы для хранения координат тела змейки
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];

    private final int NUM_OBSTACLES = 5; // Количество препятствий на поле
    private final int[] obstacleX = new int[NUM_OBSTACLES];
    private final int[] obstacleY = new int[NUM_OBSTACLES];

    private int bodyParts = 3; // Стартовая длина
    private int applesEaten = 0;
    private int appleX;
    private int appleY;

    private char direction = 'R'; // R = Right, L = Left, U = Up, D = Down
    private boolean running = false;
    private boolean paused = false; // Флаг для паузы
    private int level = 1;          // Текущий уровень
    private Timer timer;
    private final String spielerName;

    public SpielPanel(String spielerName) {
        this.spielerName = spielerName;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 20));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        starteGefecht();
    }

    public void starteGefecht() {
        generiereHindernisse();
        neuerApfel();
        running = true;

        // Если таймер ещё не был создан — создаём его со стартовой скоростью
        if (timer == null) {
            timer = new Timer(INITIAL_DELAY, this);
        } else {
            // Если он уже есть (например, при перезапуске на 'R'), просто сбрасываем скорость на стартовую
            timer.setDelay(INITIAL_DELAY);
        }

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        zeichnen(g);
    }

    public void zeichnen(Graphics g) {
        if (running) {
            // Рисуем еду (яблоко)
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            // Рядом со Score рисуем текущий Level
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 30);
            g.drawString("Level: " + level, SCREEN_WIDTH - 110, 30);

            // Рисуем препятствия (Hindernisse)
            g.setColor(Color.GRAY); // Серый цвет для кирпичей
            for (int i = 0; i < NUM_OBSTACLES; i++) {
                // Рисуем сам блок
                g.fillRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);

                // Делаем легкую черную рамку вокруг каждого блока, чтобы они выглядели как отдельные кирпичи
                g.setColor(Color.BLACK);
                g.drawRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);
                g.setColor(Color.GRAY); // Возвращаем цвет для следующей итерации
            }

// Если игра на паузе — пишем это по центру
            if (paused) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("PAUSED", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("PAUSED")) / 2, SCREEN_HEIGHT / 2);
            }



            // Рисуем змейку
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN); // Голова
                } else {
                    g.setColor(new Color(45, 180, 0)); // Тело
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Отображение текущего счета
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 30);
        } else {
            gameOver(g);
        }
    }

    public void generiereHindernisse() {
        Random random = new Random();
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            while (true) {
                // Генерируем случайную точку на сетке
                int targetX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                int targetY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

                // Проверяем, чтобы блок не появился на стартовой позиции змейки (в левом верхнем углу)
                if (targetX < UNIT_SIZE * 5 && targetY == 0) {
                    continue; // Генерируем заново
                }

                obstacleX[i] = targetX;
                obstacleY[i] = targetY;
                break; // Координаты подходят, переходим к следующему блоку
            }
        }
    }

    public void neuerApfel() {
        Random random = new Random();
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
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

            // Уровни растут каждые 5 яблок
            if (applesEaten % 5 == 0) {
                level++;

                // Теперь всё строго на английском, без колхоза
                int currentDelay = timer.getDelay();
                int newDelay = currentDelay - 10;

                // Безопасный предел скорости
                if (newDelay < 70) {
                    newDelay = 70;
                }

                timer.setDelay(newDelay);

                System.out.println("Level UP! Level: " + level + ", Delay: " + newDelay + " ms");
            }
        }
    }

    public void pruefeKollisionen() {
        // 1. ЖЕЛЕЗНАЯ ПРОВЕРКА КИРПИЧЕЙ
        if (running) {
            for (int i = 0; i < NUM_OBSTACLES; i++) {
                if ((x[0] == obstacleX[i]) && (y[0] == obstacleY[i])) {
                    System.out.println("СТОЛКНОВЕНИЕ С КИРПИЧОМ! Игрок врезался в блок " + i); // Отладка
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

        // 3. Проверка столкновения со стенами
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        // 4. Если игра окончена — останавливаем всё строго ОДИН раз
        if (!running) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            SoundManager.playSound("gameover.wav");
            HighScoreManager.saveScore(spielerName, applesEaten);
        }
    }

    public void spielZuruecksetzen() {
        bodyParts = 3;
        applesEaten = 0;
        level = 1;
        direction = 'R';
        paused = false;

        timer.setDelay(150); // ТУТ ДОЛЖНА БЫТЬ ТВОЯ СТАРТОВАЯ КОМФОРТНАЯ СКОРОСТЬ!

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        starteGefecht();
    }

    public void gameOver(Graphics g) {
        // 1. Рисуем надпись Game Over
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, 80);

        // 2. Рисуем текущий результат игрока
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Dein Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Dein Score: " + applesEaten)) / 2, 120);

        // 3. Заголовок таблицы рекордов
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("--- HIGHSCORES ---", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("--- HIGHSCORES ---")) / 2, 180);

        // 4. Загружаем рекорды через HighscoreManager и выводим на экран
        java.util.List<String> scores = HighScoreManager.loadScores();
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 16)); // Моноширинный шрифт, чтобы колонки были ровными

        // подсказать кнопку перезапуска
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Drücke 'R' für Neustart", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Drücke 'R' für Neustart")) / 2, SCREEN_HEIGHT - 30);

        int yOffset = 220;
        int rank = 1;

        // Выводим только топ-10 результатов, чтобы они не вылезли за экран
        for (String line : scores) {
            if (rank > 5) break;

            // Разделяем строку "Имя;Очки;Дата"
            String[] parts = line.split(";");
            if (parts.length >= 3) {
                String name = parts[0];
                String score = parts[1];
                String date = parts[2];

                // Форматируем строку для красивого выравнивания
                String scoreLine = String.format("%2d. %-12s Pkt: %-5s (%s)", rank, name, score, date);

                g.drawString(scoreLine, 130, yOffset);
                yOffset += 30; // Смещаемся ниже для следующей строчки
                rank++;
            }
        }

        if (scores.isEmpty()) {
            g.drawString("Noch keine Highscores vorhanden.", 160, yOffset);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) { // Если игра идет и НЕ на паузе — двигаемся
            bewegen();
            pruefeApfel();
            pruefeKollisionen();
        }
        repaint();
    }

    // Управление клавишами (WASD или Стрелочки)
    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            // Если игра окончена и игрок нажимает R — перезапускаем игру
            if (!running && keyCode == KeyEvent.VK_R) {
                spielZuruecksetzen();
                return;
            }

            // Если нажали P — переключаем паузу
            if (running && keyCode == KeyEvent.VK_P) {
                paused = !paused;
                repaint();
                return;
            }

            // Если игра на паузе, змейка не должна реагировать на стрелки
            if (paused) return;

            // Обычное управление движением
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