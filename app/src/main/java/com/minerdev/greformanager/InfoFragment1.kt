package com.minerdev.greformanager

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
import androidx.lifecycle.ViewModelProvider
import com.minerdev.greformanager.databinding.FragmentInfo1Binding

class InfoFragment1 : Fragment(), OnSaveDataListener {
    private val handler = Handler()

    private lateinit var toggleButtonGroupManageFeeContains: ToggleButtonGroup
    private lateinit var arrayAdapterPayment: ArrayAdapter<String>
    private lateinit var addressDialog: Dialog
    private lateinit var binding: FragmentInfo1Binding
    private lateinit var viewModel: HouseModifyViewModel
    private lateinit var houseType: String
    private lateinit var paymentType: String
    private var house: House? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info1, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(HouseModifyViewModel::class.java)


        // 주소 입력 초기화
        binding.houseModify1MaterialButtonSearch.setOnClickListener { showAddressDialog() }


        // 스피너 초기화
        setSpinners()


        // 체크박스 초기화
        setCheckBoxes()


        // 관리비 초기화
        toggleButtonGroupManageFeeContains = ToggleButtonGroup(context, "관리비 항목")
        toggleButtonGroupManageFeeContains.addToggleButtons(resources.getStringArray(R.array.manage_fee))
        for (toggleButton in toggleButtonGroupManageFeeContains.toggleButtons) {
            binding.houseModify1FlowLayoutManageFee.addView(toggleButton)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun checkData(): Boolean {
        if (binding.houseModify1TextViewAddress.text.toString() == "") return false
        if (binding.houseModify1EditTextNumber.text.toString() == "") return false
        if (binding.houseModify1SpinnerHouseType.selectedItemId == 0L) return false
        if (binding.houseModify1SpinnerPaymentType.selectedItemId == 0L) return false

        if (!(paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대")
                && binding.houseModify1EditTextPrice.text.toString() == "") {
            return false

        } else if ((paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대")
                && (binding.houseModify1EditTextDeposit.text.toString() == "" || binding.houseModify1EditTextMonthlyRent.text.toString() == "")) {
            return false
        }

        if (houseType == "상가, 점포" && paymentType == "임대"
                && binding.houseModify1EditTextPremium.text.toString() == "") {
            return false
        }

        return !(!binding.houseModify1CheckBoxManageFee.isChecked
                && binding.houseModify1EditTextManageFee.text.toString() == "")
    }

    override fun saveData() {
        if (house == null) {
            house = viewModel.newHouse
        }

        house?.let {
            it.address = binding.houseModify1TextViewAddress.text.toString()
            it.number = binding.houseModify1EditTextNumber.text.toString()
            it.house_type = binding.houseModify1SpinnerHouseType.selectedItemPosition.toByte()
            it.facility = (if (binding.houseModify1CheckBoxFacility.isChecked) 1 else 0).toByte()
            it.payment_type = binding.houseModify1SpinnerPaymentType.selectedItemPosition.toByte()
            it.price = parseInt(binding.houseModify1EditTextPrice.text.toString())
            it.deposit = parseInt(binding.houseModify1EditTextDeposit.text.toString())
            it.monthly_rent = parseInt(binding.houseModify1EditTextMonthlyRent.text.toString())
            it.premium = parseInt(binding.houseModify1EditTextPremium.text.toString())
            it.manage_fee = parseInt(binding.houseModify1EditTextManageFee.text.toString())
            it.manage_fee_contains = toggleButtonGroupManageFeeContains.checkedToggleButtonTextsInSingleLine
        }
    }

    private fun initData() {
        // 데이터 읽기
        house = viewModel.house
        if (house)
        house?.let {
            binding.houseModify1SpinnerHouseType.tag = "loadData"
            binding.houseModify1SpinnerPaymentType.tag = "loadData"
            loadData(it)
        }
    }

    private fun loadData(house: House) {
        houseType = resources.getStringArray(R.array.houseType)[house.house_type.toInt()]
        paymentType = Constants.instance.PAYMENT_TYPE[house.house_type - 1][house.payment_type.toInt()]
        binding.houseModify1TextViewAddress.text = house.address
        binding.houseModify1EditTextNumber.setText(house.number)
        binding.houseModify1SpinnerHouseType.setSelection(house.house_type.toInt())
        arrayAdapterPayment.clear()
        arrayAdapterPayment.addAll(Constants.instance.PAYMENT_TYPE[house.house_type - 1])
        binding.houseModify1SpinnerPaymentType.setSelection(house.payment_type.toInt())
        binding.houseModify1CheckBoxFacility.isChecked = house.facility.toInt() == 1
        binding.houseModify1EditTextPrice.setText(if (house.price == 0) "" else house.price.toString())
        binding.houseModify1EditTextDeposit.setText(if (house.deposit == 0) "" else house.deposit.toString())
        binding.houseModify1EditTextMonthlyRent.setText(if (house.monthly_rent == 0) "" else house.monthly_rent.toString())
        binding.houseModify1EditTextPremium.setText(if (house.premium == 0) "" else house.premium.toString())

        if (house.manage_fee > 0) {
            binding.houseModify1CheckBoxManageFee.isChecked = false
            binding.houseModify1EditTextManageFee.setText(house.manage_fee.toString())
        }

        if (!house.manage_fee_contains.isNullOrEmpty()) {
            val manageFeeTexts = house.manage_fee_contains?.split("\\|")?.toTypedArray()
            manageFeeTexts?.let {
                for (text in it) {
                    toggleButtonGroupManageFeeContains.setToggleButtonCheckedState(text, true)
                }
            }
        }
    }

    private fun setSpinners() {
        // 계약 형태 초기화
        arrayAdapterPayment = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        binding.houseModify1SpinnerPaymentType.adapter = arrayAdapterPayment
        binding.houseModify1SpinnerPaymentType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (binding.houseModify1SpinnerPaymentType.tag == null) {
                    onPaymentTypeItemSelected(parent, view, position, id)
                } else {
                    binding.houseModify1SpinnerPaymentType.tag = null
                    if (paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대") {
                        binding.houseModify1TableRowPrice.visibility = View.GONE
                        binding.houseModify1TableRowDeposit.visibility = View.VISIBLE
                        binding.houseModify1TableRowMonthlyRent.visibility = View.VISIBLE
                    } else {
                        binding.houseModify1TableRowPrice.visibility = View.VISIBLE
                        binding.houseModify1TableRowDeposit.visibility = View.GONE
                        binding.houseModify1TableRowMonthlyRent.visibility = View.GONE
                    }
                    if (houseType == "상가, 점포" && paymentType == "임대") {
                        binding.houseModify1TableRowPremium.visibility = View.VISIBLE
                    } else {
                        binding.houseModify1TableRowPremium.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 매물 종류 초기화
        val arrayAdapterHouse = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterHouse.addAll(*resources.getStringArray(R.array.houseType))
        binding.houseModify1SpinnerHouseType.adapter = arrayAdapterHouse
        binding.houseModify1SpinnerHouseType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (binding.houseModify1SpinnerHouseType.tag == null) {
                    onHouseTypeItemSelected(parent, view, position, id)
                } else {
                    binding.houseModify1CheckBoxFacility.isEnabled = houseType == "상가, 점포"
                    binding.houseModify1SpinnerHouseType.tag = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                arrayAdapterPayment.clear()
            }
        }
    }

    private fun setCheckBoxes() {
        // 관리비 초기화
        binding.houseModify1CheckBoxManageFee.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            binding.houseModify1EditTextManageFee.setText("")
            binding.houseModify1EditTextManageFee.isEnabled = !isChecked
            binding.houseModify1FlowLayoutManageFee.visibility = if (isChecked) View.GONE else View.VISIBLE
            toggleButtonGroupManageFeeContains.resetToggleButtonCheckedState()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showAddressDialog() {
        addressDialog = Dialog(requireContext())
        addressDialog.setContentView(R.layout.dialog_address)

        val webView = addressDialog.findViewById<WebView>(R.id.address_dialog_button_webView)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AndroidBridge(), "GREApp")
        webView.loadUrl(Constants.instance.DNS + "/api/daum-address")

        val params = addressDialog.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        addressDialog.window?.attributes = params

        val buttonBack = addressDialog.findViewById<Button>(R.id.address_dialog_button_back)
        buttonBack.setOnClickListener { addressDialog.dismiss() }
        addressDialog.show()
    }

    private fun parseInt(number: String?): Int {
        return if (number == null || number == "") {
            0
        } else {
            number.toInt()
        }
    }

    private fun parseFloat(number: String?): Float {
        return if (number == null || number == "") {
            0F
        } else {
            number.toFloat()
        }
    }

    private fun onPaymentTypeItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        paymentType = binding.houseModify1SpinnerPaymentType.selectedItem.toString()
        if (paymentType == "월세" || paymentType == "단기임대" || paymentType == "임대") {
            binding.houseModify1TableRowPrice.visibility = View.GONE
            binding.houseModify1TableRowDeposit.visibility = View.VISIBLE
            binding.houseModify1TableRowMonthlyRent.visibility = View.VISIBLE
        } else {
            binding.houseModify1TableRowPrice.visibility = View.VISIBLE
            binding.houseModify1TableRowDeposit.visibility = View.GONE
            binding.houseModify1TableRowMonthlyRent.visibility = View.GONE
        }
        if (houseType == "상가, 점포" && paymentType == "임대") {
            binding.houseModify1TableRowPremium.visibility = View.VISIBLE
        } else {
            binding.houseModify1TableRowPremium.visibility = View.GONE
        }
        binding.houseModify1EditTextPrice.setText("")
        binding.houseModify1EditTextDeposit.setText("")
        binding.houseModify1EditTextMonthlyRent.setText("")
        binding.houseModify1EditTextPremium.setText("")
    }

    private fun onHouseTypeItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        houseType = binding.houseModify1SpinnerHouseType.selectedItem.toString()

        // 시설 유무 초기화
        binding.houseModify1CheckBoxFacility.isEnabled = houseType == "상가, 점포"
        if (position > 0) {
            arrayAdapterPayment.clear()
            arrayAdapterPayment.addAll(Constants.instance.PAYMENT_TYPE[position - 1])
            binding.houseModify1SpinnerPaymentType.setSelection(0)
        } else {
            arrayAdapterPayment.clear()
        }
        binding.houseModify1CheckBoxFacility.isChecked = false
    }

    private inner class AndroidBridge {
        @JavascriptInterface
        fun setAddress(arg1: String?, arg2: String?, arg3: String?) {
            handler.post {
                binding.houseModify1TextViewAddress.text = String.format("(%s) %s %s",
                        arg1 ?: "", arg2 ?: "", arg3 ?: "")
                addressDialog.dismiss()
            }
        }
    }
}