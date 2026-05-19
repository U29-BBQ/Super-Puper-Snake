import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HighScoreManager {
    private static final String FILE_PATH = "highscores.txt";

    // Возвращаем запись даты назад!
    public static void saveScore(String username, int score) {
        String datum = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            out.println(username + ";" + score + ";" + datum); // Теперь пишется Имя;Очки;Дата
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Твой метод загрузки с сортировкой
    public static List<String> loadScores() {
        List<String> scores = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return scores;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) scores.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        scores.sort((o1, o2) -> {
            try {
                int s1 = Integer.parseInt(o1.split(";")[1].trim());
                int s2 = Integer.parseInt(o2.split(";")[1].trim());
                return Integer.compare(s2, s1);
            } catch (Exception e) {
                return 0;
            }
        });

        return scores;
    }
}