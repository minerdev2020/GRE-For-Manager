package com.minerdev.greformanager;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class TabMenu {
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
    private Context context;

    public TabMenu(Context context) {
        this.context = context;
    }

    public ArrayList<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    public void addMenu(String menuTitle) {
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleButton.setText(menuTitle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String buttonText = toggleButton.getText().toString();
                FilterActivity.filter.setCheckState(buttonText, isChecked);
            }
        });
        toggleButtons.add(toggleButton);
    }

    public void addMenus(String... menuTitles) {
        for (String menuTitle : menuTitles) {
            ToggleButton toggleButton = new ToggleButton(context);
            toggleButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            toggleButton.setText(menuTitle);
            toggleButton.setTextOn(menuTitle);
            toggleButton.setTextOff(menuTitle);
            toggleButtons.add(toggleButton);
        }
    }

    public int getMenuCount() {
        return toggleButtons.size();
    }
}
