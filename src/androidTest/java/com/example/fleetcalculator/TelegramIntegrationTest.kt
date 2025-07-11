package com.example.fleetcalculator

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fleetcalculator.data.LoginDataSource
import com.example.fleetcalculator.data.LoginRepository
import com.example.fleetcalculator.data.Result
import com.example.fleetcalculator.service.MessageComposer
import com.example.fleetcalculator.service.TelegramIntegrationHelper
import com.example.fleetcalculator.service.TelegramService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for Telegram integration functionality
 */
@RunWith(AndroidJUnit4::class)
class TelegramIntegrationTest {

    private lateinit var context: android.content.Context
    private lateinit var loginDataSource: LoginDataSource
    private lateinit var loginRepository: LoginRepository
    private lateinit var messageComposer: MessageComposer
    private lateinit var telegramService: TelegramService
    private lateinit var telegramHelper: TelegramIntegrationHelper

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        loginDataSource = LoginDataSource(context)
        loginRepository = LoginRepository(loginDataSource, context)
        messageComposer = MessageComposer()
        telegramService = TelegramService(context)
        telegramHelper = TelegramIntegrationHelper(context)
    }

    @Test
    fun testUserAuthentication() {
        // Test user registration
        val result = loginDataSource.login("testuser", "password123")
        assertTrue("User registration should succeed", result is Result.Success)
        
        // Test user login with correct credentials
        val loginResult = loginDataSource.login("testuser", "password123")
        assertTrue("User login should succeed", loginResult is Result.Success)
        
        // Test user login with wrong credentials
        val wrongLoginResult = loginDataSource.login("testuser", "wrongpassword")
        assertTrue("User login with wrong password should fail", wrongLoginResult is Result.Error)
    }

    @Test
    fun testMessageComposition() {
        // Create a test user
        val testUser = com.example.fleetcalculator.data.model.LoggedInUser("testuser", "Test User")
        
        // Test Hauler message composition
        val haulerMessage = messageComposer.composeHaulerMessage(
            user = testUser,
            vesselCapacity = 25.0,
            jarak = 500.0,
            kecepatan = 15.0,
            travelTime = 4.0,
            cycleTime = 8.0,
            swellFactor = 1.2,
            efisiensiKerja = 0.85,
            productivity = 159.38
        )
        
        assertTrue("Hauler message should contain user name", haulerMessage.contains("Test User"))
        assertTrue("Hauler message should contain vessel capacity", haulerMessage.contains("25.0"))
        assertTrue("Hauler message should contain productivity", haulerMessage.contains("159.38"))
        
        // Test Loader message composition
        val loaderMessage = messageComposer.composeLoaderMessage(
            user = testUser,
            fleet = "CAT 992K",
            unit = "Wheel Loader",
            bucketCapacity = 6.1,
            bucketFactor = 0.9,
            cycleTime = 45.0,
            efficiency = 0.83,
            productivity = 250.5
        )
        
        assertTrue("Loader message should contain user name", loaderMessage.contains("Test User"))
        assertTrue("Loader message should contain fleet", loaderMessage.contains("CAT 992K"))
        assertTrue("Loader message should contain productivity", loaderMessage.contains("250.5"))
    }

    @Test
    fun testTelegramConfiguration() {
        // Test Telegram enabled status when not configured
        assertFalse("Telegram should be disabled by default", telegramHelper.isTelegramEnabled())
        
        // Configure Telegram settings
        val sharedPreferences = context.getSharedPreferences("telegram_settings", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean("telegram_enabled", true)
            .putString("bot_token", "test_token")
            .putString("chat_id", "test_chat_id")
            .apply()
        
        // Test enabled status after configuration
        assertTrue("Telegram should be enabled after configuration", telegramHelper.isTelegramEnabled())
    }

    @Test
    fun testUserSessionManagement() {
        // Test login repository functionality
        val loginResult = loginRepository.login("sessiontestuser", "testpassword")
        assertTrue("Login should succeed", loginResult is Result.Success)
        assertTrue("User should be logged in", loginRepository.isLoggedIn)
        assertNotNull("User should not be null", loginRepository.user)
        
        // Test logout
        loginRepository.logout()
        assertFalse("User should not be logged in after logout", loginRepository.isLoggedIn)
        assertNull("User should be null after logout", loginRepository.user)
    }

    @Test
    fun testAppContext() {
        // Verify that the app context is working correctly
        assertEquals("com.example.fleetcalculator", context.packageName)
        
        // Test that preferences can be accessed
        val prefs = context.getSharedPreferences("test_prefs", android.content.Context.MODE_PRIVATE)
        assertNotNull("SharedPreferences should not be null", prefs)
    }
}