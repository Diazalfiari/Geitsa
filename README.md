# Geitsa Fleet Calculator

A comprehensive Android application for fleet management calculations with integrated Telegram bot messaging functionality.

## Features

### Core Functionality
- **Fleet Calculations**: 
  - Hauler productivity calculations (vessel capacity, travel time, cycle time)
  - Loader productivity calculations (bucket capacity, cycle time, efficiency)
  - Jumlah Hauler calculations (multiple calculation forms)
  - Matching Factor calculations
- **Calculation History**: All calculations are saved locally for future reference
- **User-Friendly Interface**: Intuitive design with clear input forms and result displays

### New Telegram Integration
- **Automatic Messaging**: Calculation results are automatically sent to Telegram groups/channels
- **User Authentication**: Secure login system with user session management
- **Message Formatting**: Professional formatted messages with user info, timestamps, and detailed results
- **Configuration Management**: Easy setup and management of Telegram bot credentials
- **Error Handling**: Graceful handling of network errors and configuration issues

## Technical Architecture

### Components
- **Authentication System**: LoginDataSource, LoginRepository, LoginViewModel
- **Telegram Integration**: TelegramService, MessageComposer, TelegramIntegrationHelper
- **Configuration**: SettingsActivity for bot configuration
- **Calculation Activities**: Hauler, LoaderActivity, Jumlah_Hauler, Matching_Factor

### Security Features
- Secure storage of bot tokens and user credentials
- HTTPS communication for all network requests
- Local data encryption using Android's SharedPreferences
- No sensitive data transmitted to third parties

## Setup Instructions

### 1. Building the Application
```bash
# Clone the repository
git clone https://github.com/Diazalfiari/Geitsa.git
cd Geitsa

# Build the application using Android Studio or Gradle
./gradlew assembleDebug
```

### 2. Installing Dependencies
The application uses standard Android libraries:
- AndroidX components
- Kotlin coroutines for asynchronous operations
- Standard Android networking (HttpURLConnection)

### 3. Telegram Bot Setup
See [TELEGRAM_SETUP.md](TELEGRAM_SETUP.md) for detailed instructions on:
- Creating a Telegram bot
- Configuring bot tokens and chat IDs
- Setting up the application

## Usage

### User Authentication
1. Launch the application
2. Create a new account or login with existing credentials
3. New users are automatically registered on first login

### Performing Calculations
1. Login to the application
2. Select a calculation type from the main menu
3. Enter the required parameters
4. Tap "Calculate" to get results
5. Results are displayed and optionally sent to Telegram

### Telegram Configuration
1. Access Settings from the main menu
2. Enable Telegram integration
3. Enter your bot token and chat ID
4. Test the connection
5. Save settings

## Message Format

Telegram messages include:
- User information (name, calculation timestamp)
- Input parameters used in the calculation
- Detailed calculation results
- Calculation type identifier

Example:
```
📊 Hauler Calculation Result

👤 User: John Doe
📅 Date: 15/12/2024 14:30

Input Parameters:
• Vessel Capacity: 25.0 m³
• Distance: 500 m
• Speed: 15 km/h
• Swell Factor: 1.2
• Work Efficiency: 0.85

Calculation Results:
• Travel Time (Round Trip): 4.00 minutes
• Cycle Time: 8.00 minutes
• Productivity: 159.38 m³/hour

🚛 Calculated via Geitsa Fleet Calculator
```

## File Structure

```
src/main/java/com/example/fleetcalculator/
├── data/
│   ├── LoginDataSource.kt          # User authentication logic
│   ├── LoginRepository.kt          # User session management
│   ├── Result.kt                   # Result wrapper class
│   └── model/
│       └── LoggedInUser.kt         # User data model
├── service/
│   ├── TelegramService.kt          # Telegram Bot API communication
│   ├── MessageComposer.kt          # Message formatting
│   └── TelegramIntegrationHelper.kt # Integration coordination
├── ui/login/
│   ├── LoginViewModel.kt           # Login business logic
│   ├── LoginViewModelFactory.kt    # ViewModel factory
│   └── [other login UI components]
├── MainActivity.kt                 # Main application entry point
├── LoginActivity.kt                # User authentication UI
├── SettingsActivity.kt             # Telegram configuration UI
├── Hauler.kt                       # Hauler calculations
├── LoaderActivity.kt               # Loader calculations
├── Jumlah_Hauler.kt               # Jumlah Hauler calculations
└── [other calculation activities]
```

## Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing
1. Test user authentication flow
2. Test each calculation type
3. Test Telegram integration with valid bot credentials
4. Test error scenarios (network failures, invalid credentials)

## API Requirements

### Minimum Android Version
- API Level 21 (Android 5.0)
- Target SDK: Latest stable version

### Network Permissions
- `INTERNET`: Required for Telegram API communication
- `ACCESS_NETWORK_STATE`: For network status checking

## Troubleshooting

### Common Issues
1. **"Connection failed" error**: Check bot token and chat ID
2. **"No user logged in" error**: Ensure user is authenticated
3. **Messages not appearing**: Verify bot permissions in Telegram group
4. **App crashes**: Check logs for missing dependencies or permissions

### Debug Information
Enable debug logging by setting the log level in TelegramService:
```kotlin
private const val DEBUG = true
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support or questions:
- Create an issue on GitHub
- Refer to the troubleshooting section in TELEGRAM_SETUP.md
- Check the application logs for error details

## Version History

### v1.0.0 (Current)
- Initial release with Telegram integration
- User authentication system
- Comprehensive calculation suite
- Telegram bot messaging
- Configuration management
- Error handling and user feedback

## Acknowledgments

- Android development team for the robust platform
- Telegram Bot API for messaging capabilities
- Open source community for guidance and best practices