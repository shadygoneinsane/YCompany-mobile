package com.ycompany.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signUpWithEmail(email: String, password: String): Resource<FirebaseUser> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            Resource.Success(user)
        } else {
            Resource.Error("Sign up failed")
        }
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Sign up failed")
    }

    suspend fun signInWithEmail(email: String, password: String): Resource<FirebaseUser> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            Resource.Success(user)
        } else {
            Resource.Error("Sign in failed")
        }
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Sign in failed")
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun signOut() = auth.signOut()
} 