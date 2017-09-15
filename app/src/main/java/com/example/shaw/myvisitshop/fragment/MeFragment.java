package com.example.shaw.myvisitshop.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.settingitemlibrary.SetItemView;
import com.example.shaw.myvisitshop.R;

/**
 * 个人中心-主界面
 */
public class MeFragment extends BaseFragment {
    private SetItemView mMeItem;
    private SetItemView mAboutItem;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        initView();
        return view;
    }

    private void initView() {
        mMeItem = (SetItemView) view.findViewById(R.id.rl_me);
        mAboutItem = (SetItemView) view.findViewById(R.id.rl_about);
        mMeItem.setmOnClickItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                Toast.makeText(mActivity, "点击了个人中心按钮", Toast.LENGTH_SHORT).show();
            }
        });
        mAboutItem.setmOnClickItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                Toast.makeText(mActivity, "点击了关于APP按钮", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
