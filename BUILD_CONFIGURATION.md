# Build Configuration Example

## build.gradle (Module: app)

```gradle
android {
    compileSdk 34

    defaultConfig {
        applicationId "com.example.fleetcalculator"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'
    implementation 'androidx.annotation:annotation:1.7.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

## build.gradle (Project level)

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'com.android.library' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

## Key Dependencies

### Core Android Libraries
- **AndroidX Core**: Modern Android development components
- **AppCompat**: Backward compatibility for modern Android features
- **Material Design**: Google's material design components
- **ConstraintLayout**: Advanced layout management

### Lifecycle Management
- **Lifecycle LiveData**: Reactive data observation
- **Lifecycle ViewModel**: UI data management
- **Navigation Components**: Fragment navigation

### Asynchronous Operations
- **Kotlin Coroutines**: Asynchronous programming support
- **Coroutines Android**: Android-specific coroutine support

### Testing
- **JUnit**: Unit testing framework
- **Espresso**: UI testing framework
- **AndroidX Test**: Android testing utilities

## Network Configuration

The application uses Android's built-in networking capabilities:
- **HttpURLConnection**: Standard HTTP client
- **URLEncoder**: URL encoding for API parameters
- **HTTPS**: Secure communication with Telegram API

## Proguard Configuration

If using code obfuscation, add these rules to `proguard-rules.pro`:

```proguard
# Keep Telegram service classes
-keep class com.example.fleetcalculator.service.** { *; }

# Keep data model classes
-keep class com.example.fleetcalculator.data.model.** { *; }

# Keep login related classes
-keep class com.example.fleetcalculator.data.** { *; }

# Keep coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
```

## Manifest Permissions

Ensure these permissions are in your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Build Commands

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

## Notes

- Minimum SDK 21 (Android 5.0) required for modern networking features
- Target SDK should be the latest stable version
- Kotlin coroutines are used for asynchronous operations
- No external networking libraries required (uses standard Android HTTP client)
- All dependencies are standard Android/Kotlin libraries