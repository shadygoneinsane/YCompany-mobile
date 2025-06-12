package com.ycompany.ui.dashboard.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ycompany.R

class ProductsFragment : Fragment() {
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_products, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_products)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ProductAdapter()
        recyclerView.adapter = adapter

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadProducts()
    }
}