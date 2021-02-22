package com.minerdev.greformanager

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.minerdev.greformanager.databinding.FragmentInfo3Binding

class InfoFragment3 : Fragment(), OnSaveDataListener {
    private lateinit var toggleButtonGroupOptions: ToggleButtonGroup
    private lateinit var binding: FragmentInfo3Binding
    private lateinit var viewModel: HouseModifyViewModel
    private var house: House? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(HouseModifyViewModel::class.java)


        // 옵션 정보 초기화
        toggleButtonGroupOptions = ToggleButtonGroup(context, "옵션 항목")
        toggleButtonGroupOptions.addToggleButtons(resources.getStringArray(R.array.option))
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.houseModify3FlowLayoutOption.addView(toggleButton)
        }


        // 담당자 정보 초기화
        binding.houseModify3EditTextPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun checkData(): Boolean {
        return binding.houseModify3EditTextPhone.text.toString() != ""
    }

    override fun saveData() {
        val data = house
        data?.let {
            it.options = toggleButtonGroupOptions.checkedToggleButtonTextsInSingleLine
            it.detail_info = binding.houseModify3DetailInfo.text.toString()
            it.phone = binding.houseModify3EditTextPhone.text.toString()
        }
    }

    fun initData() {
        // 데이터 읽기
        house = viewModel.house
        house?.let { loadData(it) }
    }

    private fun loadData(house: House) {
        if (!house.options.isNullOrEmpty()) {
            val optionsTexts = house.options?.split("\\|")?.toTypedArray()
            optionsTexts?.let {
                for (text in it) {
                    toggleButtonGroupOptions.setToggleButtonCheckedState(text, true)
                }
            }
        }
        binding.houseModify3DetailInfo.setText(house.detail_info)
        binding.houseModify3EditTextPhone.setText(house.phone)
    }
}