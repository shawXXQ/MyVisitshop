package com.example.shaw.myvisitshop;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaw.myvisitshop.Activity.BaseFragmentActivity;
import com.example.shaw.myvisitshop.Activity.CreateVisitShopActivity;
import com.example.shaw.myvisitshop.fragment.HomeFragment;
import com.example.shaw.myvisitshop.fragment.MeFragment;
import com.example.shaw.myvisitshop.fragment.ShopFragment;
import com.example.shaw.myvisitshop.fragment.TrainFragment;
import com.example.shaw.myvisitshop.fragment.VisitFragment;
import com.example.shaw.myvisitshop.utils.SharePreUtil;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ViewPager viewPager_content;
    private TextView txt_menu_bottom_home;
    private TextView txt_menu_bottom_shop;
    private TextView txt_menu_bottom_visit;
    private TextView txt_menu_bottom_train;
    private TextView txt_menu_bottom_me;
    private final int TAB_HOME = 0;
    private final int TAB_SHOP = 1;
    private final int TAB_VISIT = 2;
    private final int TAB_TRAIN = 3;
    private final int TAB_ME = 4;
    private int IsTab;

    private HomeFragment homeFragment;
    private ShopFragment shopFragment;
    private VisitFragment visitFragment;
    private TrainFragment trainFragment;
    private MeFragment meFragment;

    private FragmentAdapter adapter;
    private ImageView title_bar_more, title_bar_change;

    private String userid;
    private boolean isLoad;//是否登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化绑定ID
        initID();
        //初始化视图
        initView();
    }

    /**
     * 检测用户是否登录，给予提示
     */
    @Override
    protected void onResume() {
        userid = SharePreUtil.GetShareString(mContext, "userid");//获取保存的id
        if ("".equals(userid)) {
            //判断用户是否登录
            isLoad = false;
            Toast.makeText(mContext, "您尚未登录，请登录", Toast.LENGTH_SHORT).show();
        } else {
            isLoad = true;
        }
        super.onResume();
    }

    /**
     * 初始化控件加载
     */
    public void initID() {
        TextView title_bar_back = (TextView) findViewById(R.id.title_bar_back);
        title_bar_back.setVisibility(View.GONE);

        viewPager_content = (ViewPager) findViewById(R.id.viewPager_content);
        txt_menu_bottom_home = (TextView) findViewById(R.id.txt_menu_bottom_home);
        txt_menu_bottom_me = (TextView) findViewById(R.id.txt_menu_bottom_me);
        txt_menu_bottom_shop = (TextView) findViewById(R.id.txt_menu_bottom_shop);
        txt_menu_bottom_train = (TextView) findViewById(R.id.txt_menu_bottom_train);
        txt_menu_bottom_visit = (TextView) findViewById(R.id.txt_menu_bottom_visit);
        title_bar_more = (ImageView) findViewById(R.id.title_bar_more);
        title_bar_change = (ImageView) findViewById(R.id.title_bar_change);
        txt_menu_bottom_home.setOnClickListener(this);
        txt_menu_bottom_shop.setOnClickListener(this);
        txt_menu_bottom_visit.setOnClickListener(this);
        txt_menu_bottom_train.setOnClickListener(this);
        txt_menu_bottom_me.setOnClickListener(this);
        title_bar_more.setOnClickListener(this);
        title_bar_change.setOnClickListener(this);

        //ViewPager的滑动监听
        viewPager_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("main_viewpager", "position--" + position);
                switch (position) {
                    case TAB_HOME://点击首页模块执行
                        IsTab = 1;
                        jumpHomeFragment();
                        break;
                    case TAB_SHOP://点击巡店模块执行
                        IsTab = 2;
                        jumpShopFragment();
                        break;
                    case TAB_VISIT://点击拜访模块执行
                        IsTab = 3;
                        jumpVisitFragment();
                        break;
                    case TAB_TRAIN://点击培训模块执行
                        IsTab = 4;
                        jumpTrainFragment();
                        break;
                    case TAB_ME://点击个人中心模块执行
                        IsTab = 5;
                        title_bar_more.setVisibility(View.GONE);
                        title_bar_change.setVisibility(View.GONE);
                        setSelected(txt_menu_bottom_me);
                        viewPager_content.setCurrentItem(TAB_ME, false);
                        setTitleName("个人中心");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化视图--默认显示首页
     */
    public void initView() {
        isLoad = false;
        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        visitFragment = new VisitFragment();
        trainFragment = new TrainFragment();
        meFragment = new MeFragment();
        adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager_content.setAdapter(adapter);
        setSelected(txt_menu_bottom_home);
        viewPager_content.setCurrentItem(TAB_HOME, false);
        setTitleName("首页");
    }

    @Override
    public void onClick(View view) {
        if (!isLoad) {
            Toast.makeText(mContext, "您尚未登录，请登录", Toast.LENGTH_SHORT).show();
        }
        switch (view.getId()) {
            case R.id.txt_menu_bottom_home://点击首页模块执行
                IsTab = 1;
                title_bar_change.setVisibility(View.GONE);
                title_bar_more.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_home);
                viewPager_content.setCurrentItem(TAB_HOME, false);
                setTitleName("首页");
                break;
            case R.id.txt_menu_bottom_shop://点击巡店模块执行
                IsTab = 2;
                title_bar_change.setVisibility(View.VISIBLE);
                title_bar_more.setVisibility(View.VISIBLE);
                setSelected(txt_menu_bottom_shop);
                viewPager_content.setCurrentItem(TAB_SHOP, false);
                setTitleName("巡店");
                break;
            case R.id.txt_menu_bottom_visit:
                IsTab = 3;
                title_bar_change.setVisibility(View.GONE);
                title_bar_more.setVisibility(View.VISIBLE);
                setSelected(txt_menu_bottom_visit);
                viewPager_content.setCurrentItem(TAB_VISIT, false);
                setTitleName("拜访");
                break;
            case R.id.txt_menu_bottom_train:
                IsTab = 4;
                title_bar_change.setVisibility(View.GONE);
                title_bar_more.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_train);
                viewPager_content.setCurrentItem(TAB_TRAIN, false);
                setTitleName("培训");
                break;
            case R.id.txt_menu_bottom_me:
                IsTab = 5;
                title_bar_more.setVisibility(View.GONE);
                title_bar_more.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_me);
                viewPager_content.setCurrentItem(TAB_ME, false);
                setTitleName("个人中心");
                break;
            case R.id.title_bar_more:
                if (isLoad) {
                    if (IsTab == 2) {//新建巡店
                        startActivity(new Intent(mActivity, CreateVisitShopActivity.class));
                    } else if (IsTab == 3) {//新建拜访
                        Toast.makeText(mContext, "新建拜访", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void setSelected(TextView textView) {
        txt_menu_bottom_me.setSelected(false);
        txt_menu_bottom_train.setSelected(false);
        txt_menu_bottom_shop.setSelected(false);
        txt_menu_bottom_visit.setSelected(false);
        txt_menu_bottom_home.setSelected(false);
        textView.setSelected(true);
    }


    /**
     * fragment适配器
     */
    public class FragmentAdapter extends FragmentPagerAdapter {
        private final int TAB_COUNT = 5;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_HOME:
                    return homeFragment;
                case TAB_SHOP:
                    return shopFragment;
                case TAB_VISIT:
                    return visitFragment;
                case TAB_TRAIN:
                    return trainFragment;
                case TAB_ME:
                    return meFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity死亡userid保存
//        SharePreUtil.SetShareString(mContext, "userid", "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听返回键，如果当前界面不是首界面，或没切换过界面，则切到首界面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (IsTab != 1) {
                IsTab = 1;
                jumpHomeFragment();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示主界面HomeFragment
     */
    private void jumpHomeFragment() {
        title_bar_change.setVisibility(View.GONE);
        title_bar_more.setVisibility(View.GONE);
        setSelected(txt_menu_bottom_home);
        viewPager_content.setCurrentItem(TAB_HOME, false);
        setTitleName("首页");
    }

    /**
     * 切换到巡店Fragment
     */

    private void jumpShopFragment() {
        title_bar_change.setVisibility(View.VISIBLE);
        title_bar_more.setVisibility(View.VISIBLE);
        setSelected(txt_menu_bottom_shop);
        viewPager_content.setCurrentItem(TAB_SHOP, false);
        setTitleName("巡店");
    }

    /**
     * 切换到拜访Fragment
     */
    private void jumpVisitFragment() {
        title_bar_change.setVisibility(View.GONE);
        title_bar_more.setVisibility(View.VISIBLE);
        setSelected(txt_menu_bottom_visit);
        viewPager_content.setCurrentItem(TAB_VISIT, false);
        setTitleName("拜访");
    }

    /**
     * 切换到培训Fragment
     */
    private void jumpTrainFragment() {
        title_bar_change.setVisibility(View.GONE);
        title_bar_more.setVisibility(View.GONE);
        setSelected(txt_menu_bottom_train);
        viewPager_content.setCurrentItem(TAB_TRAIN, false);
        setTitleName("培训");
    }
}


