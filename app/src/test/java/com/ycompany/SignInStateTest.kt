package com.ycompany

import com.ycompany.ui.events.SignInState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SignInStateTest {

    @Test
    fun `test SignInState Idle`() {
        val state = SignInState.Idle
        assertNotNull(state)
    }

    @Test
    fun `test SignInState Loading`() {
        val state = SignInState.Loading
        assertNotNull(state)
    }

    @Test
    fun `test SignInState Success with message`() {
        val message = "Welcome User!"
        val state = SignInState.Success(message)
        assertEquals(message, state.message)
    }

    @Test
    fun `test SignInState Success with null message`() {
        val state = SignInState.Success()
        assertEquals(null, state.message)
    }

    @Test
    fun `test SignInState Error with message`() {
        val errorMessage = "Authentication failed"
        val state = SignInState.Error(errorMessage)
        assertEquals(errorMessage, state.message)
    }

    @Test
    fun `test SignInState Proceed`() {
        val state = SignInState.Proceed
        assertNotNull(state)
    }
} 