package com.ycompany.ui.base

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ycompany.R

/**
 * Utility class for creating consistent SnackBars across the app
 */
object BaseSnackBar {
    
    /**
     * Show a simple SnackBar
     */
    fun show(
        view: View,
        message: String?,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            show()
        }
    }
    
    /**
     * Show a SnackBar with string resource
     */
    fun show(
        view: View,
        @StringRes messageRes: Int,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return Snackbar.make(view, messageRes, duration).apply {
            show()
        }
    }
    
    /**
     * Show a SnackBar with action button
     */
    fun showWithAction(
        view: View,
        message: String?,
        actionText: String?,
        action: () -> Unit,
        duration: Int = Snackbar.LENGTH_LONG
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            setAction(actionText.orEmpty()) { action() }
            show()
        }
    }
    
    /**
     * Show a SnackBar with action button using string resources
     */
    fun showWithAction(
        view: View,
        @StringRes messageRes: Int,
        @StringRes actionTextRes: Int,
        action: () -> Unit,
        duration: Int = Snackbar.LENGTH_LONG
    ): Snackbar {
        return Snackbar.make(view, messageRes, duration).apply {
            setAction(actionTextRes) { action() }
            show()
        }
    }
    
    /**
     * Show an error SnackBar with red background
     */
    fun showError(
        view: View,
        message: String?,
        duration: Int = Snackbar.LENGTH_LONG
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color.error_color))
            show()
        }
    }
    
    /**
     * Show a success SnackBar with green background
     */
    fun showSuccess(
        view: View,
        message: String?,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color.success_color))
            show()
        }
    }
    
    /**
     * Show a warning SnackBar with orange background
     */
    fun showWarning(
        view: View,
        message: String?,
        duration: Int = Snackbar.LENGTH_LONG
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color.warning_color))
            show()
        }
    }
    
    /**
     * Show a SnackBar with custom background color
     */
    fun showWithColor(
        view: View,
        message: String?,
        @ColorRes backgroundColor: Int,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return Snackbar.make(view, message.orEmpty(), duration).apply {
            setBackgroundTint(ContextCompat.getColor(context, backgroundColor))
            show()
        }
    }
} 