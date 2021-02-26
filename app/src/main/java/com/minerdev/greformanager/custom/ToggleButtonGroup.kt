package com.minerdev.greformanager.custom

import android.content.Context
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ToggleButton
import java.util.*

class ToggleButtonGroup(private val context: Context?, var title: String) {
    val toggleButtons = ArrayList<ToggleButton>()
    private val toggleButtonCheckedStates = HashMap<String, Boolean>()

    val checkedToggleButtons: ArrayList<ToggleButton>
        get() {
            val checkedToggleButtons = ArrayList<ToggleButton>()
            for (button in toggleButtons) {
                if (button.isChecked) {
                    checkedToggleButtons.add(button)
                }
            }

            return checkedToggleButtons
        }

    fun getToggleButtonCheckedState(buttonText: String): Boolean? {
        return if (toggleButtonCheckedStates.size == 0) {
            null
        } else {
            val result = toggleButtonCheckedStates[buttonText]
            result
        }
    }

    fun setToggleButtonCheckedState(buttonText: String, checked: Boolean) {
        if (toggleButtonCheckedStates.size > 0) {
            for (button in toggleButtons) {
                if (button.text == buttonText) {
                    button.isChecked = checked
                    break
                }
            }
        }
    }

    val checkedToggleButtonTextsInSingleLine: String
        get() {
            val result = StringBuilder()
            for (button in toggleButtons) {
                val buttonText = button.text.toString()
                if (toggleButtonCheckedStates[buttonText]!!) {
                    if (result.toString() != "") {
                        result.append("|")
                    }
                    result.append(buttonText)
                }
            }
            return result.toString()
        }

    fun resetToggleButtonCheckedState() {
        if (toggleButtonCheckedStates.size == 0) {
            return
        } else {
            for (button in toggleButtons) {
                button.isChecked = false
            }
        }
    }

    fun addToggleButton(text: String) {
        if (text.isNotEmpty()) {
            val toggleButton = ToggleButton(context)

            toggleButton.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            toggleButton.text = text
            toggleButton.textOn = text
            toggleButton.textOff = text

            toggleButton.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                toggleButtonCheckedStates[buttonView.text.toString()] = isChecked
            }

            toggleButtons.add(toggleButton)
            toggleButtonCheckedStates[text] = false
        }
    }

    fun addToggleButtons(texts: List<String>) {
        for (text in texts) {
            if (text.isNotEmpty()) {
                addToggleButton(text)
            }
        }
    }

    fun addToggleButtonsFromText(text: String) {
        if (text.isNotEmpty()) {
            addToggleButtons(text.split('|'))
        }
    }

    val isAnyCheckedToggleButton: Boolean
        get() {
            if (toggleButtonCheckedStates.size != 0) {
                for (state in toggleButtonCheckedStates.values) {
                    if (state) {
                        return true
                    }
                }
            }

            return false
        }
}