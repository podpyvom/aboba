# Android Bluetooth Alarm App

A powerful Android 14 alarm application that automatically connects to Bluetooth speakers and plays alarm sounds with progressive volume control.

## Features

### 🔊 **Bluetooth Integration**
- Automatic connection to Bluetooth speakers
- Smart pre-alarm connection: automatically connects 1 minute before alarm triggers
- Handles Bluetooth speaker auto-disconnect after 30 minutes of inactivity
- Reliable audio routing through connected devices
- Support for paired device management

### ⏰ **Smart Alarm System**
- WorkManager-based reliable scheduling
- Progressive volume control (10% → 100% over 90 seconds)
- Custom audio file selection from device storage
- Background operation without root permissions

### 🎵 **Audio Management**
- MediaPlayer integration with proper audio focus
- Bluetooth audio routing priority
- Volume progression (1% increment every second)
- Fallback to default alarm sounds

### 🎨 **Modern UI**
- Dark theme minimalist design
- Material 3 Compose components
- Intuitive time picker and device selection
- Real-time connection status

## Technical Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Database**: Room for alarm storage
- **Dependency Injection**: Hilt
- **Background Work**: WorkManager + Foreground Services
- **Audio**: MediaPlayer with AudioManager integration

## Permissions Required

- `BLUETOOTH_CONNECT` & `BLUETOOTH_SCAN` (Android 12+)
- `SCHEDULE_EXACT_ALARM` for precise alarm timing
- `MODIFY_AUDIO_SETTINGS` for volume control
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` for background audio

## Installation

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API level 34 (Android 14)

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/android-bluetooth-alarm.git
   cd android-bluetooth-alarm
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

### GitHub Actions CI/CD

The project includes automated CI/CD pipeline that:
- Runs unit tests on every push
- Builds debug and release APKs
- Performs lint analysis
- Creates release artifacts automatically

## Core Problem Statement

This app solves a common issue: **Bluetooth speakers automatically disconnect after 30 minutes of inactivity**, causing alarms to play through the phone speaker instead of the intended Bluetooth device. 

**Solution**: The app automatically reconnects to your Bluetooth speaker **1 minute before** the alarm triggers, ensuring reliable audio playback through your preferred device.

## Usage

1. **Grant Permissions**: Allow Bluetooth and exact alarm permissions when prompted
2. **Set Alarm Time**: Use the time picker to set your desired alarm time
3. **Connect Bluetooth**: Scan and connect to your preferred Bluetooth speaker
4. **Select Audio**: Choose custom audio file or use default alarm sound
5. **Enable Alarm**: Toggle the alarm on and enjoy automatic Bluetooth connection

## Project Structure

```
app/
├── src/main/java/com/alarmapp/bluetoothalarm/
│   ├── data/           # Data models, database, repositories
│   ├── domain/         # Use cases and repository interfaces
│   ├── presentation/   # ViewModels and UI state
│   ├── ui/            # Compose UI components and screens
│   ├── service/       # Background services
│   ├── manager/       # Core managers (Bluetooth, Audio, etc.)
│   ├── receiver/      # Broadcast receivers
│   └── utils/         # Utility classes and extensions
├── src/main/res/      # Resources (layouts, strings, etc.)
└── src/test/          # Unit tests
```

## Key Components

### Core Managers
- **BluetoothManager**: Device discovery and connection management
- **AudioManager**: Audio playback and volume progression
- **AlarmScheduler**: WorkManager-based alarm scheduling
- **VolumeProgressionManager**: Progressive volume control

### Services
- **AlarmPlaybackService**: Foreground service for alarm playback
- **BluetoothMonitorService**: Background Bluetooth connection monitoring

### UI Components
- **MainScreen**: Primary app interface
- **AlarmSetupCard**: Alarm creation and management
- **BluetoothCard**: Device connection interface
- **AudioFileCard**: Audio file selection

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Troubleshooting

### Common Issues

**Bluetooth not connecting**: 
- Ensure device permissions are granted
- Check if Bluetooth is enabled
- Verify device is in pairing mode

**Alarm not triggering**:
- Check exact alarm permission is granted
- Verify app is not battery optimized
- Ensure alarm is enabled in the app

**Audio not routing to Bluetooth**:
- Confirm device is connected as audio device
- Check audio profile compatibility
- Try reconnecting the device

## Support

For issues and questions:
- Create an issue on GitHub
- Check existing issues for solutions
- Review the troubleshooting section

---

**Built with ❤️ for reliable morning wake-ups**