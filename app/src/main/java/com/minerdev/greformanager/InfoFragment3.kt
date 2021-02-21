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
    private var toggleButtonGroupOptions: ToggleButtonGroup? = null
    private var binding: FragmentInfo3Binding? = null
    private var viewModel: HouseModifyViewModel? = null
    private var house: House? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(HouseModifyViewModel::class.java)


        // 옵션 정보 초기화
        toggleButtonGroupOptions = ToggleButtonGroup(context, "옵션 항목")
        toggleButtonGroupOptions!!.addToggleButtons(*resources.getStringArray(R.array.option))
        for (toggleButton in toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseModify3FlowLayoutOption.addView(toggleButton)
        }


        // 담당자 정보 초기화
        binding.houseModify3EditTextPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        return binding.getRoot()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun checkData(): Boolean {
        return if (binding!!.houseModify3EditTextPhone.text.toString() == "") {
            false
        } else true
    }

    override fun saveData() {
        val data = house
        data!!.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine()
        data.detail_info = binding!!.houseModify3DetailInfo.text.toString()
        data.phone = binding!!.houseModify3EditTextPhone.text.toString()
    }

    fun initData() {
        // 데이터 읽기
        house = viewModel.getHouse()
        if (house != null) {
            loadData()
        }
    }

    private fun loadData() {
        if (house!!.options != null && house!!.options != "") {
            val optionsTexts = house!!.options!!.split("\\|").toTypedArray()
            for (text in optionsTexts) {
                toggleButtonGroupOptions!!.setToggleButtonCheckedState(text, true)
            }
        }
        binding!!.houseModify3DetailInfo.setText(house!!.detail_info)
        binding!!.houseModify3EditTextPhone.setText(house!!.phone)
    }
}