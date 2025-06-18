package com.ycompany.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ycompany.R
import com.ycompany.data.AuthRepository
import com.ycompany.data.Resource
import com.ycompany.ui.ResourceProvider
import com.ycompany.ui.SignInViewModel
import com.ycompany.ui.events.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class FakeResourceProvider : ResourceProvider {
    override fun getString(resId: Int, vararg formatArgs: Any?): String = when (resId) {
        R.string.error_firebase_auth_failed -> "Firebase authentication failed"
        R.string.error_firestore_failed -> "Failed to save user data"
        R.string.success_welcome_message -> if (formatArgs.isNotEmpty()) "Welcome ${formatArgs[0]}" else "Welcome"
        R.string.log_tag_google_sign_in -> "GoogleSignIn"
        R.string.error_unknown -> "Unknown error occurred"
        else -> ""
    }
    override fun getString(resId: Int): String = getString(resId, *emptyArray())
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SignInViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    @Mock
    private lateinit var mockTask: Task<Void>

    @Mock
    private lateinit var mockCollectionReference: CollectionReference

    @Mock
    private lateinit var mockAuthRepository: AuthRepository

    private lateinit var viewModel: SignInViewModel
    private lateinit var resourceProvider: ResourceProvider

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        resourceProvider = FakeResourceProvider()
        viewModel = SignInViewModel(mockAuth, mockFirestore, resourceProvider, mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state is idle`() = runTest {
        assertEquals(SignInState.Idle, viewModel.signInState.value)
    }

    @Test
    fun `test resetState changes state to idle`() = runTest {
        viewModel.resetState()
        assertEquals(SignInState.Idle, viewModel.signInState.value)
    }

    @Test
    fun `test isUserSignedIn returns false when no user`() {
        `when`(mockAuth.currentUser).thenReturn(null)
        val result = viewModel.isUserSignedIn()
        assertFalse(result)
    }

    @Test
    fun `test isUserSignedIn returns true when user is signed in`() {
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        val result = viewModel.isUserSignedIn()
        assertTrue(result)
    }

    @Test
    fun `test getCurrentUser returns null when no user`() {
        `when`(mockAuth.currentUser).thenReturn(null)
        val result = viewModel.getCurrentUser()
        assertNull(result)
    }

    @Test
    fun `test getCurrentUser returns user when signed in`() {
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        val result = viewModel.getCurrentUser()
        assertEquals(mockUser, result)
    }

    @Test
    fun `test firebaseAuthWithGoogle success flow`() = runTest {
        val testToken = "test_google_token"
        val testDisplayName = "Test User"
        val testEmail = "test@example.com"
        val testUid = "test_uid"
        val testPhotoUrl = "https://example.com/photo.jpg"
        `when`(mockUser.displayName).thenReturn(testDisplayName)
        `when`(mockUser.email).thenReturn(testEmail)
        `when`(mockUser.uid).thenReturn(testUid)
        `when`(mockUser.photoUrl).thenReturn(android.net.Uri.parse(testPhotoUrl))
        `when`(mockUser.isEmailVerified).thenReturn(true)
        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any(), any(SetOptions::class.java))).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnSuccessListener<Void>
            listener.onSuccess(null)
            mockTask
        }
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)
        val mockAuthResult = mock(com.google.firebase.auth.AuthResult::class.java)
        `when`(mockAuthResult.user).thenReturn(mockUser)
        val mockTaskAuth = mock(Task::class.java) as Task<com.google.firebase.auth.AuthResult>
        `when`(mockTaskAuth.isSuccessful).thenReturn(true)
        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }
        `when`(mockAuth.signInWithCredential(any())).thenReturn(mockTaskAuth)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        viewModel.firebaseAuthWithGoogle(testToken)
        verify(mockAuth).signInWithCredential(any(GoogleAuthProvider.getCredential(testToken, null)::class.java))
        verify(mockFirestore).collection("users")
        verify(mockCollectionReference).document(testUid)
        verify(mockDocumentReference).set(any(), eq(SetOptions.merge()))
    }

    @Test
    fun `test firebaseAuthWithGoogle failure flow`() = runTest {
        val testToken = "test_google_token"
        val mockTaskAuth = mock(Task::class.java) as Task<com.google.firebase.auth.AuthResult>
        `when`(mockTaskAuth.isSuccessful).thenReturn(false)
        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }
        `when`(mockAuth.signInWithCredential(any())).thenReturn(mockTaskAuth)
        `when`(mockAuth.currentUser).thenReturn(null)
        viewModel.firebaseAuthWithGoogle(testToken)
        assertEquals(SignInState.Error("Firebase authentication failed"), viewModel.signInState.value)
    }

    @Test
    fun `test firebaseAuthWithGoogle user not verified`() = runTest {
        val testToken = "test_google_token"
        `when`(mockUser.isEmailVerified).thenReturn(false)
        val mockTaskAuth = mock(Task::class.java) as Task<com.google.firebase.auth.AuthResult>
        `when`(mockTaskAuth.isSuccessful).thenReturn(true)
        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }
        `when`(mockAuth.signInWithCredential(any())).thenReturn(mockTaskAuth)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        viewModel.firebaseAuthWithGoogle(testToken)
        assertEquals(SignInState.Error("Firebase authentication failed"), viewModel.signInState.value)
    }

    @Test
    fun `test firebaseAuthWithGoogle firestore failure`() = runTest {
        val testToken = "test_google_token"
        val testDisplayName = "Test User"
        `when`(mockUser.displayName).thenReturn(testDisplayName)
        `when`(mockUser.email).thenReturn("test@example.com")
        `when`(mockUser.uid).thenReturn("test_uid")
        `when`(mockUser.photoUrl).thenReturn(null)
        `when`(mockUser.isEmailVerified).thenReturn(true)
        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any(), any(SetOptions::class.java))).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
        `when`(mockTask.addOnFailureListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnFailureListener
            listener.onFailure(Exception("Firestore error"))
            mockTask
        }
        val mockAuthResult = mock(com.google.firebase.auth.AuthResult::class.java)
        `when`(mockAuthResult.user).thenReturn(mockUser)
        val mockTaskAuth = mock(Task::class.java) as Task<com.google.firebase.auth.AuthResult>
        `when`(mockTaskAuth.isSuccessful).thenReturn(true)
        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }
        `when`(mockAuth.signInWithCredential(any())).thenReturn(mockTaskAuth)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        viewModel.firebaseAuthWithGoogle(testToken)
        assertEquals(SignInState.Error("Failed to save user data"), viewModel.signInState.value)
    }

    @Test
    fun `test signUpWithEmail success`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        `when`(mockUser.displayName).thenReturn("Test User")
        `when`(mockUser.email).thenReturn(email)
        `when`(mockUser.uid).thenReturn("test_uid")
        `when`(mockUser.photoUrl).thenReturn(null)
        `when`(mockUser.isEmailVerified).thenReturn(true)
        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any(), any(SetOptions::class.java))).thenReturn(mockTask)
        `when`(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<Any>(0) as com.google.android.gms.tasks.OnSuccessListener<Void>
            listener.onSuccess(null)
            mockTask
        }
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)
        `when`(mockAuthRepository.signUpWithEmail(email, password)).thenReturn(Resource.Success(mockUser))
        
        viewModel.signUpWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify(mockAuthRepository).signUpWithEmail(email, password)
        verify(mockFirestore).collection("users")
    }

    @Test
    fun `test signUpWithEmail failure`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Sign up failed"
        `when`(mockAuthRepository.signUpWithEmail(email, password)).thenReturn(Resource.Error(errorMessage))
        
        viewModel.signUpWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(SignInState.Error(errorMessage), viewModel.signInState.value)
    }

    @Test
    fun `test signInWithEmail success`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        `when`(mockUser.displayName).thenReturn("Test User")
        `when`(mockUser.email).thenReturn(email)
        `when`(mockAuthRepository.signInWithEmail(email, password)).thenReturn(Resource.Success(mockUser))
        
        viewModel.signInWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify(mockAuthRepository).signInWithEmail(email, password)
        assertEquals(SignInState.Success("Welcome Test User"), viewModel.signInState.value)
    }

    @Test
    fun `test signInWithEmail failure`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Sign in failed"
        `when`(mockAuthRepository.signInWithEmail(email, password)).thenReturn(Resource.Error(errorMessage))
        
        viewModel.signInWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(SignInState.Error(errorMessage), viewModel.signInState.value)
    }

    private fun <T> mock(clazz: Class<T>): T = org.mockito.Mockito.mock(clazz)
} 