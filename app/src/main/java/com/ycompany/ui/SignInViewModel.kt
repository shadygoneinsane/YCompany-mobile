package com.ycompany.ui

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ycompany.R
import com.ycompany.data.AuthRepository
import com.ycompany.data.Constants
import com.ycompany.data.Resource
import com.ycompany.ui.base.BaseViewModel
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val resourceProvider: ResourceProvider,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    fun firebaseAuthWithGoogle(idToken: String) {
        _signInState.value = SignInState.Loading
        
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful && auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
                val user = auth.currentUser!!
                saveInDb(user)
            } else {
                _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_firebase_auth_failed))
            }
        }
    }

    /**
     * Saves user data in Firestore database.
     * This function is called after successful sign-in with Google.
     */
    private fun saveInDb(user: FirebaseUser) {
        val userData = hashMapOf(
            Constants.FIELD_UID to user.uid,
            Constants.FIELD_NAME to user.displayName,
            Constants.FIELD_EMAIL to user.email,
            Constants.FIELD_PHOTO_URL to user.photoUrl?.toString(),
            Constants.FIELD_LAST_LOGIN to FieldValue.serverTimestamp()
        )
        
        db.collection(Constants.COLLECTION_USERS).document(user.uid).set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(resourceProvider.getString(R.string.log_tag_google_sign_in), "User data added successfully")
                val welcomeMessage = resourceProvider.getString(R.string.success_welcome_message, user.displayName ?: "")
                _signInState.value = SignInState.Success(welcomeMessage)
                _signInState.value = SignInState.Proceed
            }.addOnFailureListener { e ->
                Log.w(resourceProvider.getString(R.string.log_tag_google_sign_in), "Error adding user data", e)
                _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_firestore_failed))
            }
    }
    
    /**
     * Reset the sign-in state to idle
     */
    fun resetState() {
        _signInState.value = SignInState.Idle
    }
    
    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    /**
     * Check if user is signed in
     */
    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun signUpWithEmail(email: String, password: String) {
        _signInState.value = SignInState.Loading
        launchWithExceptionHandler {
            when (val result = authRepository.signUpWithEmail(email, password)) {
                is Resource.Success -> {
                    val user = result.data
                    saveInDb(user)
                }
                is Resource.Error -> {
                    _signInState.value = SignInState.Error(result.message)
                }
                else -> {
                    _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_unknown))
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _signInState.value = SignInState.Loading
        launchWithExceptionHandler {
            when (val result = authRepository.signInWithEmail(email, password)) {
                is Resource.Success -> {
                    val user = result.data
                    _signInState.value = SignInState.Success(resourceProvider.getString(R.string.success_welcome_message, user.displayName ?: email))
                    _signInState.value = SignInState.Proceed
                }
                is Resource.Error -> {
                    _signInState.value = SignInState.Error(result.message)
                }
                else -> {
                    _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_unknown))
                }
            }
        }
    }

    fun signUpWithEmailAndName(displayName: String, email: String, password: String) {
        _signInState.value = SignInState.Loading
        launchWithExceptionHandler {
            when (val result = authRepository.signUpWithEmail(email, password)) {
                is Resource.Success -> {
                    val user = result.data
                    // Set display name in Firebase Auth profile
                    user?.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
                        ?.addOnCompleteListener {
                            saveInDb(user, displayName)
                        }
                }
                is Resource.Error -> {
                    _signInState.value = SignInState.Error(result.message)
                }
                else -> {
                    _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_unknown))
                }
            }
        }
    }

    private fun saveInDb(user: FirebaseUser?, displayName: String? = null) {
        if (user == null) return
        val userData = hashMapOf(
            Constants.FIELD_UID to user.uid,
            Constants.FIELD_NAME to (displayName ?: user.displayName),
            Constants.FIELD_EMAIL to user.email,
            Constants.FIELD_PHOTO_URL to user.photoUrl?.toString(),
            Constants.FIELD_LAST_LOGIN to FieldValue.serverTimestamp()
        )
        db.collection(Constants.COLLECTION_USERS).document(user.uid).set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(resourceProvider.getString(R.string.log_tag_google_sign_in), "User data added successfully")
                val welcomeMessage = resourceProvider.getString(R.string.success_welcome_message, displayName ?: user.displayName ?: "")
                _signInState.value = SignInState.Success(welcomeMessage)
                _signInState.value = SignInState.Proceed
            }.addOnFailureListener { e ->
                Log.w(resourceProvider.getString(R.string.log_tag_google_sign_in), "Error adding user data", e)
                _signInState.value = SignInState.Error(resourceProvider.getString(R.string.error_firestore_failed))
            }
    }
}