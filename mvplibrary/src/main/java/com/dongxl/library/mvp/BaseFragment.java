package com.dongxl.library.mvp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    protected View rootView;

    protected abstract String getFragmentTag();

    protected abstract int getResourceId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (null != parentView) {
                parentView.removeView(rootView);
            }
            return rootView;
        }
        return inflater.inflate(getResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
    }

    @Override
    public void onDestroyView() {
        if (null != rootView) {
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (null != parentView) {
                parentView.removeView(rootView);
            }
        }
        super.onDestroyView();
    }
}
