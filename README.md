# Medical AI Chatbot (Sức Khỏe Việt AI)

A modern Android application providing AI-powered medical consultation and health companionship.

## Hướng dẫn trải nghiệm (User Trial Guide)

Để trải nghiệm đầy đủ các tính năng của **Sức Khỏe Việt AI**, người dùng có thể thực hiện theo các bước sau:

1.  **Khởi đầu**: Xem qua các màn hình Giới thiệu (Onboarding) để nắm bắt giá trị cốt lõi của ứng dụng.
2.  **Đăng ký/Đăng nhập**: Tạo tài khoản cá nhân (dữ liệu được lưu cục bộ bảo mật).
3.  **Tư vấn AI**:
    *   Tại màn hình chính, chọn các **Câu hỏi gợi ý** (ví dụ: "Tôi bị đau đầu và sốt") hoặc nhập triệu chứng trực tiếp.
    *   Sử dụng các nút **Trả lời nhanh** (Quick Replies) ở phía trên thanh nhập liệu để tương tác nhanh với AI.
4.  **Theo dõi phân loại (Triage)**:
    *   Quan sát các nhãn màu: **Xanh** (Theo dõi tại nhà), **Vàng** (Cần khám bác sĩ), **Đỏ** (Cấp cứu).
    *   Nhấp vào phần "Phân tích từ AI" để xem chi tiết dự đoán và các triệu chứng ghi nhận.
5.  **Quản lý lịch sử**: Xem lại các cuộc hội thoại cũ tại tab **Lịch sử**.
6.  **Tra cứu cơ sở y tế**: Sử dụng tính năng bản đồ để tìm bệnh viện hoặc nhà thuốc gần nhất.

## Các tính năng chính (Key Features)

- **🤖 Trợ lý AI thông minh**: Tư vấn sức khỏe dựa trên công nghệ Gemini AI, phản hồi tự nhiên và chuyên sâu.
- **🏥 Phân loại bệnh tự động (Triage)**: Hệ thống tự động đánh giá mức độ khẩn cấp của triệu chứng theo chuẩn y tế.
- **📋 Tóm tắt bệnh án**: Tự động tổng hợp thông tin từ cuộc hội thoại để người dùng dễ dàng trình bày với bác sĩ.
- **📂 Quản lý lịch sử tư vấn**: Lưu trữ toàn bộ lộ trình sức khỏe cá nhân và gia đình.
- **📍 Tìm kiếm cơ sở y tế**: Tích hợp bản đồ tìm kiếm bệnh viện, phòng khám và nhà thuốc xung quanh.
- **📱 Giao diện Adaptive**: Thiết kế hiện đại, mượt mà và tự động tương thích với mọi kích thước màn hình điện thoại.

## Features

- **AI Medical Consultation**: Powered by Google's Gemini AI (Firebase AI) to provide health-related information and advice.
- **Triage & Assessment**: Automatic classification of health issues (GREEN, YELLOW, RED) based on AI analysis.
- **User Authentication**: Secure local authentication system using Room database.
- **Modern UI/UX**: Built entirely with Jetpack Compose following Material 3 design guidelines.
- **Health Profile**: Manage personal health information and medical history.
- **Location-based Services**: Find nearby medical facilities and pharmacies.

## Architecture: Clean Architecture + MVVM

The project follows **Clean Architecture** principles combined with **MVVM** to ensure scalability, maintainability, and testability.

### 1. Presentation Layer (UI & ViewModel)
- **Framework**: Jetpack Compose.
- **Navigation**: Type-safe navigation using Compose Navigation.
- **State Management**: ViewModel with StateFlow for reactive UI updates.

### 2. Domain Layer (Business Logic)
- **Entities**: Core models like `ChatThread`, `UserProfile`, `TriageTag`.
- **Use Cases**: Encapsulates specific logic like `SendMessageUseCase` or `ProcessAiResponseUseCase`.

### 3. Data Layer (Implementation)
- **Repositories**: Coordinates data from Room (local history), Firebase AI (Gemini), and Location services.
- **AI Integration**: Custom `ChatResponseParser` for structured output from raw LLM text.

## Directory Structure
```
app/src/main/java/edu/hust/medicalaichatbot/
├── data/               # Data Layer (Repo impl, Room, AI services, Tag Parsers)
├── domain/             # Domain Layer (Business Entities & Use Cases)
├── ui/                 # Presentation Layer (Compose UI, ViewModels, Theme)
└── utils/              # Common Utilities & Constants
```

## Tech Stack

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Clean Architecture + MVVM
- **Database**: Room (with Paging 3 support)
- **AI Integration**: Firebase AI (Gemini API)
- **Navigation**: Compose Navigation
- **Concurrency**: Kotlin Coroutines & Flow
- **JVM Target**: 21

## Setup Requirements

- Android Studio Ladybug or newer.
- JDK 21.
- Firebase project with Gemini AI enabled.
- `google-services.json` placed in the `app/` directory.

## Firebase AI Setup Guide

To get the AI features working, you need to connect the app to your own Firebase project:

1. **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Add an Android App**: Register your app with the package name `edu.hust.medicalaichatbot`.
3. **Download configuration**: Download the `google-services.json` file and place it in the `app/` directory of this project.
4. **Enable Gemini AI for Firebase**: In the Firebase console, go to **Build > Gemini** (or Vertex AI depending on your console version) and click **Get Started**. This enables the Gemini AI capabilities for your project.
5. **Check API Keys**: Ensure that your API keys have permissions to access the Gemini AI services in the Google Cloud Console.

For detailed instructions, refer to the [official Firebase documentation for Gemini](https://firebase.google.com/docs/gemini).
