package com.minerdev.greformanager;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class TabPage {
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
    private Context context;

    public TabPage(Context context) {
        this.context = context;
    }

    public ArrayList<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    public void addMenu(String tabPageTitle, String menuTitle) {
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleButton.setText(menuTitle);
        toggleButton.setTextOn(menuTitle);
        toggleButton.setTextOff(menuTitle);
        toggleButtons.add(toggleButton);

        Filter.addMenuTitle(tabPageTitle, menuTitle);
    }

    public void addMenus(String tabPageTitle, String... menuTitles) {
        for (String menuTitle : menuTitles) {
            addMenu(tabPageTitle, menuTitle);
        }
    }

    public int getMenuCount() {
        return toggleButtons.size();
    }
}
