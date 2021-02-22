package com.minerdev.greformanager

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
import com.minerdev.greformanager.databinding.FragmentInfo2Binding

class InfoFragment2 : Fragment(), OnSaveDataListener {
    private val viewModel: HouseModifyViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = HouseModifyViewModel() as T
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
        binding.houseModify2ImageButtonBuiltDate.setOnClickListener {
            showDatePickerDialog(object : OnClickListener {
                override fun onClick(v: View?, result: String?) {
                    binding.houseModify2TextViewBuiltDate.text = result
                }
            })
        }


        // 입주가능일 초기화
        binding.houseModify2ImageButtonMoveDate.isEnabled = false
        binding.houseModify2ImageButtonMoveDate.setOnClickListener {
            showDatePickerDialog(object : OnClickListener {
                override fun onClick(v: View?, result: String?) {
                    binding.houseModify2TextViewMoveDate.text = result
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
        houseType = Constants.instance.HOUSE_TYPE[house.house_type.toInt()]

        // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
        val visibility = if (houseType == "주택" || houseType == "오피스텔") View.VISIBLE else View.GONE
        binding.houseModify2TableRowStructure.visibility = visibility
        binding.houseModify2View.visibility = visibility
        binding.houseModify2SpinnerStructure.setSelection(0)


        // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
        binding.houseModify2TableRowLocation.visibility = if (houseType == "사무실" || houseType == "상가, 점포") View.VISIBLE else View.GONE
        binding.houseModify2RadioGroup.clearCheck()

        loadData()
    }

    override fun checkData(): Boolean {
        if (binding.houseModify2EditTextAreaMeter.text.toString() == "") {
            return false
        }
        if (binding.houseModify2EditTextRentAreaMeter.text.toString() == "") {
            return false
        }
        if (binding.houseModify2EditTextBuildingFloor.text.toString() == "") {
            return false
        }
        if (!binding.houseModify2CheckBoxUnderground.isChecked
                && binding.houseModify2EditTextFloor.text.toString() == "") {
            return false
        }
        if ((houseType == "주택" || houseType == "오피스텔")
                && binding.houseModify2SpinnerStructure.selectedItemId == 0L) {
            return false
        }
        if (binding.houseModify2SpinnerBathroom.selectedItemId == 0L) {
            return false
        }
        if ((houseType == "사무실" || houseType == "상가, 점포")
                && binding.houseModify2RadioGroup.checkedRadioButtonId == -1) {
            return false
        }
        if (binding.houseModify2SpinnerDirection.selectedItemId == 0L) {
            return false
        }
        if (binding.houseModify2TextViewBuiltDate.text.toString() == "") {
            return false
        }
        return !(!binding.houseModify2CheckBoxMoveNow.isChecked
                && binding.houseModify2TextViewMoveDate.text.toString() == "")
    }

    override fun saveData() {
        house.area_meter = parseFloat(binding.houseModify2EditTextAreaMeter.text.toString())
        house.rent_area_meter = parseFloat(binding.houseModify2EditTextRentAreaMeter.text.toString())
        house.building_floor = parseInt(binding.houseModify2EditTextBuildingFloor.text.toString()).toByte()

        if (binding.houseModify2CheckBoxUnderground.isChecked) {
            house.floor = -1

        } else {
            val floor = binding.houseModify2EditTextFloor.text.toString()
            house.floor = if (floor.isNotEmpty()) floor.toByte() else 0
        }

        house.structure = binding.houseModify2SpinnerStructure.selectedItemPosition.toByte()
        house.bathroom = binding.houseModify2SpinnerBathroom.selectedItemPosition.toByte()

        if (binding.houseModify2RadioButtonOutside.isChecked) {
            house.bathroom_location = 0

        } else {
            house.bathroom_location = 1
        }

        house.direction = binding.houseModify2SpinnerDirection.selectedItemId.toByte()
        house.built_date = binding.houseModify2TextViewBuiltDate.text.toString()
        house.move_date = binding.houseModify2TextViewMoveDate.text.toString()
    }

    @SuppressLint("DefaultLocale")
    private fun loadData() {
        binding.houseModify2EditTextAreaMeter.setText(if (house.area_meter == 0f) "" else house.area_meter.toString())
        binding.houseModify2EditTextRentAreaMeter.setText(if (house.rent_area_meter == 0f) "" else house.rent_area_meter.toString())
        binding.houseModify2EditTextAreaPyeong.setText(if (house.area_meter == 0f) "" else String.format("%.2f", house.area_meter * Constants.instance.METER_TO_PYEONG))
        binding.houseModify2EditTextRentAreaPyeong.setText(if (house.rent_area_meter == 0f) "" else String.format("%.2f", house.rent_area_meter * Constants.instance.METER_TO_PYEONG))
        binding.houseModify2EditTextBuildingFloor.setText(if (house.building_floor.toInt() == 0) "" else house.building_floor.toString())

        if (house.floor.toInt() == -1) {
            binding.houseModify2CheckBoxUnderground.isChecked = true

        } else {
            binding.houseModify2EditTextFloor.setText(if (house.floor.toInt() == 0) "" else house.floor.toString())
        }

        binding.houseModify2SpinnerStructure.setSelection(house.structure.toInt())
        binding.houseModify2SpinnerBathroom.setSelection(house.bathroom.toInt())

        if (house.bathroom_location.toInt() == 0) {
            binding.houseModify2RadioButtonOutside.isChecked = true

        } else {
            binding.houseModify2RadioButtonInside.isChecked = true
        }

        binding.houseModify2SpinnerDirection.setSelection(house.direction.toInt())
        binding.houseModify2TextViewBuiltDate.text = house.built_date

        if (house.move_date == "") {
            binding.houseModify2CheckBoxMoveNow.isChecked = true

        } else {
            binding.houseModify2TextViewMoveDate.text = house.move_date
        }
    }

    private fun setSpinners() {
        // 구조 초기화
        val arrayAdapterStructure = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterStructure.addAll(*resources.getStringArray(R.array.structure))
        binding.houseModify2SpinnerStructure.adapter = arrayAdapterStructure


        // 욕실 갯수 초기화
        val arrayAdapterBathroom = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterBathroom.addAll(*resources.getStringArray(R.array.bathroom))
        binding.houseModify2SpinnerBathroom.adapter = arrayAdapterBathroom


        // 방향 초기화
        val arrayAdapterDirection = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        arrayAdapterDirection.addAll(*resources.getStringArray(R.array.direction))
        binding.houseModify2SpinnerDirection.adapter = arrayAdapterDirection
    }

    private fun setCheckBoxes() {
        // 층 초기화
        binding.houseModify2CheckBoxUnderground.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            binding.houseModify2EditTextFloor.setText("")
            binding.houseModify2EditTextFloor.isEnabled = !isChecked
        }


        // 입주 가능일 초기화
        binding.houseModify2CheckBoxMoveNow.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            binding.houseModify2TextViewMoveDate.text = ""
            binding.houseModify2ImageButtonMoveDate.isEnabled = !isChecked
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setAreaEditTexts() {
        // 전용 면적 초기화
        binding.houseModify2EditTextAreaPyeong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.houseModify2EditTextAreaPyeong.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.houseModify2EditTextAreaMeter.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.houseModify2EditTextAreaMeter.setText(String.format("%.2f",
                                area * Constants.instance.PYEONG_TO_METER))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.houseModify2EditTextAreaMeter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.houseModify2EditTextAreaMeter.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.houseModify2EditTextAreaPyeong.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.houseModify2EditTextAreaPyeong.setText(String.format("%.2f",
                                area * Constants.instance.METER_TO_PYEONG))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        // 임대 면적 초기화
        binding.houseModify2EditTextRentAreaPyeong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.houseModify2EditTextRentAreaPyeong.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.houseModify2EditTextRentAreaMeter.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.houseModify2EditTextRentAreaMeter.setText(String.format("%.2f", area * 3.305))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.houseModify2EditTextRentAreaMeter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.houseModify2EditTextRentAreaMeter.hasFocus()) {
                    val temp = s.toString()
                    if (temp == "") {
                        binding.houseModify2EditTextRentAreaPyeong.setText("")
                    } else {
                        val area = temp.toFloat()
                        binding.houseModify2EditTextRentAreaPyeong.setText(String.format("%.2f", area * 0.3025))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showDatePickerDialog(listener: OnClickListener?) {
        val datePickerDialog = Dialog(requireContext())
        datePickerDialog.setContentView(R.layout.dialog_date_picker)
        val buttonBack = datePickerDialog.findViewById<Button>(R.id.date_picker_dialog_button_back)
        buttonBack.setOnClickListener { datePickerDialog.dismiss() }
        val buttonSelect = datePickerDialog.findViewById<Button>(R.id.date_picker_dialog_button_select)
        buttonSelect.setOnClickListener { v: View? ->
            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.date_picker_dialog_datePicker)
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