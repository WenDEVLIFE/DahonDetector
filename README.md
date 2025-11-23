# Dahon Detector - Plant Identification App 🌿

A full-featured Android app that uses **Google Gemini AI** to identify plants from photos and provides detailed information about them. All chat interactions are stored in **Firebase Firestore** for persistent access.

## Features ✨

- 📷 **Plant Scanning**: Take photos of plants using the camera
- 🤖 **AI-Powered Detection**: Uses Google Gemini 1.5 Flash to identify plants
- 💬 **Interactive Chat**: Ask questions about detected plants
- 🔥 **Firebase Integration**: Chat logs are stored and synced via Firestore
- 🔑 **API Key Management**: Secure local storage of your Gemini API key
- 📊 **Confidence Scoring**: See how confident the AI is in its identification
- 📝 **Chat History**: View and manage all your previous plant interactions

## Setup Instructions 🚀

### Prerequisites

1. **Android Studio** (Latest version recommended)
2. **Google Gemini API Key** - Get it from [Google AI Studio](https://makersuite.google.com/app/apikey)
3. **Firebase Project** - Already configured in this project

### Installation Steps

1. **Clone/Open the project** in Android Studio
2. **Sync Gradle** - Let Android Studio download all dependencies
3. **Build the project** - Make sure there are no errors
4. **Run on device or emulator** (Minimum SDK: 26)

### First Time Setup

1. **Launch the app**
2. Click on **"Setup API Key First"** button on the home screen
3. **Enter your Gemini API Key** (Get it from [Google AI Studio](https://makersuite.google.com/app/apikey))
4. Click **"Save API Key"**
5. You're ready to scan plants! 🎉

## How to Use 📱

### Scanning a Plant

1. From the **Home Screen**, tap **"Scan Plant"**
2. Grant **camera permissions** if prompted
3. Point your camera at a plant
4. Tap the **capture button** (📷)
5. Wait for the AI to analyze the image
6. View the detection results with:
   - Plant name (common and scientific)
   - Confidence level
   - Detailed description
   - Care instructions

### Asking Questions

1. After detecting a plant, scroll down to the **"Ask about this plant"** text field
2. Type your question (e.g., "How do I water this plant?")
3. Tap **"Ask Question"**
4. View the AI response below
5. The conversation is automatically saved to Firebase

### Viewing Chat Logs

1. From the **Home Screen**, tap **"📝 View Chat Logs"**
2. Browse all your previous plant conversations
3. Tap on any chat log to view details
4. Chat logs are synced across devices via Firebase

### Changing API Key

1. From the **Home Screen**, tap **"🔑 Change API Key"**
2. Enter a new API key
3. Save to update

## Project Structure 📁

```
app/src/main/java/com/example/dahondetector/
├── data/
│   ├── local/
│   │   └── ApiKeyManager.kt          # DataStore for API key storage
│   ├── model/
│   │   ├── ChatLog.kt                # Firebase-compatible chat log model
│   │   └── DetectionResult.kt        # Plant detection result model
│   ├── repository/
│   │   └── ChatLogRepository.kt      # Firebase Firestore operations
│   └── service/
│       └── GeminiService.kt          # Gemini AI API integration
├── ui/
│   ├── components/                    # Reusable UI components
│   ├── screens/
│   │   ├── ApiKeyScreen.kt           # API key setup screen
│   │   ├── CameraScreen.kt           # Camera capture screen
│   │   ├── ChatLogsScreen.kt         # Chat history screen
│   │   ├── DetectionResultScreen.kt  # Plant details & chat screen
│   │   └── HomeScreen.kt             # Main landing screen
│   └── theme/                        # Material 3 theming
├── util/
│   └── CameraManager.kt              # CameraX utility
├── viewmodel/
│   └── PlantDetectorViewModel.kt     # State management
└── MainActivity.kt                    # App entry point
```

## Technologies Used 🛠️

- **Kotlin** - Primary language
- **Jetpack Compose** - Modern UI toolkit
- **CameraX** - Camera functionality
- **Google Gemini AI** - Plant identification and chat
- **Firebase Firestore** - Cloud database for chat logs
- **DataStore** - Local storage for API key
- **Coil** - Image loading
- **Material 3** - Design system
- **Coroutines & Flow** - Asynchronous programming

## Permissions Required 📋

- **CAMERA** - To capture plant photos
- **INTERNET** - To communicate with Gemini AI and Firebase
- **READ_MEDIA_IMAGES** - To access captured images

## Firebase Configuration 🔥

The app is already configured with Firebase. The `google-services.json` file is included in the project.

**Firestore Collections:**
- `chatLogs` - Stores all plant detection conversations

## API Usage 💰

This app uses the **Google Gemini API** which has usage limits:
- Free tier: Limited requests per minute
- For production use, consider upgrading to a paid plan
- Monitor your usage at [Google AI Studio](https://makersuite.google.com/)

## Troubleshooting 🔧

### Camera not working
- Ensure camera permissions are granted
- Check if your device has a working camera
- Try restarting the app

### Plant detection fails
- Verify your API key is correct
- Check your internet connection
- Ensure you have available API quota
- Try with a clearer image

### Chat logs not loading
- Check internet connection
- Verify Firebase configuration
- Check Firestore security rules

### API Key issues
- Get a fresh API key from Google AI Studio
- Make sure you copied the entire key
- Try clearing app data and re-entering

## Building for Release 📦

1. Update `versionCode` and `versionName` in `app/build.gradle.kts`
2. Generate a signed APK/Bundle in Android Studio
3. Test thoroughly before release
4. Consider implementing ProGuard rules for optimization

## Future Enhancements 🚀

- [ ] Plant database with offline support
- [ ] Image gallery selection
- [ ] Share plant information
- [ ] Plant care reminders
- [ ] Multi-language support
- [ ] Dark theme improvements
- [ ] Export chat logs

## License 📄

This project is for educational purposes. Please ensure you comply with:
- Google Gemini API Terms of Service
- Firebase Terms of Service
- Android SDK License

## Credits 👏

- **Google Gemini AI** for plant identification
- **Firebase** for backend services
- **Android Jetpack** for modern development tools

## Support 💬

For issues or questions:
1. Check the troubleshooting section
2. Review Google Gemini API documentation
3. Check Firebase Firestore documentation

---

**Made with ❤️ using Kotlin and Jetpack Compose**

**Dahon** (Filipino word for "leaf") 🍃

