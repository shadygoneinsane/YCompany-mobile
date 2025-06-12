package com.ycompany.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SignInViewModel : ViewModel() {
    private val TAG = "GoogleSignIn"
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful && auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
                val user = auth.currentUser!!
                saveInDb(user)
                _signInState.value = SignInState.Success("Welcome ${user.displayName}")
            } else {
                _signInState.value = SignInState.Error("Firebase Auth Failed")
            }
        }
    }


    /**
     * Saves user data in Firestore database.
     * This function is called after successful sign-in with Google.
     */
    private fun saveInDb(user: FirebaseUser) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "photoUrl" to user.photoUrl?.toString(),
            "lastLogin" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(user.uid).set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User data added successfully")
                _signInState.value = SignInState.Proceed
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding user data", e)
                _signInState.value = SignInState.Error("Firestore error: ${e.message}")
            }
    }
}