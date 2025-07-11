package com.example.fleetcalculator.data

import android.content.Context
import com.example.fleetcalculator.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource, private val context: Context) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
        
        // Try to restore user session
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("current_user", null)
        if (currentUserId != null) {
            val displayName = sharedPreferences.getString("display_name_$currentUserId", currentUserId) ?: currentUserId
            user = LoggedInUser(currentUserId, displayName)
        }
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // Store current user session
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("current_user", loggedInUser.userId)
            .apply()
    }
}