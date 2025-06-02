package com.ycompany.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ycompany.R
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "GoogleSignIn"
    private lateinit var credentialManager: CredentialManager
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            launchGoogleSignIn()
        }
    }

    private fun launchGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest(listOf(googleIdOption))

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@SignInActivity, request)

                val customCredential = result.credential as? CustomCredential

                if (customCredential != null && customCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                    val googleIdCredential =
                        GoogleIdTokenCredential.createFrom(customCredential.data)
                    val idToken = googleIdCredential.idToken

                    firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e(
                        "SignIn", "Unexpected credential type: ${result.credential.javaClass.name}"
                    )
                }
            } catch (e: Exception) {
                Log.e("SignIn", "Google Sign-In failed: ${e.localizedMessage}")
                Toast.makeText(this@SignInActivity, "Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful && auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
                val user = auth.currentUser!!
                saveInDb(user)
                Toast.makeText(this, "Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show()
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
        db.collection("users")
            .document(user.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User data added successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user data", e)
            }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}