import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFenster login = new LoginFenster();
            login.setVisible(true);
        });
    }
}