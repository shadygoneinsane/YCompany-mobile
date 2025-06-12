package com.ycompany.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.ycompany.R

class LoadingSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loading_spinner, this, true)
        progressBar = findViewById(R.id.progressBar)
        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }
}