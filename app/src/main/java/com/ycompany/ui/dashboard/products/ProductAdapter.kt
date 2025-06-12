package com.ycompany.ui.dashboard.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ycompany.R
import com.ycompany.data.model.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        private val tvName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_product_price)

        fun bind(product: Product) {
            tvName.text = product.name
            tvPrice.text = "₹${product.price}"

            Glide.with(itemView.context).load(product.imageUrl)
                .placeholder(R.drawable.ic_placeholder).into(imgProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
