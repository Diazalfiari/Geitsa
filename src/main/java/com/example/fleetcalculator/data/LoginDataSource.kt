package com.example.fleetcalculator.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fleetcalculator.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val context: Context) {

    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // Simple authentication - check if user exists or create new one
            val storedPassword = sharedPreferences.getString("password_$username", null)
            
            if (storedPassword == null) {
                // New user - register them
                sharedPreferences.edit()
                    .putString("password_$username", password)
                    .putString("display_name_$username", username)
                    .apply()
                
                val user = LoggedInUser(username, username)
                return Result.Success(user)
            } else if (storedPassword == password) {
                // Existing user - correct password
                val displayName = sharedPreferences.getString("display_name_$username", username) ?: username
                val user = LoggedInUser(username, displayName)
                return Result.Success(user)
            } else {
                // Existing user - wrong password
                return Result.Error(IOException("Invalid credentials"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // Clear current user session but keep registered users
        sharedPreferences.edit()
            .remove("current_user")
            .apply()
    }
}