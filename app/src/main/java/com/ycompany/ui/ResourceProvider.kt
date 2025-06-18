package com.ycompany.ui

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String
    fun getString(@StringRes resId: Int): String
}

class ContextResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int, vararg formatArgs: Any?): String = context.getString(resId, *formatArgs)
    override fun getString(resId: Int): String = context.getString(resId)
} 