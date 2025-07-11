# Geitsa Fleet Calculator - Telegram Integration

## Overview
The Geitsa Fleet Calculator app now includes Telegram integration that automatically sends calculation results to a specified Telegram group or channel after each calculation is performed.

## Features
- **User Authentication**: Simple username/password login system
- **Telegram Integration**: Automatic sending of calculation results to Telegram
- **Configuration Management**: Easy setup of Telegram bot credentials
- **Secure Storage**: Bot tokens and user credentials are stored securely
- **Error Handling**: Graceful handling of network errors and configuration issues

## Setup Instructions

### 1. User Authentication
- On first launch, users need to create an account by entering a username and password
- The system will automatically register new users
- Existing users can log in with their credentials
- Users can logout from the main menu

### 2. Telegram Bot Setup

#### Step 1: Create a Telegram Bot
1. Open Telegram and search for `@BotFather`
2. Send `/newbot` command to BotFather
3. Follow the instructions to create your bot
4. Save the **Bot Token** provided by BotFather

#### Step 2: Set Up a Group/Channel
1. Create a new Telegram group or channel where you want to receive calculation results
2. Add your bot to the group/channel as an administrator
3. Get the **Chat ID** of your group/channel:
   - Add `@getidsbot` to your group and send `/start`
   - The bot will respond with the Chat ID (e.g., `-1001234567890`)

#### Step 3: Configure the App
1. Open the Geitsa Fleet Calculator app
2. Login with your credentials
3. Tap the Settings button (⚙️) in the top-right corner of the main screen
4. Enable "Telegram Integration" toggle
5. Enter your **Bot Token** (from Step 1)
6. Enter your **Chat ID** (from Step 2)
7. Tap "Test Connection" to verify the setup
8. Tap "Save Settings" to save your configuration

## Usage

### Performing Calculations
1. Login to the app with your credentials
2. Navigate to any calculation module (Hauler, Loader, etc.)
3. Enter the required parameters
4. Tap "Calculate" or "Hitung"
5. The calculation result will be displayed
6. If Telegram integration is enabled, the result will be automatically sent to your configured Telegram group/channel

### Message Format
Each Telegram message includes:
- **User**: Name of the logged-in user who performed the calculation
- **Date**: Timestamp of when the calculation was performed
- **Input Parameters**: All input values used in the calculation
- **Results**: The calculated output values
- **Calculation Type**: Type of calculation performed (Hauler, Loader, etc.)

### Example Message
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

## Configuration Management

### Settings Screen
The settings screen allows you to:
- Enable/disable Telegram integration
- Configure Bot Token and Chat ID
- Test the connection to verify setup
- Save configuration settings

### Security Notes
- Bot tokens are stored securely in the app's private storage
- User credentials are encrypted and stored locally
- Network communications use HTTPS for security
- No sensitive data is transmitted to third parties

## Troubleshooting

### Common Issues

#### "Connection failed" error
- Verify your Bot Token is correct
- Ensure your bot is added to the target group/channel
- Check that the Chat ID is correct (should start with `-` for groups)
- Verify your internet connection

#### "No user logged in" error
- Make sure you're logged in before performing calculations
- Try logging out and logging back in

#### Messages not appearing in Telegram
- Check that your bot has permission to send messages in the group/channel
- Verify the Chat ID is correct
- Ensure Telegram integration is enabled in settings

#### "Telegram not configured" error
- Go to Settings and configure your Bot Token and Chat ID
- Enable Telegram integration
- Test the connection before use

### Support
For technical support or questions about the Telegram integration, please contact the development team.

## Version Information
- **Feature**: Telegram Integration
- **Version**: 1.0
- **Compatibility**: Android 5.0+ (API Level 21+)
- **Dependencies**: None (uses standard Android networking)

## Privacy Policy
The app only sends calculation results to your configured Telegram group/channel. No personal data is shared with third parties. Bot tokens and user credentials are stored securely on your device.