package com.ycompany.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {
    
    /**
     * Coroutine scope with exception handling
     */
    protected val viewModelScopeWithExceptionHandler = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    
    /**
     * Launch coroutine with exception handling
     */
    protected fun launchWithExceptionHandler(block: suspend CoroutineScope.() -> Unit) {
        viewModelScopeWithExceptionHandler.launch {
            block()
        }
    }
    
    /**
     * Handle errors - override in subclasses
     */
    protected open fun handleError(throwable: Throwable) {
        // Default error handling - can be overridden
        throwable.printStackTrace()
    }
    
    /**
     * Get error message from throwable
     */
    protected fun getErrorMessage(throwable: Throwable): String {
        return throwable.message ?: com.ycompany.data.Constants.ERROR_UNKNOWN
    }
} 