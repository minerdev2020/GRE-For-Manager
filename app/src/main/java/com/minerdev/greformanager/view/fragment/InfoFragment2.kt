package com.minerdev.greformanager.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minerdev.greformanager.R
import com.minerdev.greformanager.databinding.FragmentInfo2Binding
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.utils.Constants.HOUSE_TYPE
import com.minerdev.greformanager.utils.Constants.METER_TO_PYEONG
import com.minerdev.greformanager.utils.Constants.PYEONG_TO_METER
import com.minerdev.greformanager.viewmodel.SharedViewModel

class InfoFragment2 : Fragment(), OnSaveDataListener {
    private val viewModel: SharedViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = SharedViewModel() as T
        }
    }

    private lateinit var binding: FragmentInfo2Binding
    private lateinit var houseType: String
    private lateinit var house: House

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info2, container, false)


        // 스피너 초기화
        setSpinners()


        // 체크박스 초기화
        setCheckBoxes()


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts()


        // 준공년월 초기화
        binding.imageBtnBuiltDate.setOnClickListener {
            showDatePickerDialog(object : OnClickListener {
                override fun onClick(v: View?, result: String?) {
                    binding.tvBuiltDate.text = result
                }
            })
        }


        // 입주가능일 초기화
        binding.imageBtnMoveDate.isEnabled = false
        binding.imageBtnMoveDate.setOnClickListener {
            showDatePickerDialog(object : OnClickListener {
                override fun onClick(v: View?, result: String?) {
                    binding.tvMoveDate.text = result
                }
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        house = viewModel.house
        houseType = HOUSE_TYPE[house.house_type.toInt()]

        // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
        val visibility = if (houseType == "주택" || houseType == "오피스텔") View.VISIBLE else View.GONE
        binding.trStructure.visibility = visibility
        binding.view.visibility = visibility
        binding.spnStructure.setSelection(0)


        // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
        binding.trLocation.visibility = if (houseType == "사무실" || houseType == "상가, 점포") View.VISIBLE else View.GONE
        binding.radioGroup.clearCheck()

        loadData()
    }

    override fun checkData(): Boolean {
        if (binding.etAreaMeter.text.isNullOrEmpty()) {
            return false
        }
        if (binding.etRentAreaMeter.text.isNullOrEmpty()) {
            return false
        }
        if (binding.etBuildingFloor.text.isNullOrEmpty()) {
            return false
        }
        if (!binding.cbUnderground.isChecked && binding.etFloor.text.isNullOrEmpty()) {
            return false
        }
        if ((houseType == "주택" || houseType == "오피스텔")
                && binding.spnStructure.selectedItemId == 0L) {
            return false
        }
        if (binding.spnBathroom.selectedItemId == 0L) {
            return false
        }
        if ((houseType == "사무실" || houseType == "상가, 점포")
                && binding.radioGroup.checkedRadioButtonId == -1) {
            return false
        }
        if (binding.spnDirection.selectedItemId == 0L) {
            return false
        }
        if (binding.tvBuiltDate.text.isNullOrEmpty()) {
            return false
        }
        if (!binding.cbMoveNow.isChecked && binding.tvMoveDate.text.isNullOrEmpty()) {
            return false
        }

        return true
    }

    override fun saveData() {
        house.area_meter = parseFloat(binding.etAreaMeter.text.toString())
        house.rent_area_meter = parseFloat(binding.etRentAreaMeter.text.toString())
        house.building_floor = parseInt(binding.etBuildingFloor.text.toString()).toByte()

        if (binding.cbUnderground.isChecked) {
            house.floor = -1

        } else {
            val floor = binding.etFloor.text.toString()
            house.floor = if (floor.isNotEmpty()) floor.toByte() else 0
        }

        house.structure = binding.spnStructure.selectedItemPosition.toByte()
        house.bathroom = binding.spnBathroom.selectedItemPosition.toByte()

        if (binding.rbOutside.isChecked) {
            house.bathroom_location = 0

        } else {
            house.bathroom_location = 1
        }

        house.direction = binding.spnDirection.selectedItemId.toByte()
        house.built_date = binding.tvBuiltDate.text.toString()
        house.move_date = binding.tvMoveDate.text.toString()
    }

    @SuppressLint("DefaultLocale")
    private fun loadData() {
        binding.etAreaMeter.setText(if (house.area_meter == 0f) "" else house.area_meter.toString())
        binding.etRentAreaMeter.setText(if (house.rent_area_meter == 0f) "" else house.rent_area_meter.toString())
        binding.etAreaPyeong.setText(if (house.area_meter == 0f) "" else String.format("%.2f", house.area_meter * METER_TO_PYEONG))
        binding.etRentAreaPyeong.setText(if (house.rent_area_meter == 0f) "" else String.format("%.2f", house.rent_area_meter * METER_TO_PYEONG))
        binding.etBuildingFloor.setText(if (house.building_floor.toInt() == 0) "" else house.building_floor.toString())

        if (house.floor.toInt() == -1) {
            binding.cbUnderground.isChecked = true

        } else {
            binding.etFloor.setText(if (house.floor.toInt() == 0) "" else house.floor.toString())
        }

        binding.spnStructure.setSelection(house.structure.toInt())
        binding.spnBathroom.setSelection(house.bathroom.toInt())

        if (house.bathroom_location.toInt() == 0) {
            binding.rbOutside.isChecked = true

        } else {
            binding.rbInside.isChecked = true
        }

        binding.spnDirection.setSelection(house.direction.toInt())
        binding.tvBuiltDate.text = house.built_date

        if (house.move_date == "") {
            binding.cbMoveNow.isChecked = true

        } else {
            binding.tvMoveDate.text = house.move_date
        }
    }

    private fun setSpinners() {
        // 구조 초기화
        val arrayAdapterStructure = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterStructure.addAll(*resources.getStringArray(R.array.structure))
        binding.spnStructure.adapter = arrayAdapterStructure


        // 욕실 갯수 초기화
        val arrayAdapterBathroom = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterBathroom.addAll(*resources.getStringArray(R.array.bathroom))
        binding.spnBathroom.adapter = arrayAdapterBathroom


        // 방향 초기화
        val arrayAdapterDirection = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterDirection.addAll(*resources.getStringArray(R.array.direction))
        binding.spnDirection.adapter = arrayAdapterDirection
    }

    private fun setCheckBoxes() {
        // 층 초기화
        binding.cbUnderground.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            binding.etFloor.setText("")
            binding.etFloor.isEnabled = !isChecked
        }


        // 입주 가능일 초기화
        binding.cbMoveNow.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            binding.tvMoveDate.text = ""
            binding.imageBtnMoveDate.isEnabled = !isChecked
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setAreaEditTexts() {
        // 전용 면적 초기화
        binding.etAreaPyeong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etAreaPyeong.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.etAreaMeter.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.etAreaMeter.setText(String.format("%.2f",
                                area * PYEONG_TO_METER))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etAreaMeter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etAreaMeter.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.etAreaPyeong.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.etAreaPyeong.setText(String.format("%.2f",
                                area * METER_TO_PYEONG))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        // 임대 면적 초기화
        binding.etRentAreaPyeong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etRentAreaPyeong.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.etRentAreaMeter.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.etRentAreaMeter.setText(String.format("%.2f", area * 3.305))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etRentAreaMeter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etRentAreaMeter.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.etRentAreaPyeong.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.etRentAreaPyeong.setText(String.format("%.2f", area * 0.3025))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showDatePickerDialog(listener: OnClickListener?) {
        val datePickerDialog = Dialog(requireContext())
        datePickerDialog.setContentView(R.layout.dialog_date_picker)

        val buttonBack = datePickerDialog.findViewById<Button>(R.id.btn_back)
        buttonBack.setOnClickListener { datePickerDialog.dismiss() }

        val buttonSelect = datePickerDialog.findViewById<Button>(R.id.btn_select)
        buttonSelect.setOnClickListener { v: View? ->
            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
            val date = datePicker.year.toString() + "." + datePicker.month + "." + datePicker.dayOfMonth
            listener?.onClick(v, date)
            datePickerDialog.dismiss()
        }

        datePickerDialog.show()
    }

    private fun parseInt(number: String): Int {
        return if (number == "") {
            0
        } else {
            number.toInt()
        }
    }

    private fun parseFloat(number: String): Float {
        return if (number == "") {
            0F
        } else {
            number.toFloat()
        }
    }

    interface OnClickListener {
        fun onClick(v: View?, result: String?)
    }
}