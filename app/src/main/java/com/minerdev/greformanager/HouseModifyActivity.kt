package com.minerdev.greformanager

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.minerdev.greformanager.databinding.ActivityHouseModifyBinding
import java.util.*

class HouseModifyActivity : AppCompatActivity() {
    private val adapter = SectionPageAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> binding.btnPrevious.isEnabled = false
                1, 2 -> {
                    binding.btnPrevious.isEnabled = true
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnSave.visibility = View.GONE
                }
                3 -> {
                    binding.btnNext.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    private val sharedViewModel: SharedViewModel by viewModels()
    private val viewModel: HouseModifyViewModel by viewModels()
    private val binding by lazy { ActivityHouseModifyBinding.inflate(layoutInflater) }

    private var count = 0
    private var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        mode = intent.getStringExtra("mode")

        if (mode == "add") {
            actionBar?.title = "매물 추가"

        } else if (mode == "modify") {
            actionBar?.title = "매물 정보 수정"

            val house: House = intent.getParcelableExtra("house_value")!!
            sharedViewModel.house = house
            viewModel.getOrderByPosition(sharedViewModel.house.id).observe(this, { images: List<Image> ->
                val uris = ArrayList<Uri>()
                for (image in images) {
                    uris.add(Uri.parse(Constants.instance.DNS + "/storage/images/" + image.house_id + "/" + image.title))
                    if (image.thumbnail.toInt() == 1) {
                        sharedViewModel.thumbnail = image.position.toInt()
                    }
                }

                sharedViewModel.images = images as ArrayList<Image>
                sharedViewModel.imageUris.addAll(uris)
            })
        }

        binding.btnNext.setOnClickListener { toNextPage() }
        binding.btnPrevious.isEnabled = false
        binding.btnPrevious.setOnClickListener {
            hideKeyboard()
            val current = binding.viewPager.currentItem
            val listener = adapter.getItem(current) as OnSaveDataListener
            listener.saveData()
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
        binding.btnSave.visibility = View.GONE
        binding.btnSave.setOnClickListener { askSave() }
        setViewPager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("저장하지 않고 돌아가시겠습니까?")
        builder.setIcon(R.drawable.ic_round_help_24)
        builder.setPositiveButton("확인") { _: DialogInterface?, _: Int -> super@HouseModifyActivity.finish() }
        builder.setNegativeButton("취소") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        adapter.getItem(binding.viewPager.currentItem).onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setViewPager() {
        binding.viewPager.addOnPageChangeListener(onPageChangeListener)
        adapter.addFragment(InfoFragment1(), "매물 정보 입력")
        adapter.addFragment(InfoFragment2(), "매물 정보 입력")
        adapter.addFragment(InfoFragment3(), "매물 정보 입력")
        adapter.addFragment(ImageFragment(), "매물 사진 선택")
        binding.viewPager.adapter = adapter
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun toNextPage() {
        hideKeyboard()
        val current = binding.viewPager.currentItem
        val listener = adapter.getItem(current) as OnSaveDataListener
        if (listener.checkData()) {
            listener.saveData()
            binding.viewPager.currentItem = current + 1
        } else {
            Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askSave() {
        hideKeyboard()
        val current = binding.viewPager.currentItem
        val listener = adapter.getItem(current) as OnSaveDataListener
        if (listener.checkData()) {
            listener.saveData()

            val builder = AlertDialog.Builder(this)
            builder.setMessage("저장하시겠습니까?")
            builder.setIcon(R.drawable.ic_round_help_24)
            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                sharedViewModel.house.thumbnail = sharedViewModel.thumbnailTitle

                count = 0

                if (mode == "add") {
                    viewModel.add(sharedViewModel.house, sharedViewModel.imageUris, sharedViewModel.images)

                } else if (mode == "modify") {
                    viewModel.modify(sharedViewModel.house, sharedViewModel.imageUris, sharedViewModel.images)
                }
            }
            builder.setNegativeButton("취소") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()

        } else {
            Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
        }
    }
}