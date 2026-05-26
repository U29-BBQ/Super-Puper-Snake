import java.awt.Color;

public enum GameTheme {
    // 1. РЕТРО: Тот самый оригинальный ЗЕЛЁНЫЙ экран монохромной Nokia
    // Фон: каноничный зелёно-болотный (RGB: 133, 151, 101)
    // Стены и Яблоко: тёмные монохромные пиксели (RGB: 40, 45, 35)
    RETRO("Retro Nokia", new Color(133, 151, 101), new Color(40, 45, 35), new Color(40, 45, 35)),

    // 2. МОДЕРН: Светлая тема
    MODERN("Modern", new Color(240, 240, 240), new Color(180, 180, 180), new Color(255, 69, 0)),

    // 3. ДАРК МОД: Полночный стиль
    DARK_MODE("Dark Mode", new Color(35, 35, 50), new Color(0, 255, 204), new Color(255, 105, 180));

    private final String name;
    private final Color backgroundColor;
    private final Color wallColor;
    private final Color appleColor;

    GameTheme(String name, Color backgroundColor, Color wallColor, Color appleColor) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.wallColor = wallColor;
        this.appleColor = appleColor;
    }

    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getWallColor() { return wallColor; }
    public Color getAppleColor() { return appleColor; }

    @Override
    public String toString() {
        return name;
    }
}