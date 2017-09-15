package com.example.shaw.myvisitshop.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.shaw.myvisitshop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadUtils {
    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;

    public static void DownLoad(String url, final String path, final Context context) {
        //获取通知栏管理器
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        //通知栏参数设置，标题，正文，图标等
        mBuilder.setContentTitle("版本更新")
                .setContentText("正在下载...")
                .setContentInfo("0%")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.xiazai);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                int current = len;
                long total = 0;
                try {
                    is = response.body().byteStream();
                    total = response.body().contentLength();
                    File file = new File(path);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        //计算下载进度，将当前下载的数据大小和总大小计算，得到进度百分比，并在通知栏更新显示
                        BigDecimal b = new BigDecimal((float) current / (float) total);
                        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        mBuilder.setProgress(100, (int) (f1 * 100), false);
                        mBuilder.setContentInfo((int) (f1 * 100) + "%");
                        mNotifyManager.notify(1, mBuilder.build());
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    installApp(context, path);
                } catch (IOException e) {
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    /**
     * 安装指定APK文件
     *
     * @param context
     * @param filePath
     */
    public static void installApp(Context context, String filePath) {
        File _file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(_file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
