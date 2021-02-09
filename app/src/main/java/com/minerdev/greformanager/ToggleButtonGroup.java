package com.minerdev.greformanager;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleButtonGroup {
    private final ArrayList<ToggleButton> toggleButtons;
    private final HashMap<String, Boolean> toggleButtonCheckedStates;
    private final Context context;
    private String title;

    public ToggleButtonGroup(Context context, String title) {
        this.context = context;
        this.title = title;

        toggleButtons = new ArrayList<>();
        toggleButtonCheckedStates = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ToggleButton> getToggleButtons() {
        if (toggleButtons.size() == 0) {
            return null;

        } else {
            return toggleButtons;
        }
    }

    public ArrayList<ToggleButton> getCheckedToggleButtons() {
        if (toggleButtonCheckedStates.size() == 0) {
            return null;
        }

        final ArrayList<ToggleButton> checkedToggleButtons = new ArrayList<>();
        for (ToggleButton button : toggleButtons) {
            if (button.isChecked()) {
                checkedToggleButtons.add(button);
            }
        }

        return checkedToggleButtons;
    }

    public HashMap<String, Boolean> getToggleButtonCheckedStates() {
        if (toggleButtonCheckedStates.size() == 0) {
            return null;

        } else {
            return toggleButtonCheckedStates;
        }
    }

    public Boolean getToggleButtonCheckedState(String buttonText) {
        if (toggleButtonCheckedStates.size() == 0) {
            return null;

        } else {
            Boolean result = toggleButtonCheckedStates.get(buttonText);
            return result != null && result;
        }
    }

    public void setToggleButtonCheckedState(String buttonText, boolean checked) {
        if (toggleButtonCheckedStates.size() == 0) {
            return;

        } else {
            for (ToggleButton button : toggleButtons) {
                if (button.getText().equals(buttonText)) {
                    button.setChecked(checked);
                    return;
                }
            }
        }
    }

    public String getCheckedToggleButtonTextsInSingleLine() {
        StringBuilder result = new StringBuilder();
        for (ToggleButton button : toggleButtons) {
            String buttonText = button.getText().toString();
            if (toggleButtonCheckedStates.get(buttonText)) {
                if (!result.toString().equals("")) {
                    result.append("|");
                }

                result.append(buttonText);
            }
        }

        return result.toString();
    }

    public void addToggleButton(String text) {
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        toggleButton.setText(text);
        toggleButton.setTextOn(text);
        toggleButton.setTextOff(text);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleButtonCheckedStates.put(buttonView.getText().toString(), isChecked));

        toggleButtons.add(toggleButton);
        toggleButtonCheckedStates.put(text, false);
    }

    public void addToggleButtons(String... texts) {
        for (String text : texts) {
            addToggleButton(text);
        }
    }

    public boolean isAnyCheckedToggleButton() {
        if (toggleButtonCheckedStates.size() != 0) {
            for (Boolean state : toggleButtonCheckedStates.values()) {
                if (state) {
                    return true;
                }
            }
        }

        return false;
    }
}