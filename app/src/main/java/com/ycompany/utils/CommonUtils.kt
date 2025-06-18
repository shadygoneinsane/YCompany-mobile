package com.ycompany.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Patterns
import com.ycompany.data.Constants
import java.text.NumberFormat
import java.util.*

/**
 * Utility class containing common helper functions
 */
object CommonUtils {
    
    /**
     * Check if device has internet connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Format price with currency
     */
    fun formatPrice(price: Double, currencyCode: String = "USD"): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        return format.format(price)
    }
    
    /**
     * Get initials from name
     */
    fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return ""
        
        val words = name.trim().split("\\s+".toRegex())
        return when {
            words.isEmpty() -> ""
            words.size == 1 -> words[0].take(2).uppercase()
            else -> "${words[0].firstOrNull()?.uppercase() ?: ""}${words[1].firstOrNull()?.uppercase() ?: ""}"
        }
    }
    
    /**
     * Capitalize first letter of each word
     */
    fun capitalizeWords(text: String?): String {
        if (text.isNullOrEmpty()) return ""
        
        return text.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) {
                word[0].uppercase() + word.substring(1).lowercase()
            } else {
                word
            }
        }
    }
    
    /**
     * Truncate text to specified length
     */
    fun truncateText(text: String?, maxLength: Int): String {
        if (text.isNullOrEmpty()) return ""
        return if (text.length <= maxLength) text else "${text.take(maxLength)}..."
    }
    
    /**
     * Check if string is not null or empty
     */
    fun isNotNullOrEmpty(text: String?): Boolean {
        return !text.isNullOrEmpty() && text.trim().isNotEmpty()
    }
    
    /**
     * Get safe string (return default if null/empty)
     */
    fun getSafeString(text: String?, default: String = Constants.DEFAULT_EMPTY_STRING): String {
        return if (isNotNullOrEmpty(text)) text!! else default
    }
} 