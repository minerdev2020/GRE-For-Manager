package com.minerdev.greformanager;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public abstract class DataFragment extends Fragment {
    public abstract ArrayList<String> getData();
}