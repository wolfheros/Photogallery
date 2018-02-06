package com.bignerdranch.android.photogallery;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james_huker on 12/1/17.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    // 变量mItems 会在AsyncTask 中传入新的对象
    private List<GalleryItem> mItems = new ArrayList<>();
    // 创建的后台looper主线程用来下载的实例
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;


    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  设置是否在设备状态变化的时候保存Fragment视图
       setRetainInstance(true);
       // 这里调用后台线程的方法，可以使 mItems List<GalleryItem> 对象不为空。
       new FetchItemsTask().execute();

       // 创建一个ThumbnailDownloader 用于后台下载的进程。
        mThumbnailDownloader = new ThumbnailDownloader<>();
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container,false);

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        setupAdapter();

        return view;
    }

    // 设置Adapter 过程中判断当前fragment 是否和 activity 依然绑定。如果不绑定是无法获得TextView视图继而
    // 创建 PhotoHolder 视图的。
    private void setupAdapter() {
        if (isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG , "Background thread Destroy");
    }

    // ViewHolder 视图
    private class PhotoHolder extends RecyclerView.ViewHolder {
        //private TextView mTitleTextView;
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            // mTitleTextView =(TextView) itemView;
            mItemImageView = (ImageView) itemView
                    .findViewById(R.id.fragment_photo_garllery_image_view);
        }

//        public void bindGalleryItem(GalleryItem item) {
//            // 重写后的toString() 方法。
//            mTitleTextView.setText(item.toString());
//        }
        // 在这个ImageView 上设置drawable对象。
        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    // 绑定的展示类
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        // 创建PhotoHolder
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup ,int viewType) {
            // 创建一个textView视图，然后传给P后头 Holder作为视图。
//            TextView textView = new TextView(getActivity());
//            return new PhotoHolder(textView);
            // 替换TextView 成 immageView

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item , viewGroup ,false);

            return new PhotoHolder(view);
        }
        // 在这里进行绑定
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder , int position) {
            // 触发图片下载的的确应该放在这里进行。当显示到指定位置时，就更新下载视图
            GalleryItem galleryItem = mGalleryItems.get(position);
            Log.i(TAG ,"当前位置为" + position);
            //photoHolder.bindGalleryItem(galleryItem);
            // 绑定imageView 视图
            Drawable placeholder = getResources().getDrawable(R.drawable.james_huker);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder , galleryItem.getUrl_sq());
        }

        @Override
        public int getItemCount(){
            return mGalleryItems.size();
        }

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            // 获取返回一个GalleryItems 集合。
            return new FlickFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            // 替换创建的空白集合GalleryItems
            mItems = items;
            // 在进行一次RecycleView 视图绑定。
            setupAdapter();
        }
    }

}
