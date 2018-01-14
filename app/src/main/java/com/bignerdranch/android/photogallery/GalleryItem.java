package com.bignerdranch.android.photogallery;

/**
 * 模型数据储存类，用来获取从网络获得json对象
 * Created by james_huker on 12/26/17.
 */

public class GalleryItem {
    private String mId;
    private String mUrl;
    private String title;
    private String url_sq;

    public String getUrl_sq() {
        return url_sq;
    }

    public void setUrl_sq(String url_sq) {
        this.url_sq = url_sq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
