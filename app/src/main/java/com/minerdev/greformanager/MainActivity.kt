package com.minerdev.greformanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.minerdev.greformanager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val FINISH_INTERVAL_TIME = 2000
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val saleAdapter by lazy { HouseListAdapter(HouseListAdapter.DiffCallback()) }
    private val soldAdapter by lazy { HouseListAdapter(HouseListAdapter.DiffCallback()) }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Constants.instance.initialize(applicationContext)

        viewModel.allSale.observe(this, saleAdapter::submitList)
        viewModel.allSold.observe(this, soldAdapter::submitList)

        // 툴바 초기화
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        // 판매중 매물 RecyclerView 및 HouseListAdapter 초기화
        binding.recyclerViewSale.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSale.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewSale.adapter = saleAdapter

        saleAdapter.setOnItemClickListener(object : HouseListAdapter.OnItemClickListener {
            override fun onItemClick(viewHolder: HouseListAdapter.ViewHolder, view: View, position: Int) {
                val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
                intent.putExtra("house_value", saleAdapter[position])
                startActivity(intent)
            }
        })


        binding.imageBtnSaleExpand.setOnClickListener {
            binding.recyclerViewSale.visibility = if (binding.recyclerViewSale.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE

            binding.imageBtnSaleExpand.setImageResource(if (binding.recyclerViewSale.visibility == View.VISIBLE)
                R.drawable.ic_round_expand_less_24
            else
                R.drawable.ic_round_expand_more_24)
        }


        // 판매완료 매물 RecyclerView 및 HouseListAdapter 초기화
        binding.recyclerViewSold.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSold.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerViewSold.adapter = soldAdapter

        soldAdapter.setOnItemClickListener(object : HouseListAdapter.OnItemClickListener {
            override fun onItemClick(viewHolder: HouseListAdapter.ViewHolder, view: View, position: Int) {
                val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
                intent.putExtra("house_value", soldAdapter[position])
                startActivity(intent)
            }
        })


        binding.imageBtnSoldExpand.setOnClickListener {
            binding.recyclerViewSold.visibility = if (binding.recyclerViewSold.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE

            binding.imageBtnSoldExpand.setImageResource(if (binding.recyclerViewSold.visibility == View.VISIBLE)
                R.drawable.ic_round_expand_less_24
            else
                R.drawable.ic_round_expand_more_24)
        }


        // NavigationView 초기화
        setNavigationView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHouses()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        binding.searchView.visibility = View.VISIBLE
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    rearrangeList(p0)
                }

                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            binding.searchView.onActionViewCollapsed()
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
            binding.searchView.onActionViewCollapsed()
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

    private fun setNavigationView() {
        binding.drawerLayout.lockSwipe(GravityCompat.END)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_menu_db_reset -> viewModel.deleteAll()
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