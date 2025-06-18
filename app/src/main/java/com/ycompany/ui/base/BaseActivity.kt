package com.ycompany.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ycompany.YCompanyApp
import com.ycompany.di.DaggerViewModelFactory
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseActivity<VB : ViewDataBinding, VM : ViewModel>(
    private val viewModelClass: KClass<VM>
) : AppCompatActivity() {
    
    protected lateinit var binding: VB
        private set
    
    protected lateinit var viewModel: VM
        private set
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
    }
    
    /**
     * Call this in child onCreate to initialize DataBinding and ViewModel
     */
    protected fun initBindingAndViewModel(layoutId: Int) {
        binding = DataBindingUtil.setContentView(this, layoutId)
        val viewModelFactory = (application as YCompanyApp).appComponent.viewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[viewModelClass.java]
    }
    
    /**
     * Setup observers - override in subclasses
     */
    protected open fun setupObservers() {}
} 