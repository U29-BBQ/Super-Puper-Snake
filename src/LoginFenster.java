import javax.swing.*;
import java.awt.*;

public class LoginFenster extends JFrame {
    private JTextField nameField;
    private JButton startButton;

    public LoginFenster() {
        setTitle("Snake Game - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Панель интерфейса (Тёмная тема)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Spielername eingeben:", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(label, gbc);

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(nameField, gbc);

        startButton = new JButton("Spiel starten");
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(startButton, gbc);

        add(panel, BorderLayout.CENTER);

        // Логика нажатия кнопки
        startButton.addActionListener(e -> {
            String spielerName = nameField.getText().trim();
            if (spielerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte Namen eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                this.dispose(); // Закрываем окно логина
                starteSpiel(spielerName); // Запускаем саму игру
            }
        });
    }

    private void starteSpiel(String spielerName) {
        JFrame gameFrame = new JFrame("Snake Game - Spieler: " + spielerName);
        SpielPanel spielPanel = new SpielPanel(spielerName);

        gameFrame.add(spielPanel);
        gameFrame.setResizable(false);
        gameFrame.pack(); // Подгоняет размер окна под размер SpielPanel
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
    }
}