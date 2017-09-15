package com.example.shaw.myvisitshop.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.fragment.BaseFragment;
import com.example.shaw.myvisitshop.utils.Constant;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewActivity extends BaseFragmentActivity implements View.OnClickListener {
    private ImageView right, left;
    private PhotoView img;
    private Button delete;
    private List<String> list;
    private String[] imgs;
    private int mposition;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        setTitleName("图片查看");
        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mposition = (Integer)getIntent().getIntExtra("position", -1);
        type =(Integer) getIntent().getIntExtra("type", -1);
        showImage(mposition, type);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        img = (PhotoView) findViewById(R.id.image_view_img);
        right = (ImageView) findViewById(R.id.image_view_right);
        left = (ImageView) findViewById(R.id.image_view_left);
        delete = (Button) findViewById(R.id.image_view_delete);
        right.setOnClickListener(this);
        left.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_left://浏览上一张图片
                if (mposition > 0) {
                    mposition--;
                    showImage(mposition, type);
                }
                break;
            case R.id.image_view_right://浏览下一张图片
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (mposition < list.size() - 1 && mposition > -1) {
                    mposition++;
                    showImage(mposition, type);
                }
                break;
            case R.id.image_view_delete://删除图片
                if (list != null) {
                    if (list.size() > 0) {
                        File file = new File(list.get(mposition));
                        switch (type) {
                            case Constant.PhotoUp:
                                //培训图片处理
                                break;
                            case Constant.ShopImgUp:
                                CreateVisitShopActivity.filePaths.remove(mposition);
                                break;
                        }
                        if (file.exists()) {
                            file.delete();
                        }
                        if (list.size() == 0) {
                            finish();
                        }
                        if (mposition == list.size()) {
                            mposition--;
                            showImage(mposition, type);
                        } else {
                            showImage(mposition, type);
                        }
                    }
                }
                break;
        }
    }

    public void showImage(int position, int type) {
        if (type > 0 && position >= 0) {
            switch (type) {
                case Constant.ShopImgLook:
                    //巡店完成查看图片详情
                    break;
                case Constant.PhotoLook:
                    //培训图片查看
                    break;
                case Constant.PhotoUp:
                    //培训拍照上传查看图片
                    break;
                case Constant.ShopImgUp:
                    //巡店图片上传查看
                    list = CreateVisitShopActivity.filePaths;
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    //如果是最后一张图片则设置右侧图标不可用
                    if (position == list.size() - 1) {
                        right.setSelected(true);
                    } else {
                        right.setSelected(false);
                    }
                    //如果是第一张图片则设置左侧图标不可用
                    if (position == 0) {
                        left.setSelected(true);
                    } else {
                        left.setSelected(false);
                    }
                    String shopImg = list.get(position);
                    File shopFile = new File(shopImg);
                    if (shopFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(shopImg);
                        img.setImageBitmap(bitmap);
                    } else {
                        list.remove(position);
                        Toast.makeText(mContext, R.string.image_no_exist, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else {
            img.setImageResource(R.drawable.default_img);
        }
    }
}
