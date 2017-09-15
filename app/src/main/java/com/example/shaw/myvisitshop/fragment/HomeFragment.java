package com.example.shaw.myvisitshop.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaw.myvisitshop.MainActivity;
import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.adapters.InfoListBaseAdapter;
import com.example.shaw.myvisitshop.adapters.TaskListBaseAdapter;
import com.example.shaw.myvisitshop.bean.AnnImageResult;
import com.example.shaw.myvisitshop.bean.AnnImgs;
import com.example.shaw.myvisitshop.bean.InfoResult;
import com.example.shaw.myvisitshop.bean.InfoResultBody;
import com.example.shaw.myvisitshop.bean.Task;
import com.example.shaw.myvisitshop.bean.TaskBody;
import com.example.shaw.myvisitshop.net.OkHttpManager;
import com.example.shaw.myvisitshop.utils.Constant;
import com.example.shaw.myvisitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

/**
 * 首页-默认展示
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private RelativeLayout progress, home_fragment_task;
    private TextView task_more, info_more;
    //三个图标的实例
    private TextView sort_shop, sort_visit, sort_train;
    private InfoListBaseAdapter info_adapter;
    private TaskListBaseAdapter task_adapter;
    private MainActivity mainActivity;
    private RecyclerView task_rv, info_rv;
    private LinearLayout r1_http_failed;
    private ViewPager vp;
    private List<View> views;//轮播图图片集合
    private MViewpager vp_adapter;
    private Timer timer;//轮播图计时器
    private LinearLayout layout;//轮播图下标集合
    private int count = 0;//轮播图当前下标
    public final int GetImags = 1014;//获取广告图返回码
    public final int AnnFaild = 1011;//获取广告图失败返回码

    private String userid;
    private boolean isLoad;
    private List<InfoResultBody> info_list;
    private List<TaskBody> task_list;
    private int mShowSize = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化UI控件
        initView();
        //初始化数据
        initData();
        return mView;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        vp = (ViewPager) mView.findViewById(R.id.fragment_img_viewpager);
        layout = (LinearLayout) mView.findViewById(R.id.fragment_point_subscript);
        home_fragment_task = (RelativeLayout) mView.findViewById(R.id.home_fragment_task);
        progress = (RelativeLayout) mView.findViewById(R.id.message_fregment_progress);
        r1_http_failed = (LinearLayout) mView.findViewById(R.id.rl_http_failed);
        r1_http_failed.setOnClickListener(this);
        task_more = (TextView) mView.findViewById(R.id.fragment_home_task_more);
        task_more.setOnClickListener(this);
        info_more = (TextView) mView.findViewById(R.id.fragment_home_info_more);
        info_more.setOnClickListener(this);
        sort_shop = (TextView) mView.findViewById(R.id.fragment_sort_shop);
        sort_shop.setOnClickListener(this);
        sort_visit = (TextView) mView.findViewById(R.id.fragment_sort_visit);
        sort_visit.setOnClickListener(this);
        sort_train = (TextView) mView.findViewById(R.id.fragment_sort_train);
        sort_train.setOnClickListener(this);
        task_rv = (RecyclerView) mView.findViewById(R.id.fragment_home_task_list);
        info_rv = (RecyclerView) mView.findViewById(R.id.fragment_home_info_list);

        task_rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        info_rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        //使RecyclerView的滑动更加顺畅
        task_rv.setNestedScrollingEnabled(false);
        info_rv.setNestedScrollingEnabled(false);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userid = SharePreUtil.GetShareString(mActivity, "userid");
        if (TextUtils.isEmpty(userid)) {
            isLoad = false;
            Toast.makeText(mContext, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
        } else {
            isLoad = true;
        }
        if (views == null) {
            views = new ArrayList<View>();
        }
        vp_adapter = new MViewpager();
        vp.setAdapter(vp_adapter);
        //添加界面滚动监听
        vp.addOnPageChangeListener(vp_adapter);
        progress.setVisibility(View.VISIBLE);
        //首页轮播图获取
        OkHttpManager.getInstance().getNet(
                Constant.Announcement, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, Exception e) {
                        getAnnFailure();
                    }

                    @Override
                    public void onSuccessed(String response) {
                        getAnnSuccess(response);
                    }
                }
        );
        //请求资讯
        OkHttpManager.getInstance().getNet(
                Constant.Info + "?pagenum=" + 1 + "&type=" + 0, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, Exception e) {
                        getInfoFailed();
                    }

                    @Override
                    public void onSuccessed(String response) {
                        getInfoSuccess(response);
                    }
                }
        );

        //登录后请求任务
        if (isLoad) {
            home_fragment_task.setVisibility(View.VISIBLE);
            //发送网络请求获取数据
            OkHttpManager.getInstance().getNet(
                    Constant.Task + "?pagenum=" + 1, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onFailed(Request request, Exception e) {
                            getTaskFailed();
                        }

                        @Override
                        public void onSuccessed(String response) {
                            getTaskSuccess(response);
                        }
                    }
            );
        }
    }


    /**
     * 从服务端获取任务失败
     * 此时展示数据库缓存
     */
    private void getTaskFailed() {
        progress.setVisibility(View.GONE);
        r1_http_failed.setVisibility(View.VISIBLE);
        //展示保存的数据
        task_list = DataSupport.findAll(TaskBody.class);
        if (task_list != null) {
            task_adapter = new TaskListBaseAdapter(mContext, task_list, mShowSize);
            task_rv.setAdapter(task_adapter);
        }
    }

    /**
     * 从服务端获取任务成功
     */

    private void getTaskSuccess(String response) {
        progress.setVisibility(View.GONE);
        Gson gson_task = new Gson();
        Task task = gson_task.fromJson(response, Task.class);
        //适配器列表
        if (task.getBody() != null) {
            task_list = task.getBody();
            task_adapter = new TaskListBaseAdapter(mContext, task_list, mShowSize);
            task_rv.setAdapter(task_adapter);
            //从数据库清除数据保存
            DataSupport.deleteAll(TaskBody.class);
            //保存新的数据到数据库
            DataSupport.saveAll(task.getBody());
        }
    }

    /**
     * 从服务端获取资讯列表失败
     * 此时展示数据库缓存
     */
    private void getInfoFailed() {
        progress.setVisibility(View.GONE);
        r1_http_failed.setVisibility(View.VISIBLE);
        //展示保存数据
        info_list = DataSupport.findAll(InfoResultBody.class);
        if (info_list != null) {
            info_adapter = new InfoListBaseAdapter(mContext, info_list, mShowSize);
            info_rv.setAdapter(info_adapter);
        }
    }

    /**
     * 从服务端获取资讯列表成功
     */

    private void getInfoSuccess(String response) {
        progress.setVisibility(View.GONE);
        Gson gson_info = new Gson();
        InfoResult infoResult = gson_info.fromJson(response, InfoResult.class);
        //适配资讯列表
        if (infoResult.getBody() != null) {
            info_list = infoResult.getBody();
            info_adapter = new InfoListBaseAdapter(mContext, info_list, mShowSize);
            info_rv.setAdapter(info_adapter);
            //从数据库清除数据保存
            DataSupport.deleteAll(InfoResultBody.class);
            //保存新的数据到数据库
            DataSupport.saveAll(infoResult.getBody());
        }
    }

    /**
     * 从服务端获取轮播图数据失败时
     * 展示数据库中的缓存数据
     */
    private void getAnnFailure() {
        //从数据库中获取数据
        List<AnnImgs> imgs_dblist = DataSupport.findAll(AnnImgs.class);
        if (imgs_dblist != null) {
            //展示数据
            updateAnnShow(imgs_dblist);
        }
    }

    /**
     * 从服务端获取轮播图数据成功时
     */

    private void getAnnSuccess(String resultImgs) {
        //服务端返回有效数据时展示，没有不做展示
        if (resultImgs != null && !"".equals(resultImgs)) {
            Gson gson = new Gson();
            AnnImageResult air = gson.fromJson(resultImgs, AnnImageResult.class);
            List<AnnImgs> imgs_list = air.getBody();
            if (imgs_list == null) {
                imgs_list = new ArrayList<>();
            }
            updateAnnShow(imgs_list);
            //更新数据库缓存
            if (imgs_list.size() > 0) {
                //先清除数据库缓存
                DataSupport.deleteAll(AnnImgs.class);
                //添加新数据到数据库缓存
                DataSupport.saveAll(imgs_list);
            }
        }
    }


    /**
     * 根据公告图片地址动态更新界面
     *
     * @param imgs_list
     */
    private void updateAnnShow(List<AnnImgs> imgs_list) {
        views.clear();
        //动态创建轮播图展示view
        for (int i = 0; i < imgs_list.size(); i++) {
            ImageView img = new ImageView(mActivity);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //通过网络地址显示图片
            Picasso.with(mActivity)
                    .load(Constant.BaseUrl + imgs_list.get(i).getImgUrl())
                    .into(img);
            views.add(img);
        }
        //更新界面显示
        vp_adapter.notifyDataSetChanged();
        //添加指示器下标
        initPoint();
        //开启任务计时器，实现自动轮播效果
        if (timer == null) {
            timer = new Timer();
            timer.schedule(task, 0, 3000);
        }
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.fragment_sort_shop:
//                Toast.makeText(mainActivity, "点击了首页的巡店按钮", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.fragment_sort_visit:
//                Toast.makeText(mainActivity, "点击了首页的拜访按钮", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.fragment_sort_train:
//                Toast.makeText(mainActivity, "点击了首页的培训按钮", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.home_fragment_info_more:
//                Toast.makeText(mainActivity, "点击了资讯的更多按钮", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.home_fragment_task_more:
//                Toast.makeText(mainActivity, "点击了任务的更多按钮", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
    }

    class MViewpager extends PagerAdapter implements ViewPager.OnPageChangeListener {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        /**
         * viewpager滑动监听，动态改变指示下标的选中状态
         *
         * @param position
         * @param positionOffset
         * @param positionOffsetPixels
         */

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                ImageView imageView = (ImageView) layout.getChildAt(i);
                if (i == position) {
                    imageView.setSelected(true);
                } else {
                    imageView.setSelected(false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.Scroll://接受滚动消息，并执行
                    vp.setCurrentItem(count);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 创建图片变换下标
     */
    public void initPoint() {
        //清除所有指示器下标
        layout.removeAllViews();
        for (int i = 0; i < views.size(); i++) {
            ImageView img = new ImageView(mActivity);
            //添加下标圆点参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = 5;
            params.rightMargin = 5;
            img.setLayoutParams(params);
            img.setImageResource(R.drawable.sns_v2_page_point);
            if (i == 0) {
                img.setSelected(true);
            }
            layout.addView(img);
        }
    }

    //创建计时器发送图片轮播消息
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(Constant.Scroll);
            if (count == views.size()) {
                count = 0;
            } else {
                count = count + 1;
            }
            mHandler.sendEmptyMessage(Constant.Scroll);
        }
    };
}
