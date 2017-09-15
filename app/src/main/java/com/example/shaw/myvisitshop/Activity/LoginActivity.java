package com.example.shaw.myvisitshop.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shaw.myvisitshop.MainActivity;
import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.bean.LoginBeanResult;
import com.example.shaw.myvisitshop.bean.User;
import com.example.shaw.myvisitshop.net.OkHttpManager;
import com.example.shaw.myvisitshop.utils.Constant;
import com.example.shaw.myvisitshop.utils.SharePreUtil;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtPassword;
    private TextInputLayout mEtName_design;
    private TextInputLayout mEtPassword_design;
    private Button mBtnLogin;
    private String mUserName;
    private String mUserPassword;
    private RelativeLayout mRelLoading;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        LitePal.initialize(mContext);
        Stetho.initializeWithDefaults(this);
//        SharePreUtil.SetShareString(mContext, "userid", "num01");
//        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        finish();
        if (checkLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            //将状态栏透明化
            initStatusBarColor();
            //绑定控件
            bindView();
            //初始化监听事件
            initListener();
        }
    }

    private boolean checkLogin() {
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            //数据库表不为空，已经登录，返回true
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void bindView() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtName_design = (TextInputLayout) findViewById(R.id.et_name_design);
        mEtPassword_design = (TextInputLayout) findViewById(R.id.et_password_design);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //将TextInput的错误信息提示隐藏
                mEtName_design.setErrorEnabled(false);
            }
        });
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mEtPassword_design.setErrorEnabled(false);
            }
        });
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /**
     * 登录操作
     */
    private void login() {
        //检查数据是否合法
        if (checkData()) {
            //显示登录加载界面
            mRelLoading.setVisibility(View.VISIBLE);
            //发送登录请求，进行网络验证(Post方式)
            OkHttpManager.getInstance().postNet(Constant.Login, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, Exception e) {
                    //登录失败，去掉加载界面，提示错误信息
                    mRelLoading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "服务器连接异常，登录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccessed(String response) {
                    //解析服务端返回的数据为java对象
                    LoginBeanResult loginBeanResult = getDataFromJson(response);
                    mRelLoading.setVisibility(View.GONE);
                    if (loginBeanResult.getCode() == 0) {
                        //用户存在且密码正确，登录成功，先保存登录信息，然后跳转到主界面
                        //文件保存
                        SharePreUtil.SetShareString(mContext, "userid",
                                loginBeanResult.getBody().getUserid());
                        //数据库保存,先删除数据库
                        DataSupport.deleteAll(User.class);
                        User user = new User();
                        user.setUserId(loginBeanResult.getBody().getUserid());
                        user.setNickName(loginBeanResult.getBody().getNickname());
                        user.setSex(loginBeanResult.getBody().getSex());
                        user.setJob(loginBeanResult.getBody().getJob());
                        user.setArea(loginBeanResult.getBody().getArea());
                        user.setPhoneNum(loginBeanResult.getBody().getPhonenum());
                        user.setImg(loginBeanResult.getBody().getImg());
                        user.save();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "用户名或密码错误，登录失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }, new OkHttpManager.Param("userid", mUserName), new OkHttpManager.Param("password", mUserPassword));
        }
    }

    /**
     * 检查输入数据是否合法
     */
    private boolean checkData() {
        //trim作用是去掉前后空格
        mUserName = mEtName.getText().toString().trim();
        mUserPassword = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName)) {
            mEtName_design.setError("用户名不能为空");
            return false;
        }
        if (mUserName.length() < 0 || mUserName.length() > 6) {
            mEtName_design.setError("请输入6位数以内的用户名");
            return false;
        }
        if (TextUtils.isEmpty(mUserPassword)) {
            mEtPassword_design.setError("密码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 初始化状态栏，使其和背景色保持一致
     */
    private void initStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 返回数据组装
     */
    private LoginBeanResult getDataFromJson(String strResult) {
        Gson gson = new Gson();
        LoginBeanResult loginBeanResult = gson.fromJson(strResult, LoginBeanResult.class);
        return loginBeanResult;
    }

}
