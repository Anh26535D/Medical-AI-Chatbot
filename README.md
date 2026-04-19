# Medical AI Chatbot (Sức Khỏe Việt AI)

A modern Android application providing AI-powered medical consultation and health companionship.

## Features

- **AI Medical Consultation**: Powered by Google's Gemini AI (Firebase AI) to provide health-related information and advice.
- **User Authentication**: Secure local authentication system using Room database.
- **Modern UI/UX**: Built entirely with Jetpack Compose following Material 3 design guidelines.
- **Seamless Navigation**: Smooth transitions between Splash, Onboarding, Authentication, and Home screens.
- **Health Profile**: Manage personal health information and medical history.
- **Location-based Services**: Find nearby medical facilities and pharmacies.

## Architecture: Clean Architecture + MVVM

The project follows **Clean Architecture** principles combined with **MVVM** to ensure scalability, maintainability, and testability.

### 1. Presentation Layer (UI & ViewModel)
- **Framework**: Jetpack Compose.
- **ViewModel**: Manages UI state and communicates with the Domain layer via Use Cases.
- **Components**: `ui/screens`, `ui/viewmodel`, `ui/theme`.

### 2. Domain Layer (Business Logic)
- **Entities**: Plain Kotlin objects representing the core business models (e.g., `ChatThread`, `UserProfile`).
- **Use Cases**: Encapsulates specific business rules (e.g., `GetThreadsUseCase`, `SendMessageUseCase`).
- **Repository Interfaces**: Defines contracts for data operations, decoupled from implementation.

### 3. Data Layer (Implementation)
- **Repositories**: Implements the Domain interfaces, coordinating data from multiple sources (Local, Remote, AI).
- **Local (Room)**: Handles persistence of chat history and user profiles.
- **AI (Firebase AI)**: Manages communication with Gemini API.
- **Mappers**: Converts between Data Entities and Domain Models.

## Directory Structure
```
app/src/main/java/edu/hust/medicalaichatbot/
├── data/               # Data Layer (Repo impl, Room, AI services)
│   ├── local/          # Room DB, DAOs, Entities
│   ├── repository/     # Repository implementations
│   ├── service/        # External services (Location, Places)
│   └── mapper/         # Data converters
├── domain/             # Domain Layer (Business Logic)
│   ├── model/          # Business Entities
│   ├── repository/     # Repository Interfaces
│   └── usecase/        # Use Cases (Interactors)
├── ui/                 # Presentation Layer
│   ├── screens/        # Compose Screen UI
│   ├── viewmodel/      # UI Logic & State
│   └── theme/          # Material 3 Styling
└── utils/              # Common Utilities & Helpers
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Clean Architecture + MVVM
- **Database**: Room (with Paging 3 support)
- **AI Integration**: Firebase AI (Gemini API)
- **Dependency Injection**: Manual (Constructor Injection)
- **JVM Target**: 21
- **Gradle**: Kotlin DSL with Version Catalog

## Setup Requirements

- Android Studio Ladybug or newer.
- JDK 21.
- Firebase project with Gemini AI enabled.

## Firebase AI Setup Guide

To get the AI features working, you need to connect the app to your own Firebase project:

1. **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Add an Android App**: Register your app with the package name `edu.hust.medicalaichatbot`.
3. **Download configuration**: Download the `google-services.json` file and place it in the `app/` directory of this project.
4. **Enable Gemini AI for Firebase**: In the Firebase console, go to **Build > Gemini** (or Vertex AI depending on your console version) and click **Get Started**. This enables the Gemini AI capabilities for your project.
5. **Check API Keys**: Ensure that your API keys have permissions to access the Gemini AI services in the Google Cloud Console.

For detailed instructions, refer to the [official Firebase documentation for Gemini](https://firebase.google.com/docs/gemini).
