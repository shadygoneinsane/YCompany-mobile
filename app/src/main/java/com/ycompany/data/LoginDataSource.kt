package com.ycompany.data

import com.ycompany.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Resource<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            return Resource.Success(fakeUser)
        } catch (e: IOException) {
            return Resource.Error("Error logging in: ${e.message}")
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}