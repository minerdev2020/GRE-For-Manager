package com.minerdev.greformanager.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.minerdev.greformanager.R
import com.minerdev.greformanager.custom.adapter.HouseListAdapter
import com.minerdev.greformanager.databinding.ActivityMainBinding
import com.minerdev.greformanager.utils.Constants
import com.minerdev.greformanager.utils.Constants.FINISH_INTERVAL_TIME
import com.minerdev.greformanager.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val saleAdapter by lazy { HouseListAdapter(HouseListAdapter.DiffCallback()) }
    private val soldAdapter by lazy { HouseListAdapter(HouseListAdapter.DiffCallback()) }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Constants.initialize(applicationContext)

        viewModel.allSale.observe(this, saleAdapter::submitList)
        viewModel.allSold.observe(this, soldAdapter::submitList)

        // 툴바 초기화
        setSupportActionBar(binding.toolbar)

        // 판매중 매물 RecyclerView 및 HouseListAdapter 초기화
        setRecyclerViewSale()

        // 판매완료 매물 RecyclerView 및 HouseListAdapter 초기화
        setRecyclerViewSold()

        // NavigationView 초기화
        setNavigationView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHouses()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        binding.searchView.setOnSearchClickListener {
            menu.findItem(R.id.main_menu_add).isVisible = false
            menu.findItem(R.id.main_menu_refresh).isVisible = false
        }

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    rearrangeList(query)
                }
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            binding.searchView.onActionViewCollapsed()
            menu.findItem(R.id.main_menu_add).isVisible = true
            menu.findItem(R.id.main_menu_refresh).isVisible = true
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_add -> {
                val intent = Intent(this, HouseModifyActivity::class.java)
                intent.putExtra("mode", "add")
                startActivity(intent)
            }
            R.id.main_menu_refresh -> viewModel.loadHouses()
            R.id.main_menu_my_menu -> binding.drawerLayout.openDrawer(GravityCompat.END)
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawers()
            return
        }

        if (binding.searchView.hasFocus()) {
            val closeBtnId = binding.searchView.context.resources.getIdentifier(
                "android:id/search_close_btn",
                null,
                null
            )
            val closeBtn = binding.searchView.findViewById<ImageView>(closeBtnId)
            closeBtn.performClick()
            return
        }

        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime
        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            finish()

        } else {
            backPressedTime = tempTime
            Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rearrangeList(keyword: String) {

    }

    private fun setRecyclerViewSale() {
        binding.recyclerViewSale.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSale.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerViewSale.adapter = saleAdapter

        saleAdapter.clickListener = object : HouseListAdapter.OnItemClickListener {
            override fun onItemClick(
                viewHolder: HouseListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
                intent.putExtra("house_id", saleAdapter[position].id)
                startActivity(intent)
            }
        }

        binding.imageBtnSaleExpand.setOnClickListener {
            binding.recyclerViewSale.visibility =
                if (binding.recyclerViewSale.visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE

            binding.imageBtnSaleExpand.setImageResource(
                if (binding.recyclerViewSale.visibility == View.VISIBLE)
                    R.drawable.ic_round_expand_less_24
                else
                    R.drawable.ic_round_expand_more_24
            )
        }
    }

    private fun setRecyclerViewSold() {
        binding.recyclerViewSold.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSold.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerViewSold.adapter = soldAdapter

        soldAdapter.clickListener = object : HouseListAdapter.OnItemClickListener {
            override fun onItemClick(
                viewHolder: HouseListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
                intent.putExtra("house_id", soldAdapter[position].id)
                startActivity(intent)
            }
        }


        binding.imageBtnSoldExpand.setOnClickListener {
            binding.recyclerViewSold.visibility =
                if (binding.recyclerViewSold.visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE

            binding.imageBtnSoldExpand.setImageResource(
                if (binding.recyclerViewSold.visibility == View.VISIBLE)
                    R.drawable.ic_round_expand_less_24
                else
                    R.drawable.ic_round_expand_more_24
            )
        }
    }

    private fun setNavigationView() {
        binding.drawerLayout.lockSwipe(GravityCompat.END)

        binding.navView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_menu_logout -> {
                    val sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_menu_exit -> finish()
                else -> {
                }
            }
            true
        }
    }
}