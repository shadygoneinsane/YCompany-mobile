package com.ycompany.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ycompany.R
import com.ycompany.ui.dashboard.orders.OrdersFragment
import com.ycompany.ui.dashboard.products.ProductsFragment

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProductsFragment()).commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_products -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProductsFragment()).commit()
                    true
                }

                R.id.menu_orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, OrdersFragment()).commit()
                    true
                }

                else -> false
            }
        }
    }
}