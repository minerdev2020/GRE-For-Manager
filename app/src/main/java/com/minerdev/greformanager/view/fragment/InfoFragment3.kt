package com.minerdev.greformanager.view.fragment

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
import com.minerdev.greformanager.R
import com.minerdev.greformanager.custom.view.ToggleButtonGroup
import com.minerdev.greformanager.databinding.FragmentInfo3Binding
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.viewmodel.SharedViewModel

class InfoFragment3 : Fragment(), OnSaveDataListener {
    private val viewModel: SharedViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = SharedViewModel() as T
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
            binding.flowLayoutOption.addView(toggleButton)
        }


        // 담당자 정보 초기화
        binding.etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

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
        if (binding.textInputEtDetailInfo.text.isNullOrEmpty()) {
            return false
        }
        if (binding.etPhone.text.isNullOrEmpty()) {
            return false
        }

        return true
    }

    override fun saveData() {
        house.options = toggleButtonGroupOptions.checkedToggleButtonTextsInSingleLine
        house.detail_info = binding.textInputEtDetailInfo.text.toString()
        house.phone = binding.etPhone.text.toString()
    }

    private fun loadData(house: House) {
        val optionsTexts = house.options.split('|')
        for (text in optionsTexts) {
            toggleButtonGroupOptions.setToggleButtonCheckedState(text, true)
        }

        binding.textInputEtDetailInfo.setText(house.detail_info)
        binding.etPhone.setText(house.phone)
    }
}