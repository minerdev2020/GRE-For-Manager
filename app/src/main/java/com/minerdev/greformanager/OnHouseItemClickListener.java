package com.minerdev.greformanager;

import android.view.View;

public interface OnHouseItemClickListener {
    public void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position);

    public void onItemLongClick(HouseListAdapter.ViewHolder viewHolder, View view, int position);
}
