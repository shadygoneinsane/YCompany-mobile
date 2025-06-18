package com.ycompany.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ycompany.YCompanyApp
import com.ycompany.di.DaggerViewModelFactory

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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