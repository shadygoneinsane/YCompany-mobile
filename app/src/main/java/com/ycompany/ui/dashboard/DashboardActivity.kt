package com.ycompany.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ycompany.R
import com.ycompany.databinding.ActivityDashboardBinding
import com.ycompany.ui.SignInActivity
import com.ycompany.ui.base.BaseActivity
import com.ycompany.ui.base.BaseSnackBar
import com.ycompany.ui.dashboard.orders.OrdersFragment
import com.ycompany.ui.dashboard.products.ProductsFragment

class DashboardActivity : BaseActivity<ActivityDashboardBinding, ViewModel>(ViewModel::class) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBindingAndViewModel(R.layout.activity_dashboard)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProductsFragment()).commit()

        binding.bottomNav.setOnItemSelectedListener {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        BaseSnackBar.showSuccess(binding.root, getString(R.string.success_signed_out))

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}