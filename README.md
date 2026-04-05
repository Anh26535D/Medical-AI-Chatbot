# Medical AI Chatbot (Sức Khỏe Việt AI)

A modern Android application providing AI-powered medical consultation and health companionship.

## Features

- **AI Medical Consultation**: Powered by Google's Gemini AI (Firebase AI) to provide health-related information and advice.
- **User Authentication**: Secure local authentication system using Room database.
- **Modern UI/UX**: Built entirely with Jetpack Compose following Material 3 design guidelines.
- **Seamless Navigation**: Smooth transitions between Splash, Onboarding, Authentication, and Home screens.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (for local data persistence)
- **AI Integration**: Firebase AI (Gemini API)
- **Dependency Management**: Gradle Version Catalog (libs.versions.toml)
- **JVM Target**: 17

## Setup Requirements

- Android Studio Koala or newer.
- JDK 17.
- Firebase project with Gemini AI enabled.

## Firebase AI Setup Guide

To get the AI features working, you need to connect the app to your own Firebase project:

1. **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Add an Android App**: Register your app with the package name `edu.hust.medicalaichatbot`.
3. **Download configuration**: Download the `google-services.json` file and place it in the `app/` directory of this project.
4. **Enable Gemini AI for Firebase**: In the Firebase console, go to **Build > Gemini** (or Vertex AI depending on your console version) and click **Get Started**. This enables the Gemini AI capabilities for your project.
5. **Check API Keys**: Ensure that your API keys have permissions to access the Gemini AI services in the Google Cloud Console.

For detailed instructions, refer to the [official Firebase documentation for Gemini](https://firebase.google.com/docs/gemini).
