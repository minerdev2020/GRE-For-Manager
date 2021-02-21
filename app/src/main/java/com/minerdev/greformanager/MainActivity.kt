package com.minerdev.greformanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.minerdev.greformanager.HouseListAdapter.DiffCallback
import com.minerdev.greformanager.HttpConnection.OnReceiveListener
import com.minerdev.greformanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val saleAdapter = HouseListAdapter(DiffCallback())
    private val soldAdapter = HouseListAdapter(DiffCallback())

    private var backPressedTime: Long = 0
    private var viewModel: MainViewModel = ViewModelProvider(this,
            AndroidViewModelFactory(application)).get(MainViewModel::class.java)

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Constants.instance.initialize(applicationContext)
        viewModel.allSale.observe(this, saleAdapter::submitData)
        viewModel.allSold.observe(this, soldAdapter::submitData)

        // 툴바 초기화
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)


        // 판매중 매물 RecyclerView 및 HouseListAdapter 초기화
        binding.mainRecyclerViewSale.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerViewSale.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        saleAdapter.setOnItemClickListener { _: HouseListAdapter.ViewHolder?, _: View?, position: Int ->
            val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
            intent.putExtra("house_value", saleAdapter[position])
            startActivityForResult(intent, Constants.instance.HOUSE_DETAIL_ACTIVITY_REQUEST_CODE)
        }

        binding.mainRecyclerViewSale.adapter = saleAdapter

        binding.mainImageButtonSaleExpand.setOnClickListener {
            binding.mainRecyclerViewSale.visibility = if (binding.mainRecyclerViewSale.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE

            binding.mainImageButtonSaleExpand.setImageResource(if (binding.mainRecyclerViewSale.visibility == View.VISIBLE)
                R.drawable.ic_round_expand_less_24
            else
                R.drawable.ic_round_expand_more_24)
        }


        // 판매완료 매물 RecyclerView 및 HouseListAdapter 초기화
        binding.mainRecyclerViewSold.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerViewSold.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        soldAdapter.setOnItemClickListener { _: HouseListAdapter.ViewHolder?, _: View?, position: Int ->
            val intent = Intent(this@MainActivity, HouseDetailActivity::class.java)
            intent.putExtra("house_value", soldAdapter[position])
            startActivityForResult(intent, Constants.instance.HOUSE_DETAIL_ACTIVITY_REQUEST_CODE)
        }

        binding.mainRecyclerViewSold.adapter = soldAdapter

        binding.mainImageButtonSoldExpand.setOnClickListener {
            binding.mainRecyclerViewSold.visibility = if (binding.mainRecyclerViewSold.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE

            binding.mainImageButtonSoldExpand.setImageResource(if (binding.mainRecyclerViewSold.visibility == View.VISIBLE)
                R.drawable.ic_round_expand_less_24
            else
                R.drawable.ic_round_expand_more_24)
        }


        // NavigationView 초기화
        setNavigationView()
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        binding.mainSearchView.visibility = View.VISIBLE
        binding.mainSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                rearrangeList(query)
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        binding.mainSearchView.setOnCloseListener {
            binding.mainSearchView.onActionViewCollapsed()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_add -> {
                val intent = Intent(this, HouseModifyActivity::class.java)
                intent.putExtra("mode", "add")
                startActivityForResult(intent, Constants.instance.HOUSE_MODIFY_ACTIVITY_REQUEST_CODE)
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

        if (binding.mainSearchView.hasFocus()) {
            binding.mainSearchView.onActionViewCollapsed()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.instance.HOUSE_DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val house: House? = data?.getParcelableExtra("house_value")
                house?.let {
                    viewModel.insert(house)
                }
            }

        } else if (requestCode == Constants.instance.HOUSE_MODIFY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val house: House? = data?.getParcelableExtra("house_value")
                house?.let {
                    viewModel.insert(house)
                }
            }
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

    private fun loadItems() {
        HttpConnection.instance.receive(this, "houses/last-updated-at",
                object : OnReceiveListener {
                    override fun onReceive(receivedData: String?) {
                        if (receivedData.isNullOrEmpty()) {
                            loadItemsFromWeb()

                        } else {
                            checkUpdate(receivedData)
                        }
                    }
                })
    }

    private fun checkUpdate(receivedData: String) {
        Thread {
            val serverTimestamp = receivedData.toLong()
            val clientTimestamp = viewModel.lastUpdatedAt

            if (serverTimestamp > clientTimestamp) {
                loadItemsFromWeb()
            }
        }.start()
    }

    private fun loadItemsFromWeb() {
        HttpConnection.instance.receive(this, "houses",
                object : OnReceiveListener {
                    override fun onReceive(receivedData: String?) {
                        val gson = Gson()
                        val array = gson.fromJson(receivedData, Array<House>::class.java)

                        if (array == null) {
                            Toast.makeText(this@MainActivity, "데이터 수신 실패.", Toast.LENGTH_SHORT).show()
                            return
                        }

                        Log.d("last_updated_at", "load from web")
                        viewModel.insert(array.toList())
                    }
                })
    }

    companion object {
        private const val FINISH_INTERVAL_TIME: Long = 2000
    }
}