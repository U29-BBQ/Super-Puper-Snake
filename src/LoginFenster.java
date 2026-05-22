import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LoginFenster extends JFrame {
    private JTextField nameField;
    private JButton startButton;
    private JComboBox<GameTheme> themeComboBox;
    private Color ausgewaehlteFarbe = Color.GREEN; // Цвет по умолчанию

    // Кнопки для выбора цвета
    private JButton btnGreen, btnBlue, btnMagenta, btnOrange;

    public LoginFenster() {
        // Заставляет Java использовать системный рендеринг для четкости DPI
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        setTitle("Snake Game - Login");
        setSize(420, 600); // Немного увеличили высоту окна под новый элемент
        // Изменяем операцию закрытия
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Добавляем слушатель для перехвата закрытия окна
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int reply = JOptionPane.showConfirmDialog(
                        LoginFenster.this,
                        "Möchtest du das Spiel wirklich beenden?",
                        "Spiel beenden",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ====================================================================
        // 1. ВЫБОР ЦВЕТА (Строки 0 и 1)
        // ====================================================================
        JLabel colorLabel = new JLabel("Wähle deine Snake-Farbe:", SwingConstants.CENTER);
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(colorLabel, gbc);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        colorPanel.setBackground(new Color(30, 30, 30));

        btnGreen = createColorButton(Color.GREEN);
        btnBlue = createColorButton(Color.BLUE);
        btnMagenta = createColorButton(Color.MAGENTA);
        btnOrange = createColorButton(new Color(255, 165, 0));

        // Выделяем зеленый цвет по умолчанию
        btnGreen.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));

        colorPanel.add(btnGreen);
        colorPanel.add(btnBlue);
        colorPanel.add(btnMagenta);
        colorPanel.add(btnOrange);

        gbc.gridy = 1; gbc.gridwidth = 4;
        panel.add(colorPanel, gbc);

        // ====================================================================
        // 2. ВВОД ИМЕНИ (Строки 2 и 3)
        // ====================================================================
        JLabel label = new JLabel("Spielername eingeben:", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 15, 5, 15);
        panel.add(label, gbc);

        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets = new Insets(5, 15, 15, 15);
        panel.add(nameField, gbc);

        // ====================================================================
        // 3. БЛОК ВЫБОРА ТЕМЫ (Строки 4 и 5)
        // ====================================================================
        JLabel themeLabel = new JLabel("Wähle dein Theme:", SwingConstants.CENTER);
        themeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        themeLabel.setForeground(Color.WHITE);
        gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(themeLabel, gbc);

        themeComboBox = new JComboBox<>(GameTheme.values());
        themeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JLabel)themeComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.insets = new Insets(5, 15, 15, 15);
        panel.add(themeComboBox, gbc);

        // ====================================================================
        // 4. БЛОК УПРАВЛЕНИЯ (Строка 6)
        // ====================================================================
        JTextPane helpPane = new JTextPane();
        helpPane.setText(
                "STEUERUNG:\n\n" +
                        "WASD / PFEILTASTEN - BEWEGEN\n" +
                        "'P' TASTE - PAUSE\n" +
                        "'R' TASTE - NEUSTART"
        );
        helpPane.setEditable(false);
        helpPane.setFont(new Font("Arial", Font.BOLD, 14));
        helpPane.setForeground(Color.WHITE);
        helpPane.setBackground(new Color(45, 45, 45));
        helpPane.setOpaque(true);
        helpPane.setMargin(new Insets(12, 15, 12, 15));

        // Центрирование текста внутри JTextPane
        StyledDocument doc = helpPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        helpPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 15, 15, 15);
        panel.add(helpPane, gbc);

        // ====================================================================
        // 5. КНОПКА СТАРТА (Строка 7 - в самом низу)
        // ====================================================================
        startButton = new JButton("Spiel starten");
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(300, 42));

        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 15, 15, 15);
        panel.add(startButton, gbc);

        add(panel, BorderLayout.CENTER);

        // Логика кнопки старта
        startButton.addActionListener(e -> {
            String spielerName = nameField.getText().trim();
            if (spielerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte Namen eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                GameTheme ausgewaehltesTheme = (GameTheme) themeComboBox.getSelectedItem();
                this.dispose();
                starteSpiel(spielerName, ausgewaehlteFarbe, ausgewaehltesTheme);
            }
        });
    }

    private JButton createColorButton(Color color) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        btn.addActionListener(e -> {
            btnGreen.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            btnBlue.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            btnMagenta.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            btnOrange.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
            ausgewaehlteFarbe = color;
        });

        return btn;
    }

    private void starteSpiel(String spielerName, Color farbe, GameTheme theme) {
        SpielPanel spielPanel = new SpielPanel(spielerName, farbe, theme);

        JFrame gameFrame = new JFrame("Snake Game - Spieler: " + spielerName);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(spielPanel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }
}