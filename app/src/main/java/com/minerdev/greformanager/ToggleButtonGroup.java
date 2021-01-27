package com.minerdev.greformanager;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleButtonGroup {
    private final ArrayList<ToggleButton> toggleButtons;
    private final HashMap<String, Boolean> toggleButtonCheckedStates;
    private Context context;
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
        final ArrayList<ToggleButton> checkedToggleButtons = new ArrayList<>();
        for (ToggleButton button : toggleButtons) {
            if (button.isChecked()) {
                checkedToggleButtons.add(button);
            }
        }

        return checkedToggleButtons;
    }

    public HashMap<String, Boolean> getToggleButtonCheckedStates() {
        if (toggleButtonCheckedStates.size() != 0) {
            for (Boolean state : toggleButtonCheckedStates.values()) {
                if (state == true) {
                    return toggleButtonCheckedStates;
                }
            }
        }

        return null;
    }

    public Boolean getToggleButtonCheckedState(String buttonText) {
        if (toggleButtonCheckedStates.size() == 0) {
            return false;

        } else {
            Boolean result = toggleButtonCheckedStates.get(buttonText);

            if (result == null || result == false) {
                return false;

            } else {
                return true;
            }
        }
    }

    public String getCheckedToggleButtonTextsInSingleLine() {
        String result = "";
        for (ToggleButton button : toggleButtons) {
            String buttonText = button.getText().toString();
            if (toggleButtonCheckedStates.get(buttonText)) {
                if (!result.equals("")) {
                    result += " | ";
                }

                result += buttonText;
            }
        }

        return result;
    }

    public void addToggleButton(String text) {
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        toggleButton.setText(text);
        toggleButton.setTextOn(text);
        toggleButton.setTextOff(text);
        toggleButton.setMinWidth(0);
        toggleButton.setMinimumWidth(0);

        toggleButtons.add(toggleButton);
    }

    public void addToggleButtons(String... texts) {
        for (String text : texts) {
            addToggleButton(text);
        }
    }

    public void saveCheckedStates() {
        for (ToggleButton button : toggleButtons) {
            toggleButtonCheckedStates.put(button.getText().toString(), button.isChecked());
        }
    }

    public void loadCheckedStates() {
        for (ToggleButton button : toggleButtons) {
            button.setChecked(getToggleButtonCheckedState(button.getText().toString()));
        }
    }

    public void resetCheckedStates() {
        for (ToggleButton button : toggleButtons) {
            button.setChecked(false);
        }

        toggleButtonCheckedStates.clear();
    }

    public void initCheckedStates(HashMap<String, Boolean> checkedStates) {
        if (checkedStates == null) {
            toggleButtonCheckedStates.clear();
        }

        for (ToggleButton button : toggleButtons) {
            if (checkedStates == null) {
                button.setChecked(false);

            } else {
                button.setChecked(checkedStates.get(button.getText().toString()));
                toggleButtonCheckedStates.put(button.getText().toString(), button.isChecked());
            }
        }
    }
}
