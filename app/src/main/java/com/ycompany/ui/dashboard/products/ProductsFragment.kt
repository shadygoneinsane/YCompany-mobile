package com.ycompany.ui.dashboard.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ycompany.R
import com.ycompany.databinding.FragmentProductsBinding
import com.ycompany.ui.base.BaseFragment
import com.ycompany.ui.base.BaseSnackBar
import kotlinx.coroutines.launch

class ProductsFragment : BaseFragment<FragmentProductsBinding>() {
    private lateinit var viewModel: ProductsViewModel
    private lateinit var adapter: ProductAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductsBinding {
        return FragmentProductsBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun initViews() {
        viewModel = getViewModel<ProductsViewModel>()
        setupRecyclerView()
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ProductsState.Loading -> {
                        // Show loading state (you can add a loading view)
                    }

                    is ProductsState.Success -> {
                        adapter.submitList(state.products)
                    }

                    is ProductsState.Error -> {
                        BaseSnackBar.showError(requireView(), state.message)
                    }

                    is ProductsState.Empty -> {
                        BaseSnackBar.showWarning(
                            requireView(),
                            getString(R.string.no_products_found)
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                effect?.let {
                    when (it) {
                        is ProductsEffect.NavigateToProductDetail -> {
                            BaseSnackBar.show(
                                requireView(),
                                getString(R.string.product_clicked, it.product.name)
                            )
                        }

                        is ProductsEffect.ShowError -> {
                            BaseSnackBar.showError(requireView(), it.message)
                        }
                    }
                    viewModel.clearEffect()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_products)
        adapter = ProductAdapter(
            onProductClick = { product -> viewModel.handleEvent(ProductsEvent.ProductClicked(product)) },
            onAddToCart = { product -> viewModel.addToCart(product) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val tvCartCount = requireView().findViewById<TextView>(R.id.tvCartCount)
        val btnPlaceOrder =
            requireView().findViewById<com.google.android.material.button.MaterialButton>(R.id.btnPlaceOrder)

        lifecycleScope.launch {
            viewModel.cart.collect { cartList ->
                tvCartCount.text = "Items in cart: ${cartList.size}"
                btnPlaceOrder.isEnabled = cartList.isNotEmpty()
            }
        }

        btnPlaceOrder.setOnClickListener {
            viewModel.placeAllOrders { success, message ->
                if (success) {
                    BaseSnackBar.showSuccess(requireView(), message)
                } else {
                    BaseSnackBar.showError(requireView(), message)
                }
            }
        }
    }
}