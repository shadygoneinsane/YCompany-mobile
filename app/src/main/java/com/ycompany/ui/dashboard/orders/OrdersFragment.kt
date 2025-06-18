package com.ycompany.ui.dashboard.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ycompany.data.model.Order
import com.ycompany.databinding.FragmentOrdersBinding
import com.ycompany.ui.base.BaseFragment
import kotlinx.coroutines.launch

class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {
    private lateinit var viewModel: OrdersViewModel
    private lateinit var adapter: OrdersAdapter

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentOrdersBinding.inflate(inflater, container, false)

    override fun initViews() {
        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        adapter = OrdersAdapter()
        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = adapter
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.orders.collect { orderList ->
                binding.ordersPlaceholder.isVisible = orderList.isEmpty()
                adapter.submitList(orderList)
            }
        }
        viewModel.loadOrders()
    }
}

class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    private var orders: List<Order> = emptyList()
    fun submitList(list: List<Order>) {
        orders = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return OrderViewHolder(view)
    }
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }
    override fun getItemCount() = orders.size
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(order: Order) {
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)
            text1.text = order.productName
            text2.text = "Ordered on: ${order.orderTime}"
        }
    }
}