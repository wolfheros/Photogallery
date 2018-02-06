package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 这个类的主要作用就是创建一个，在后台的looper线程用于下载网上数据。
 * Created by james on 1/26/18.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG= "ThumbnailDownloader";
    // Message 实例中的变量what 的值
    private static final int MESSAGE_DOWNLOAD = 0;
    private Boolean mHasQuit = false;
    private Handler mRequestHandler;
    // 创建一个HashMap 的集合，用来储存对象-URL键值对。
    private ConcurrentMap<T , String> mRequestMap = new ConcurrentHashMap<>();

    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG , "Got a request for URL" + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target ,String url) {
        Log.i(TAG , "Got a URL :" + url);

        // 判断是否正确获取到url。
        if (url == null) {
            mRequestMap.remove(target);
        } else{
            mRequestMap.put(target , url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD , target).sendToTarget();
        }
    }

    private void handleRequest (final T target) {
        try {
            final String url = mRequestMap.get(target);

            if (url == null) {
                return;
            }
            // 同样利用的是FlickFetchr 类中的方法建立网络链接。返回读取的数据。
            byte[] bitmapBytes = new FlickFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes , 0,bitmapBytes.length);

            Log.i(TAG , "Bitmap created");
        } catch (IOException ioe) {
            Log.e(TAG , " Error downloading image" , ioe);
        }
    }
}
