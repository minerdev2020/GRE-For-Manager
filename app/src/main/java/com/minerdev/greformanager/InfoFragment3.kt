package com.minerdev.greformanager

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minerdev.greformanager.databinding.FragmentInfo3Binding

class InfoFragment3 : Fragment(), OnSaveDataListener {
    private val viewModel: HouseModifyViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = HouseModifyViewModel() as T
        }
    }

    private lateinit var toggleButtonGroupOptions: ToggleButtonGroup
    private lateinit var binding: FragmentInfo3Binding
    private lateinit var house: House

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false)


        // 옵션 정보 초기화
        toggleButtonGroupOptions = ToggleButtonGroup(context, "옵션 항목")
        toggleButtonGroupOptions.addToggleButtons(resources.getStringArray(R.array.option).toList())
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

    private fun initData() {
        // 데이터 읽기
        house = viewModel.house
        loadData(house)
    }

    override fun checkData(): Boolean {
        return binding.houseModify3EditTextPhone.text.toString() != ""
    }

    override fun saveData() {
        house.options = toggleButtonGroupOptions.checkedToggleButtonTextsInSingleLine
        house.detail_info = binding.houseModify3DetailInfo.text.toString()
        house.phone = binding.houseModify3EditTextPhone.text.toString()
    }

    private fun loadData(house: House) {
        house.options?.let {
            val optionsTexts = it.split('|')
            for (text in optionsTexts) {
                toggleButtonGroupOptions.setToggleButtonCheckedState(text, true)
            }
        }

        binding.houseModify3DetailInfo.setText(house.detail_info)
        binding.houseModify3EditTextPhone.setText(house.phone)
    }
}