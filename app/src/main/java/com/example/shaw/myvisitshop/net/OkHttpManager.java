package com.example.shaw.myvisitshop.net;

/**
 * Created by Shaw on 2017/7/21.
 */

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求管理，对okHttp进行封装
 */
public class OkHttpManager {
    private static OkHttpManager instance;
    private OkHttpClient mOkHttpClient;
    private Handler okHandler;

    private OkHttpManager() {
        //声明Handler对象，指定线程为主线程，确保执行方法在主线程中
        okHandler = new Handler(Looper.getMainLooper());

        //指定超时时间等参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    /**
     * 获取当前实例对象，确保唯一
     */
    public static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        }
        return instance;
    }

    /**
     * get请求
     */
    public void getNet(String url, ResultCallback resultCallback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        dealNet(request, resultCallback);
    }

    /**
     * post请求
     */
    public void postNet(String url, ResultCallback resultCallback, Param... params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder formbody = new FormBody.Builder();
        for (Param p : params) {
            formbody.add(p.key, p.value);
        }
        RequestBody requestBody = formbody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody) //传入构建好的参数
                .build();
        dealNet(request, resultCallback);
    }

    public void upFileNet(String url, ResultCallback resultCallback, File[] files,
                          String fileKeys,Param... param){
        Request request=bulidMultipartFormRequest(url,files,fileKeys,param);

        dealNet(request,resultCallback);
    }

    private void dealNet(final Request request, final ResultCallback resultCallback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //请求失败时执行的操作
                        resultCallback.onFailed(request, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String str = "";
                str = response.body().string();
                final String finalStr = str;
                Log.i("OkHttpManager", "onResponse: " + finalStr);
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //请求成功时执行的操作
                        resultCallback.onSuccessed(finalStr);

                    }
                });
            }
        });
    }

    private static final MediaType MEDIA_TYPE_PNG=MediaType.parse("image/png");

    private Request bulidMultipartFormRequest(String url,File[] files,
                                              String fileKeys,Param[] params){
        params=validateParam(params);

        MultipartBody.Builder multipartBodyBuilder=new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        for (Param param: params){
            multipartBodyBuilder.addFormDataPart(param.key,param.value);
        }
        if (files!=null){
            for (int i=0;i<files.length;i++){
                File file=files[i];
                multipartBodyBuilder.addFormDataPart(fileKeys,file.getName(),RequestBody.create(MEDIA_TYPE_PNG,file));
            }
        }

        RequestBody requestBody=multipartBodyBuilder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private Param[] validateParam(Param[] params){
        if (params==null){
            return new Param[0];
        }else {
            return params;
        }
    }


    /**
     * 监听回调接口
     */
    public static abstract class ResultCallback {
        public abstract void onFailed(Request request, Exception e);

        public abstract void onSuccessed(String response);
    }

    /**
     * 参考封装类
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }


        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
