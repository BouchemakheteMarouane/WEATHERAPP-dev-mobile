# 🌦️ WeatherApp

A modern, high-performance Android weather application designed with a clean architecture and a polished user experience. Built using **Jetpack Compose**, this project serves as a practical demonstration of modern Android development standards.

---

## 📱 Overview

WeatherApp provides real-time weather tracking with a dynamic, responsive UI. It emphasizes maintainability and scalability by strictly adhering to industry-standard design patterns.

### ✨ Key Features
- **Real-Time Data:** Fetches live weather information via the OpenWeather API.
- **Dynamic UI:** Interface adapts visually to varying weather conditions.
- **Search Functionality:** Intuitive city-based lookup.
- **Robust Architecture:** Built with clean MVVM principles for clear separation of concerns.
- **Secure Input:** Implements input sanitization to ensure data integrity.

---

## 🛠️ Technical Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose & Material 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Hilt
- **Networking:** Retrofit
- **Asynchronous Work:** Coroutines & Flow
- **Data Source:** OpenWeather API

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable version recommended)
- An active API key from [OpenWeather](https://openweathermap.org/)

### Installation
Clone the repository to your local machine:
```bash
git clone https://github.com/marouanekassimi/WeatherApp.git
```

### Configuration
1. Open the project in Android Studio.
2. Create a `local.properties` file in the project root if it does not exist.
3. Add your API key to the file:
```text
OPEN_WEATHER_MAP_API_KEY=your_api_key_here
```

---


## 🔐 Security

This project was designed and reviewed using an OWASP-inspired checklist for mobile applications.  
Key points:

- **Input Validation:** User input (city search) is sanitized and validated before being sent to the API.
- **Network Security:** All API calls use HTTPS with secure network configuration.
- **Secrets Management:** The OpenWeather API key is injected via `local.properties` / `BuildConfig` and never hardcoded.
- **Error Handling:** User-facing errors avoid exposing internal stack traces or sensitive details.
- **Hardening:** Release builds use code obfuscation (R8/ProGuard) and minimized logging.
---


## 🔮 Future Roadmap
- 📍 Auto-location weather detection
- 🌙 Dark / Light theme toggle
- 📊 Hourly weather visualization charts
- 🔔 Intelligent weather notifications
- 🌐 Multi-language support

---

## 👨‍💻 Author
**Zayd Kassimi**  
GitHub: [marouanekassimi](https://github.com/marouanekassimi)

---

## 📄 License
This project is intended for educational and portfolio purposes only.
