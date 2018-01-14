package com.bignerdranch.android.photogallery;

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
import android.widget.TextView;

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

            mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                // 重写的两个方法
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                })
            });
        }
    }

    // ViewHolder 视图
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView =(TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            // 重写后的toString() 方法。
            mTitleTextView.setText(item.toString());
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
            // 创建一个textview 视图，然后传给P后头 Holder作为视图。
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }
        // 在这里进行绑定
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder , int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Log.i(TAG ,"当前位置为" + position);
           /* if (position = 99) {

            }*/
            photoHolder.bindGalleryItem(galleryItem);
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
