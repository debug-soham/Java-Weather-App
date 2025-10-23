# Weather Information App
A modern, desktop weather application built with Java Swing. It fetches live weather data from the OpenWeatherMap API and displays it in a custom-designed, pixel-perfect UI.

## Final Application Preview
<img width="1919" height="1035" alt="image" src="https://github.com/user-attachments/assets/2491cf2c-8894-43fd-bd65-9e676570b1b2" />

<br>

## Core Features
- **Live Weather Data:** Search any city for real-time weather conditions.
- **Detailed Display:** Shows current temperature, weather description, wind speed, humidity, sunrise, and sunset times.
- **5-Day Forecast:** Includes a multi-day weather forecast.
- **Search History:** Displays the last two searched cities for quick reference.
- **Custom Modern UI:** Features a dark midnight blue theme, rounded corners, custom fonts (`Montserrat`), and a pixel-perfect layout.

## Design and Implementation
This project was designed to meet and exceed the requirements of the course rubric:
-   **Use of OOP Concepts:** Strong application of OOP principles, including custom classes (`WeatherApiClient`, `RoundedPanel`), encapsulation in data models, and polymorphism by extending Swing components.
-   **GUI Design & Event Handling:** A user-friendly, pixel-perfect GUI built with **Java Swing**. Event handling is implemented via `ActionListener` and `FocusListener` for an interactive experience.
-   **Use of Package Concept:** The project follows a modular design with code organized into distinct packages (`api`, `gui`, `model`, `util`) for maintainability.
-   **Functionality & Output:** The application is fully functional, fetching and displaying live data correctly and handling errors gracefully with `try-catch` blocks and user dialogs.
-   **Project Understanding:** The problem is clearly defined and the scope is realistic, resulting in a complete and polished application.

## Project Structure
```
Java-Weather-App/
├── .gitignore
├── README.md
├── lib/                  # Required .jar libraries
└── src/
    ├── com/weatherapp/
    │   ├── api/          # Handles API communication
    │   ├── gui/          # All GUI components
    │   ├── main/         # Main entry point
    │   ├── model/        # Data model classes
    │   └── util/         # Utility classes
    └── resources/
        ├── assets/       # Weather condition icons
        ├── fonts/        # Custom font files
        └── icons/        # UI element icons
```

## Quick Setup Guide

1.  **Prerequisites:**
    * JDK 17 or later.
    * IntelliJ IDEA.

2.  **Clone the Repository:**
    * In IntelliJ, go to `Get from VCS` and paste the repository URL.

3.  **Configure Project:**
    * **Set JDK:** `File > Project Structure > Project > SDK`.
    * **Add Libraries:** `File > Project Structure > Libraries > + > Java` > select all `.jar` files in the `lib` folder.
    * **Mark Resources:** In `Modules`, right-click the `src/resources` folder and `Mark as > Resources Root`.

4.  **Add API Key:**
    * Get a free API key from [OpenWeatherMap](https://openweathermap.org/).
    * Open `src/com/weatherapp/api/WeatherApiClient.java`.
    * Paste your key into the `API_KEY` variable.

5.  **Run:**
    * `Build > Rebuild Project`.
    * Open `src/com/weatherapp/main/Main.java` and click the green play icon to run.

