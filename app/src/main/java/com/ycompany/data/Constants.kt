package com.ycompany.data

object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_ORDERS = "orders"
    
    // User Fields
    const val FIELD_UID = "uid"
    const val FIELD_NAME = "name"
    const val FIELD_EMAIL = "email"
    const val FIELD_PHOTO_URL = "photoUrl"
    const val FIELD_LAST_LOGIN = "lastLogin"
    
    // Product Fields
    const val FIELD_PRODUCT_ID = "id"
    const val FIELD_PRODUCT_NAME = "name"
    const val FIELD_PRODUCT_PRICE = "price"
    const val FIELD_PRODUCT_IMAGE_URL = "imageUrl"
    
    // Shared Preferences
    const val PREF_NAME = "YCompanyPrefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    
    // Navigation
    const val EXTRA_PRODUCT_ID = "product_id"
    const val EXTRA_PRODUCT = "product"
    const val EXTRA_USER_ID = "user_id"
    
    // Error Messages
    const val ERROR_UNKNOWN = "Unknown error occurred"
    const val ERROR_PRODUCT_NOT_FOUND = "Product not found"
    const val ERROR_USER_NOT_FOUND = "User not found"
    const val ERROR_NETWORK = "Network error occurred"
    const val ERROR_AUTHENTICATION = "Authentication failed"
    
    // Success Messages
    const val SUCCESS_LOGIN = "Login successful"
    const val SUCCESS_LOGOUT = "Logout successful"
    const val SUCCESS_DATA_SAVED = "Data saved successfully"
    
    // Default Values
    const val DEFAULT_EMPTY_STRING = ""
    const val DEFAULT_PRICE = 0.0
    const val DEFAULT_TIMEOUT = 30000L // 30 seconds
    
    // Animation Durations
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE = 100
} 