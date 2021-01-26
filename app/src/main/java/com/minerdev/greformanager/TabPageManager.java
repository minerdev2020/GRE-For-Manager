package com.minerdev.greformanager;

import java.util.ArrayList;

public class TabPageManager {
    private ArrayList<TabPage> tabPages = new ArrayList<>();

    public int getItemCount() {
        return tabPages.size();
    }

    public void addItem(TabPage tabPage) {
        tabPages.add(tabPage);
    }

    public void setItems(ArrayList<TabPage> tabPages) {
        this.tabPages = tabPages;
    }

    public TabPage getItem(int position) {
        return tabPages.get(position);
    }

    public void setItem(int position, TabPage tabPage) {
        tabPages.set(position, tabPage);
    }
}
