package com.example.shaw.myvisitshop.fragment;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaw.myvisitshop.R;
import com.example.settingitemlibrary.SetItemView;
import com.example.shaw.myvisitshop.adapters.VisitShopListAdapter;
import com.example.shaw.myvisitshop.bean.DateList;
import com.example.shaw.myvisitshop.bean.HistoryShopResult;
import com.example.shaw.myvisitshop.net.OkHttpManager;
import com.example.shaw.myvisitshop.utils.Constant;
import com.example.shaw.myvisitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 巡店界面
 */

public class ShopFragment extends BaseFragment implements XRecyclerView.LoadingListener, View.OnClickListener, TextView.OnEditorActionListener {
    private View mView;
    private XRecyclerView recyclerView;
    private RelativeLayout progress, r1_http_failed;
    public static List<DateList> info_list;
    private VisitShopListAdapter adapter;
    private EditText search;
    private String shop_name;
    private int pagenum = 1;//当前页数

    private Boolean isSearch;//是否进入搜索模式
    private String userid;
    private Boolean isLoad;
    public static boolean isFirst;//是否首次登录


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shop, container, false);
        //初始化视图
        initView();
        return mView;
    }

    /**
     * 初始化控件信息
     */
    private void initView() {
        isSearch = false;
        recyclerView = (XRecyclerView) mView.findViewById(R.id.activity_visitshop_list);
        recyclerView.setLoadingListener(this);
        r1_http_failed = (RelativeLayout) mView.findViewById(R.id.activity_visitshop_refresh);
        progress = (RelativeLayout) mView.findViewById(R.id.activity_visitshop_progress);
        r1_http_failed.setOnClickListener(this);
        search = (EditText) mView.findViewById(R.id.et_search_shop);
        search.setOnEditorActionListener(this);
        if (info_list == null) {
            info_list = new ArrayList<>();
        }
        adapter = new VisitShopListAdapter(mContext, info_list);
        //设置加载风格
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        //设置线性列表展示数据
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        );
        recyclerView.setAdapter(adapter);
        //设置空布局
        View emptyView = mView.findViewById(R.id.activity_visitshop_none);
        emptyView.setOnClickListener(this);
        recyclerView.setEmptyView(emptyView);
    }

    @Override
    public void onResume() {
        super.onResume();
        userid = SharePreUtil.GetShareString(mContext, "userid");
        if ("".equals(userid)) {
            isLoad = false;
            Toast.makeText(mContext, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
        } else {
            isLoad = true;
            if (isFirst) {
                isFirst = false;
                pagenum = 1;
                initData();//加载数据
                progress.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 加载数据
     */
    private void initData() {
        Log.i(TAG, "pagenum----" + pagenum);
        //店面查询请求
        String urlString = "";
        if (isSearch) {
            urlString = Constant.HistroyShop + "?userid=" + userid + "&pagenum=" + pagenum + "&keyword=" + shop_name;
        } else {
            urlString = Constant.HistroyShop + "?userid=" + userid + "&pagenum=" + pagenum;
        }
        OkHttpManager.getInstance().getNet(urlString, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, Exception e) {
                getShopFailed();
            }

            @Override
            public void onSuccessed(String response) {
                getShopSucceed(response);
            }
        });
    }

    /**
     * 获取历史巡店信息失败
     */
    private void getShopFailed() {
        onLoad();
        progress.setVisibility(View.GONE);
        Toast.makeText(mContext, "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT).show();
        //获取本地数据库信息
        if (info_list == null || info_list.size() == 0) {
            info_list = DataSupport.findAll(DateList.class);
            if (info_list == null) {
                info_list = new ArrayList<>();
            }
            if (info_list.size() > 0) {
                //显示联网失败刷新界面
                r1_http_failed.setVisibility(View.VISIBLE);
                adapter.setList(info_list);
                adapter.notifyDataSetChanged();
            } else {
                r1_http_failed.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取历史巡店信息成功
     */
    private void getShopSucceed(String resultStr) {
        onLoad();
        r1_http_failed.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (resultStr != null && !"".equals(resultStr)) {
            Gson gson = new Gson();
            HistoryShopResult infos = gson.fromJson(resultStr, HistoryShopResult.class);
            if (infos != null) {
                if (infos.getCode() == 0) {
                    if (infos.getDatelist() != null) {
                        if (info_list == null) {
                            info_list = new ArrayList<>();
                        }
                        //如果是第一页数据，则把之前的内容清空
                        if (pagenum == 1) {
                            info_list.clear();
                        }
                        if (infos.getDatelist().size() == 0) {
                            Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        } else {
                            info_list.addAll(infos.getDatelist());
                        }
                        //当前页数递增
                        pagenum++;
                        adapter.setList(info_list);
                        adapter.notifyDataSetChanged();
                        //从数据库清除数据保存
                        DataSupport.deleteAll(DateList.class);
                        //添加新数据到数据库
                        DataSupport.saveAll(infos.getDatelist());
                    }
                }
            }
        }
    }


    @Override
    public void onRefresh() {
        //下拉刷新,默认从第一页开始加载
        pagenum = 1;
        initData();
    }

    @Override
    public void onLoadMore() {
        //上拉加载更多
        initData();
    }

    /**
     * 结束上下拉刷新
     */
    private void onLoad() {
        recyclerView.refreshComplete();
        recyclerView.loadMoreComplete();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_visitshop_refresh://加载失败后点击刷新
                r1_http_failed.setVisibility(View.GONE);
                initData();
                break;
            case R.id.activity_visitshop_none://没有数据点击重新请求
                initData();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        //当点击搜索按钮时
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            //隐藏软键盘
            hideKeyboard();
            shop_name = search.getText().toString().trim();
            progress.setVisibility(View.VISIBLE);
            pagenum = 1;
            //店面查询请求
            String urlString = Constant.HistroyShop + "?userid=" + userid + "&pagenum=" + pagenum + "&keyword=" + shop_name;
            OkHttpManager.getInstance().getNet(urlString, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, Exception e) {
                    getShopFailed();
                }

                @Override
                public void onSuccessed(String response) {
                    getShopSucceed(response);
                }
            });
            isSearch=true;
        }
        return false;
    }

    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(mActivity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清空静态变量
        info_list=null;
        isFirst=false;
    }
}
