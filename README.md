# Cognitio

<p align="center">
    <img src="composeApp/src/desktopMain/resources/logo-transparent.png" width="25%" alt="Cognitio App">
</p>

**Cognitio** is a powerful Android and Desktop app designed to help users deepen their understanding of various concepts. Whether you're learning a new subject or exploring a new topic, Cognitio uses cutting-edge generative artificial intelligence (AI) from **Google Gemini** to create custom quizzes, test your knowledge, and grade your responses. This app allows users to input a subject and topic of choice and even upload a document (such as a reading) to be quizzed on.

Unlike traditional quiz apps that only assess multiple-choice questions, **Cognitio** empowers users by grading short-answer and long-answer questions too, helping to improve writing skills and critical thinking. This makes it the perfect tool for both students and professionals who want to test their understanding and enhance their knowledge.

---

## Features

- **Custom Quizzes**: Input any subject or topic and get AI-generated quiz questions tailored to that subject.
- **Document-Based Quizzes**: Upload a document and the app will generate questions based on its content.
- **AI-Powered Grading**: Unlike most quiz apps, Cognitio grades multiple-choice, short-answer, and long-answer questions.
- **Cross-Platform Support**: Works seamlessly on Android and Desktop platforms (Windows & macOS).
- **Easy-to-Use Interface**: Simple design and user-friendly navigation for a smooth experience.

---

## Key Screens

- **Home Screen**: Provides a summary of your recent quizzes, allowing you to quickly start new quizzes or review previous ones.
- **Quiz Creation Screen**: Enter the subject and topic, optionally upload a document, and generate a quiz. You can then take the quiz and see your performance.
- **Quiz Interface**: Answer multiple-choice, short-answer, and long-answer questions, with real-time grading powered by Google Gemini.
- **Settings Screen**: Configure your app by entering and validating your API key. The app ensures compatibility with the free GEMINI-1.5-FLASH version, enabling seamless quiz generation and grading.

---

## Installation & Setup

### 1. Environment Setup

#### Requirements:

- **For Android**:
    - Android Studio
    - Android Virtual Device (AVD) or physical Android device for testing
    - Kotlin Multiplatform Plugin installed in Android Studio

- **For Desktop (Windows/macOS)**:
    - IntelliJ IDEA or Android Studio
    - JDK 11 or later
    - Android SDK for building Android version (optional if targeting desktop only)

#### Set up the API Key:
1. You need an API key from **Google Gemini** to generate quizzes and grade them.
2. You can obtain the key from [this link](https://aistudio.google.com/app/apikey). You can also view the [Demo Video](#demo).
3. After obtaining the API key, open the **Settings** section in the app and paste the key into the appropriate field.

---

### 2. Running on Android

To run your application on an Android emulator or device:

1. **Create an Android Virtual Device** in Android Studio if you don't have one already.
2. In **Android Studio**, open the **androidApp** configuration.
3. Select your Android device (virtual or real).
4. Hit **Run** to deploy the app to the device/emulator.

Alternatively, you can use Gradle for building and running the project:

```bash
./gradlew run
```

---

### 3. Running on Desktop (Windows/macOS)

#### On Windows/macOS:

1. **Clone the repository** to your local machine if you haven’t already.
2. Open the project in IntelliJ IDEA or Android Studio.
3. To run the app, select the **desktopMain** configuration.
4. Ensure all necessary resources (like the app icon and API key) are in place.
5. Type the following command in the terminal opened in the project directory.

```bash
./gradlew run
```

---

## Troubleshooting
- If you encounter errors related to **API key** or quiz generation, make sure you've correctly entered the key in the **Settings** section, and that the API Key is for the **FREE** version of gemini-1.5-flash. You can watch the [Demo Video](#demo) to get instructions on acquiring an API Key.

---

## Libraries Used

- **Google Gemini** (AI) for quiz generation and grading
- **Jetpack Compose** for UI
- **OkHttp** and **Ktor** for networking and HTTP requests
- **Gson** for JSON handling
- **Apache PDFBox** for PDF text extraction
- **Apache POI** for DOCX text extraction

---

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

---

## Demo

To see the app in action, watch the demo video below:

<video width="640" height="360" controls>
  <source src="composeApp/src/desktopMain/resources/Cognitio.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>