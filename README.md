# Retro & Modern Snake Game

Ein klassisches **Snake-Spiel**, entwickelt in Java unter Verwendung von **Java Swing** und **AWT**. Das Projekt kombiniert traditionelles Arcade-Gameplay mit modernen Features, dynamischen Grafik-Themes und einer integrierten Highscore-Verwaltung.

---

## 🚀 Features

* **Dynamische Themes (GameTheme Enum):** * `Retro Nokia`: Eine nostalgische Emulation des klassischen, grün-monochromen Nokia 3210 Displays mit pixelgenauer Kontrastanpassung.
  * `Modern`: Ein klares, helles Design mit sanften Farben und optimierter Lesbarkeit.
  * `Dark Mode`: Ein tiefes, mitternachtsblaues Neon-Design für nächtliche Sessions.
* **Intelligente Eingabesteuerung:** Behebung des klassischen "Input Buffer Bugs". Schnelle 180-Grad-Kehrtwenden, die zu einem fehlerhaften Selbsteinschlag führen könnten, werden auf logischer Ebene (durch `lastPhysicalDirection`) blockiert.
* **Spezial-Essen & Hindernisse:** Neben normalen Äpfeln spawnen zufällig goldene "Spezial-Äpfel" mit einem Ablauf-Timer (+3 Punkte). Fünf zufällig generierte Hindernisse (Ziegelsteine) erhöhen den Schwierigkeitsgrad pro Level.
* **Dynamisches Level-System:** Alle 10 gefressenen Äpfel steigt der Spieler ein Level auf. Jedes Level erhöht die Geschwindigkeit der Schlange automatisch.
* **Persistente Highscores:** Automatische Speicherung der Top-5-Spieler (Name, Score, Datum) in einer externen Datei über den `HighScoreManager`.
* **Integrierte Soundeffekte:** Synchrone Audio-Ausgabe bei Interaktionen (Fressen, Game Over) über den `SoundManager`.

---

## 🛠️ Architektur & Klassendesign

Das Projekt folgt einer klaren objektorientierten Struktur (OOP):

* **`Main.java`**: Der Einstiegspunkt der Anwendung, der das GUI im Event-Dispatch-Thread startet.
* **`LoginFenster.java`**: Das Startmenü zur Erfassung des Spielernamens, der Schlangenfarbe und des gewünschten Designs (`GridBagLayout`).
* **`SpielPanel.java`**: Das Herzstück des Spiels. Verwaltet den Spielzyklus (`Timer`), die Kollisionsprüfung, das Rendering (`paintComponent`) und die Tastatureingaben (`KeyAdapter`).
* **`GameTheme.java`**: Ein zentrales `Enum`, das alle Farbpaletten für Hintergründe, Wände und Äpfel steuert.
* **`HighScoreManager.java` & `SoundManager.java`**: Utility-Klassen zur Entlastung der UI-Logik (Dateiverwaltung und Audio-Schnittstelle).

---

## 📦 Voraussetzungen & Installation

### Voraussetzungen
* **Java Development Kit (JDK) 17** oder höher.
* Eine gängige Java-IDE (z. B. IntelliJ IDEA, Eclipse) oder CLI-Tools.

### Installation & Start via Terminal
1. Klonen Sie das Repository:
   ```bash
   git clone [https://github.com/DEIN_BENUTZERNAME/snake-game-java.git](https://github.com/DEIN_BENUTZERNAME/snake-game-java.git)
