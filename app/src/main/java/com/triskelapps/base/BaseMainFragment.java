package com.triskelapps.base;

import com.triskelapps.ui.main.MainActivity;
import com.triskelapps.ui.main.MainPresenter;

public class BaseMainFragment extends BaseFragment {

    public MainPresenter getMainPresenter() {
        return (MainPresenter) ((MainActivity) getActivity()).getBasePresenter();
    }
}
