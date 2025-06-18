package com.ycompany.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ycompany.YCompanyApp
import com.ycompany.di.DaggerViewModelFactory

abstract class BaseFragment : Fragment() {
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()
    }
    
    /**
     * Get ViewModel with proper factory injection
     */
    protected inline fun <reified T : androidx.lifecycle.ViewModel> getViewModel(): T {
        val viewModelFactory = (requireActivity().application as YCompanyApp).appComponent.viewModelFactory()
        return ViewModelProvider(this, viewModelFactory)[T::class.java]
    }
    
    /**
     * Initialize views - override in subclasses
     */
    protected open fun initViews() {}
    
    /**
     * Setup observers - override in subclasses
     */
    protected open fun setupObservers() {}
} 