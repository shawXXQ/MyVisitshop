package com.example.shaw.myvisitshop.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.adapters.SelectShopAdapter;
import com.example.shaw.myvisitshop.bean.SelectShop;
import com.example.shaw.myvisitshop.bean.ShopList;
import com.example.shaw.myvisitshop.bean.SubmitResult;
import com.example.shaw.myvisitshop.fragment.ShopFragment;
import com.example.shaw.myvisitshop.net.OkHttpManager;
import com.example.shaw.myvisitshop.utils.CacheFileUtils;
import com.example.shaw.myvisitshop.utils.Constant;
import com.example.shaw.myvisitshop.utils.ImageTools;
import com.example.shaw.myvisitshop.utils.LogUtils;
import com.example.shaw.myvisitshop.utils.SharePreUtil;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class CreateVisitShopActivity extends BaseFragmentActivity implements View.OnClickListener, TextView.OnEditorActionListener, BDLocationListener {
    //布局组件
    private LinearLayout layout_hide;
    private RecyclerView search_lv;
    private RatingBar rb_shop, rb_person, rb_product;
    private TextView tv_save, tv_submit, tv_shopId, tv_shopWhere;
    private EditText et_suggest, et_shopName;
    private RelativeLayout r1_shopId, r1_shopWhere, progress;
    private LinearLayout gallery;
    //变量值
    private float shop_number, person_number, product_number;
    private String shop_name, str_suggest, shop_id, shop_where;
    private String filePath;
    //集合
    private List<SelectShop.ShopLists> list_shop;
    public static ArrayList<String> filePaths;

    private ShopList s1;
    private String useid;
    private Boolean IsShop;//是否是搜索店铺界面

    //百度地图定位方法类
    public LocationClient mLocationClient = null;
    private ImageView mlvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_visit_shop);
        setTitleName("新增巡店");
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        //初始化定位设置
        initLocation();
        //启动定位
        mLocationClient.start();
        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (filePaths != null) {
            //刷新图片，先清空然后重新添加图片
            gallery.removeAllViews();
            gallery.setVisibility(View.VISIBLE);
            //根据保存的图片地址添加图片
            for (int i = 0; i <= filePaths.size(); i++) {
                //创建一个ImageView
                ImageView imageView = new ImageView(mContext);
                //设置imageView的缩放类型
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(220, 220);
                layout.leftMargin = 10;
                layout.rightMargin = 10;
                imageView.setLayoutParams(layout);
                //设置图片
                if (i < filePaths.size()) {
                    //未到最后一张图片
                    File file = new File(filePaths.get(i));
                    if (file.exists()) {
                        //如果文件存在，则显示保存文件
                        Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));
                        imageView.setImageBitmap(bitmap);
                    } else {
                        //如果文件丢失，则显示默认图片
                        imageView.setImageResource(R.drawable.default_img);
                    }
                } else {
                    //最后一张显示添加图标
                    imageView.setImageResource(R.drawable.btn_add_img);
                }
                //按顺序添加
                gallery.addView(imageView, i);
                imgClickListener(imageView, i);
            }
        }
    }

    /**
     * 加载数据
     */
    private void initData() {
        useid = SharePreUtil.GetShareString(mContext, "userid");
        if (filePaths == null) {
            filePaths = new ArrayList<>();
        }
        s1 = (ShopList) getIntent().getSerializableExtra("info");
        if (s1 != null) {
            if (!"".equals(s1.getShopid())) {
                tv_shopId.setText(s1.getShopid());
                r1_shopId.setVisibility(View.VISIBLE);
            }
            if (!"".equals(s1.getName())) {
                et_shopName.setText(s1.getName());
            }
            if (!"".equals(s1.getShoplocation())) {
                tv_shopWhere.setText(s1.getShoplocation());
            }
            if (!"".equals(s1.getShoplevel())) {
                try {
                    String[] strCode = s1.getShoplevel().split(";");
                    rb_shop.setRating(Float.parseFloat(strCode[0]));
                    rb_person.setRating(Float.parseFloat(strCode[1]));
                    rb_product.setRating(Float.parseFloat(strCode[2]));
                } catch (Exception e) {
                    LogUtils.i("tag", "拆分字符串异常，请查看后台数据");
                }
            }
            if (!"".equals(s1.getFeedback())) {
                et_suggest.setText(s1.getFeedback());
            }
            if (!"".equals(s1.getImgname())) {
                try {
                    String[] imgs = s1.getImgname().split(";");
                    filePaths.clear();
                    for (int i = 0; i < imgs.length; i++) {
                        filePaths.add(imgs[i]);
                    }
                } catch (Exception e) {
                    LogUtils.i("tag", "拆分字符串异常，请查看后台数据");
                }
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        progress = (RelativeLayout) this.findViewById(R.id.activity_createvisit_progress);
        layout_hide = (LinearLayout) this.findViewById(R.id.activity_createvisit_hide);
        search_lv = (RecyclerView) this.findViewById(R.id.activity_createvisit_list);
        rb_person = (RatingBar) this.findViewById(R.id.activity_createvisit_score_person);
        rb_product = (RatingBar) this.findViewById(R.id.activity_createvisit_score_product);
        rb_shop = (RatingBar) this.findViewById(R.id.activity_createvisit_score_shop);
        tv_save = (TextView) this.findViewById(R.id.activity_createvisit_save);
        tv_submit = (TextView) this.findViewById(R.id.activity_createvisit_submit);
        tv_shopId = (TextView) this.findViewById(R.id.activity_createvisit_shopid);
        tv_shopWhere = (TextView) this.findViewById(R.id.activity_createvisit_shopwhere);
        et_suggest = (EditText) this.findViewById(R.id.activity_createvisit_et);
        et_shopName = (EditText) this.findViewById(R.id.activity_createvisit_shopname);
        r1_shopId = (RelativeLayout) this.findViewById(R.id.activity_createvisit_shopid_rl);
        r1_shopWhere = (RelativeLayout) this.findViewById(R.id.activity_createvisit_shopwhere_rl);
        gallery = (LinearLayout) this.findViewById(R.id.activity_createvisit_gallery);

        //监听事件
        viewEvent();
    }

    private void viewEvent() {
        tv_submit.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_shopWhere.setOnClickListener(this);
        et_shopName.setOnEditorActionListener(this);
        et_shopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (s1 != null) {
                    s1.setSelect(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rb_shop.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                shop_number = v;
            }
        });
        rb_person.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                shop_number = v;
            }
        });
        rb_product.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                shop_number = v;
            }
        });
        search_lv.setLayoutManager(new LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL, true
        ));
        search_lv.setNestedScrollingEnabled(false);
    }

    /**
     * 图片点击查看事件
     *
     * @param imageView
     * @param position
     */
    private void imgClickListener(View imageView, final int position) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePaths == null) {
                    filePaths = new ArrayList<String>();
                }
                //点击最后一张照片的时候处理拍照
                if (position == filePaths.size()) {
                    if (filePaths.size() < 4) {
                        goTakePhoto();
                    } else {
                        Toast.makeText(mContext, "最多拍摄四张照片", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //点击其他图片跳转到查看页面
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putExtra("type", Constant.ShopImgUp);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 调用手机拍照功能
     */
    private void goTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        filePath = CacheFileUtils.getUpLoadPhotosPath();
        Uri uri = Uri.fromFile(new File(filePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        startActivityForResult(intent, 1001);
    }

    /**
     * 选择店铺后更新界面
     */
    public void selectShop(SelectShop.ShopLists ss) {
        tv_shopId.setText(ss.getId());
        et_shopName.setText(ss.getName());
        r1_shopWhere.setVisibility(View.VISIBLE);
        r1_shopId.setVisibility(View.VISIBLE);
        layout_hide.setVisibility(View.VISIBLE);
        search_lv.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        //获取填写数据
        shop_name = et_shopName.getText().toString().trim();
        shop_id = tv_shopId.getText().toString().trim();
        shop_where = tv_shopWhere.getText().toString().trim();
        str_suggest = et_suggest.getText().toString().trim();
        shop_number = rb_shop.getRating();
        person_number = rb_person.getRating();
        product_number = rb_product.getRating();
        switch (view.getId()) {
            case R.id.activity_createvisit_submit://新增巡店提交
//                Toast.makeText(mContext, "新增巡店提交", Toast.LENGTH_SHORT).show();
                IsShop = false;
                //初步判断
                if ("".equals(shop_name) || shop_name.equals(R.string.activity_createvisit_shopname_hint)) {
                    Toast.makeText(mContext, "请对店面进行搜索添加", Toast.LENGTH_SHORT).show();
                } else if ("".equals(shop_id)) {
                    Toast.makeText(mContext, "请对店面进行搜索添加", Toast.LENGTH_SHORT).show();
                }
                //模拟器无法定位
//                  else if ("".equals(shop_where)||"null".equals(shop_where)||shop_where.equals(R.string.activity_createvisit_shopname_hint)){
//                    Toast.makeText(mContext, "店面位置为空，请进行定位", Toast.LENGTH_SHORT).show();
//                }
                else if (filePaths.size() < 2 || filePaths.size() > 4) {
                    Toast.makeText(mContext, "请为店面拍摄2-4张照片", Toast.LENGTH_SHORT).show();
                } else if (shop_number < 1 || person_number < 1 || product_number < 1) {
                    Toast.makeText(mContext, "请为店面打分，最低为1分", Toast.LENGTH_SHORT).show();
                } else if ("".equals(str_suggest)) {
                    Toast.makeText(mContext, "请为店面点评", Toast.LENGTH_SHORT).show();
                } else {
                    if (s1 != null) {
                        IsShop = s1.getSelect();
                    }
                    if (list_shop != null) {
                        for (int i = 0; i < list_shop.size(); i++) {
                            if (list_shop.get(i).getName().equals(shop_name)) {
                                IsShop = true;
                                break;
                            }
                        }
                    }
                    if (IsShop) {
                        et_shopName.setFocusableInTouchMode(false);
                        tv_save.setClickable(false);
                        tv_submit.setClickable(false);
                        progress.setVisibility(View.VISIBLE);
                        //店面数据提交请求封装
                        OkHttpManager.Param[] params = new OkHttpManager.Param[]{
                                new OkHttpManager.Param("userid", useid),
                                new OkHttpManager.Param("shopid", shop_id),
                                new OkHttpManager.Param("shopname", shop_name),
                                new OkHttpManager.Param("feedback", str_suggest),
                                new OkHttpManager.Param("shoplevel", (int) shop_number + ";" + (int) person_number + ";" + (int) product_number),
                                new OkHttpManager.Param("shopaddress", shop_where)
                        };
                        File[] files = new File[filePaths.size()];
                        for (int i = 0; i < filePaths.size(); i++) {
                            String sName = filePaths.get(i);
                            File imgFile = new File(sName);
                            files[i] = imgFile;
                        }
                        OkHttpManager.getInstance().upFileNet(Constant.VisitShopSubmit,
                                new OkHttpManager.ResultCallback() {
                                    @Override
                                    public void onFailed(Request request, Exception e) {
                                        SaveDataInDb();
                                        tv_submit.setClickable(true);
                                        tv_save.setClickable(true);
                                        et_shopName.setFocusableInTouchMode(true);
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(mContext,R.string.http_failed, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccessed(String response) {
                                        tv_submit.setClickable(true);
                                        et_shopName.setFocusableInTouchMode(true);
                                        tv_save.setClickable(true);
                                        progress.setVisibility(View.GONE);
                                        Gson gsr=new Gson();
                                        SubmitResult sr=gsr.fromJson(response,SubmitResult.class);
                                        if (sr.getCode()==0){
                                            if (s1!=null){
                                                DataSupport.delete(ShopList.class,s1.getId());
                                            }
                                            ShopFragment.isFirst=true;
                                            mActivity.setResult(RESULT_OK);
                                            finish();
                                        }else {
                                            SaveDataInDb();
                                            Toast.makeText(mContext,R.string.please_resubmit, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },files,"file",params);
                    }else {
                        Toast.makeText(mContext, "请对店面进行搜索并添加", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.activity_createvisit_save://新增巡店临时保存
                Toast.makeText(mContext, "新增巡店临时保存", Toast.LENGTH_SHORT).show();
                break;
            case R.id.shopdetail_activity_location://启动定位按钮
                if (mLocationClient != null) {
                    if (!mLocationClient.isStarted()) {
                        mLocationClient.start();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            //检验接收数据
            return;
        }
        switch (requestCode) {
            case 1001://接收拍照返回的数据
                if (!TextUtils.isEmpty(filePath)) {
                    Bitmap bitmap = ImageTools.convertToBitmap(filePath, 640, 640);
                    Bitmap bitmapComp = ImageTools.comp(bitmap);//图片压缩
                    ImageTools.saveBitmap(bitmapComp, filePath);
                    if (bitmap != null) {
                        filePaths.add(filePath);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        /**
         * 当点击搜索按钮时执行
         */
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard();
            shop_name = et_shopName.getText().toString().trim();
            if (shop_name.equals("")) {
                Toast.makeText(mContext, "请输入店名", Toast.LENGTH_SHORT).show();
            } else {
                progress.setVisibility(View.VISIBLE);
                if (list_shop == null) {
                    list_shop = new ArrayList<>();
                }
                list_shop.clear();
                //店面查询请求
                OkHttpManager.getInstance().getNet(Constant.ShopSelect + "?keyword=" + shop_name, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, Exception e) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, R.string.http_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccessed(String response) {
                        progress.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        if (!"".equals(response) && response != null) {
                            SelectShop ss = gson.fromJson(response, SelectShop.class);
                            if (ss != null) {
                                list_shop = ss.getShoplists();
                                if (list_shop == null) {
                                    list_shop = new ArrayList<SelectShop.ShopLists>();
                                }
                                if (list_shop.size() > 0) {
                                    SelectShopAdapter adapter = new SelectShopAdapter(mContext, list_shop, shop_name);
                                    search_lv.setAdapter(adapter);
                                    r1_shopId.setVisibility(View.GONE);
                                    r1_shopWhere.setVisibility(View.GONE);
                                    search_lv.setVisibility(View.VISIBLE);
                                    layout_hide.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(mContext, "未搜索到相关店铺", Toast.LENGTH_SHORT).show();
                                    shop_id = "";
                                    tv_shopId.setText("");
                                    r1_shopId.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
        }
        return false;
    }

    public void hideKeyboard() {
        //隐藏软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //接收百度定位结果
        StringBuffer sb = new StringBuffer(256);
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
            //gps定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "gps定位成功");
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            //网络定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "网络定位成功");
        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
            //离线定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "离线定位成功，离线定位的结果也是有效的");
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            LogUtils.i("bdLocation", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            LogUtils.i("bdLocation", "网络不同导致定位失败，请检查网络是否通畅");
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            LogUtils.i("bdLocation", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        } else {
            LogUtils.i("bdLocation", "返回信息码" + bdLocation.getLocType());
        }
        tv_shopWhere.setText(sb.toString());
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    /**
     * 存储数据到数据库
     */
    private void SaveDataInDb(){
        IsShop=false;
        ShopList dl=new ShopList();
        dl.setFeedback(str_suggest);
        StringBuffer sb=new StringBuffer();
        for (int i=0;i<filePaths.size();i++){
            if (i!=0){
                sb.append(";");
            }
            sb.append(filePaths.get(i));
        }
        dl.setUserid(useid);
        dl.setImgname(sb.toString());
        dl.setShopid(shop_id);
        dl.setShoplocation(shop_where);
        dl.setName(shop_name);
        dl.setShoplevel((int)shop_number+";"+(int)person_number+";"+(int)product_number);
        if (list_shop==null){
            list_shop=new ArrayList<>();
        }
        if (s1!=null){
            IsShop=s1.getSelect();
        }
        if (list_shop!=null){
            for (int i=0;i<list_shop.size();i++){
                if (list_shop.get(i).getName().equals(shop_name)){
                    IsShop=false;
                    break;
                }
            }
        }
        dl.setSelect(IsShop);
        dl.save();
        if (s1!=null){
            DataSupport.delete(ShopList.class,s1.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空静态变量
        filePaths = null;
        //关闭地图定位
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }
}
