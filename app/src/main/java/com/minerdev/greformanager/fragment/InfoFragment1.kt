package com.minerdev.greformanager.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minerdev.greformanager.OnSaveDataListener
import com.minerdev.greformanager.R
import com.minerdev.greformanager.ToggleButtonGroup
import com.minerdev.greformanager.databinding.FragmentInfo1Binding
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.utils.Constants.BASE_URL
import com.minerdev.greformanager.utils.Constants.PAYMENT_TYPE
import com.minerdev.greformanager.viewmodel.SharedViewModel

class InfoFragment1 : Fragment(), OnSaveDataListener {
    private val handler = Handler()
    private val viewModel: SharedViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = SharedViewModel() as T
        }
    }

    private lateinit var toggleButtonGroupManageFeeContains: ToggleButtonGroup
    private lateinit var arrayAdapterPayment: ArrayAdapter<String>
    private lateinit var addressDialog: Dialog
    private lateinit var binding: FragmentInfo1Binding
    private lateinit var house: House
    private var houseType: String = ""
    private var paymentType: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info1, container, false)


        // 주소 입력 초기화
        binding.materialBtnSearch.setOnClickListener { showAddressDialog() }


        // 스피너 초기화
        setSpinners()


        // 체크박스 초기화
        setCheckBoxes()


        // 관리비 초기화
        toggleButtonGroupManageFeeContains = ToggleButtonGroup(context, "관리비 항목")
        toggleButtonGroupManageFeeContains.addToggleButtons(resources.getStringArray(R.array.manage_fee).toList())
        for (toggleButton in toggleButtonGroupManageFeeContains.toggleButtons) {
            binding.flowLayoutManageFee.addView(toggleButton)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        // 데이터 읽기
        house = viewModel.house
        binding.spnHouseType.tag = "loadData"
        binding.spnPaymentType.tag = "loadData"
        loadData(house)
    }

    override fun checkData(): Boolean {
        if (binding.tvAddress.text.isNullOrEmpty()) return false
        if (binding.etNumber.text.isNullOrEmpty()) return false
        if (binding.spnHouseType.selectedItemId == 0L) return false
        if (binding.spnPaymentType.selectedItemId == 0L) return false

        if (!(paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대")
                && binding.etPrice.text.isNullOrEmpty()) {
            return false
        } else if ((paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대")
                && (binding.etDeposit.text.isNullOrEmpty() || binding.etMonthlyRent.text.isNullOrEmpty())) {
            return false
        }
        if (houseType == "상가, 점포" && paymentType == "임대" && binding.etPremium.text.isNullOrEmpty()) {
            return false
        }
        if (!binding.cbManageFee.isChecked && binding.etManageFee.text.isNullOrEmpty()) {
            return false
        }

        return true
    }

    override fun saveData() {
        house.apply {
            address = binding.tvAddress.text.toString()
            number = binding.etNumber.text.toString()
            house_type = binding.spnHouseType.selectedItemPosition.toByte()
            facility = (if (binding.cbFacility.isChecked) 1 else 0).toByte()
            payment_type = binding.spnPaymentType.selectedItemPosition.toByte()
            price = parseInt(binding.etPrice.text.toString())
            deposit = parseInt(binding.etDeposit.text.toString())
            monthly_rent = parseInt(binding.etMonthlyRent.text.toString())
            premium = parseInt(binding.etPremium.text.toString())
            manage_fee = parseInt(binding.etManageFee.text.toString())
            manage_fee_contains = toggleButtonGroupManageFeeContains.checkedToggleButtonTextsInSingleLine
        }
    }

    private fun loadData(house: House) {
        houseType = resources.getStringArray(R.array.houseType)[house.house_type.toInt()]
        paymentType = if (house.house_type > 0) {
            PAYMENT_TYPE[house.house_type - 1][house.payment_type.toInt()]
        } else {
            ""
        }

        binding.tvAddress.text = house.address
        binding.etNumber.setText(house.number)
        binding.spnHouseType.setSelection(house.house_type.toInt())

        arrayAdapterPayment.clear()
        if (house.house_type > 0) {
            arrayAdapterPayment.addAll(PAYMENT_TYPE[house.house_type - 1])
        }

        binding.spnPaymentType.setSelection(house.payment_type.toInt())
        binding.cbFacility.isChecked = house.facility.toInt() == 1
        binding.etPrice.setText(if (house.price == 0) "" else house.price.toString())
        binding.etDeposit.setText(if (house.deposit == 0) "" else house.deposit.toString())
        binding.etMonthlyRent.setText(if (house.monthly_rent == 0) "" else house.monthly_rent.toString())
        binding.etPremium.setText(if (house.premium == 0) "" else house.premium.toString())

        if (house.manage_fee > 0) {
            binding.cbManageFee.isChecked = false
            binding.etManageFee.setText(house.manage_fee.toString())
        }

        val manageFeeTexts = house.manage_fee_contains.split('|')
        for (text in manageFeeTexts) {
            toggleButtonGroupManageFeeContains.setToggleButtonCheckedState(text, true)
        }
    }

    private fun setSpinners() {
        // 계약 형태 초기화
        arrayAdapterPayment = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        binding.spnPaymentType.adapter = arrayAdapterPayment
        binding.spnPaymentType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (binding.spnPaymentType.tag == null) {
                    onPaymentTypeItemSelected()

                } else {
                    binding.spnPaymentType.tag = null

                    if (paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대") {
                        binding.trPrice.visibility = View.GONE
                        binding.trDeposit.visibility = View.VISIBLE
                        binding.trMonthlyRent.visibility = View.VISIBLE

                    } else {
                        binding.trPrice.visibility = View.VISIBLE
                        binding.trDeposit.visibility = View.GONE
                        binding.trMonthlyRent.visibility = View.GONE
                    }

                    if (houseType == "상가, 점포" && paymentType == "임대") {
                        binding.trPremium.visibility = View.VISIBLE

                    } else {
                        binding.trPremium.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // 매물 종류 초기화
        val arrayAdapterHouse = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterHouse.addAll(*resources.getStringArray(R.array.houseType))
        binding.spnHouseType.adapter = arrayAdapterHouse
        binding.spnHouseType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (binding.spnHouseType.tag == null) {
                    onHouseTypeItemSelected(p2)

                } else {
                    binding.spnHouseType.tag = null
                    binding.cbFacility.isEnabled = houseType == "상가, 점포"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setCheckBoxes() {
        // 관리비 초기화
        binding.cbManageFee.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            binding.etManageFee.setText("")
            binding.etManageFee.isEnabled = !isChecked
            binding.flowLayoutManageFee.visibility = if (isChecked) View.GONE else View.VISIBLE
            toggleButtonGroupManageFeeContains.resetToggleButtonCheckedState()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showAddressDialog() {
        addressDialog = Dialog(requireContext())
        addressDialog.setContentView(R.layout.dialog_address)

        val webView = addressDialog.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AndroidBridge(), "GREApp")
        webView.loadUrl(BASE_URL + "/api/daum-address")

        val params = addressDialog.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        addressDialog.window?.attributes = params

        val buttonBack = addressDialog.findViewById<Button>(R.id.btn_back)
        buttonBack.setOnClickListener { addressDialog.dismiss() }
        addressDialog.show()
    }

    private fun parseInt(number: String): Int {
        return if (number.isEmpty()) {
            0
        } else {
            number.toInt()
        }
    }

    private fun parseFloat(number: String): Float {
        return if (number.isEmpty()) {
            0F
        } else {
            number.toFloat()
        }
    }

    private fun onPaymentTypeItemSelected() {
        paymentType = binding.spnPaymentType.selectedItem.toString()
        if (paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대") {
            binding.trPrice.visibility = View.GONE
            binding.trDeposit.visibility = View.VISIBLE
            binding.trMonthlyRent.visibility = View.VISIBLE
        } else {
            binding.trPrice.visibility = View.VISIBLE
            binding.trDeposit.visibility = View.GONE
            binding.trMonthlyRent.visibility = View.GONE
        }
        if (houseType == "상가, 점포" && paymentType == "임대") {
            binding.trPremium.visibility = View.VISIBLE
        } else {
            binding.trPremium.visibility = View.GONE
        }
        binding.etPrice.setText("")
        binding.etDeposit.setText("")
        binding.etMonthlyRent.setText("")
        binding.etPremium.setText("")
    }

    private fun onHouseTypeItemSelected(position: Int) {
        houseType = binding.spnHouseType.selectedItem.toString()

        // 시설 유무 초기화
        binding.cbFacility.isEnabled = houseType == "상가, 점포"
        if (position > 0) {
            arrayAdapterPayment.clear()
            arrayAdapterPayment.addAll(PAYMENT_TYPE[position - 1])
            binding.spnPaymentType.setSelection(0)

        } else {
            arrayAdapterPayment.clear()
        }

        binding.cbFacility.isChecked = false
    }

    private inner class AndroidBridge {
        @JavascriptInterface
        fun setAddress(arg1: String?, arg2: String?, arg3: String?) {
            handler.post {
                binding.tvAddress.text = String.format("(%s) %s %s",
                        arg1 ?: "", arg2 ?: "", arg3 ?: "")
                addressDialog.dismiss()
            }
        }
    }
}