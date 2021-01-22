package com.minerdev.greformanager;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class TabMenu {
    private Context context;
    private ArrayList<ToggleButton> toggleButtons;

    public TabMenu(Context context) {
        this.context = context;
        toggleButtons = new ArrayList<>();
    }

    public void addMenu(String menuTitle) {
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleButton.setText(menuTitle);
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

    public ArrayList<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    public int getMenuCount() {
        return toggleButtons.size();
    }
}
