package com.triskelapps.busjerez.base;

import com.triskelapps.busjerez.ui.main.MainActivity;
import com.triskelapps.busjerez.ui.main.MainPresenter;

public class BaseMainFragment extends BaseFragment {

    public MainPresenter getMainPresenter() {
        return (MainPresenter) ((MainActivity) getActivity()).getBasePresenter();
    }
}
