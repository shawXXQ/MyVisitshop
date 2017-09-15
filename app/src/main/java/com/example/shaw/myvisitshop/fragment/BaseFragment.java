package com.example.shaw.myvisitshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.shaw.myvisitshop.R;


/**
 * Created by Shaw on 2017/7/22.
 */

/**
 * Fragment基类
 */

public class BaseFragment extends Fragment {
    protected final String TAG = "微服私访";
    protected Activity mActivity;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext=getActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //转场动画
        getActivity().overridePendingTransition(R.anim.activity_in, 0);
    }
}
