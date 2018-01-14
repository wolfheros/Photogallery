package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by james_huker on 12/1/17.
 * 此类的作用就是专门处理网络请求的应用使用。
 */

public class FlickFetchr {

    private static final String TAG = "FlickrFetchr";
    // Flickr api_key
    private static final String API_KEY = "d83bc11baca3cb65091e08494b1e2c26";

    // 建立连接，并从URL中读取数据将其储存在数组中
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        //  创建一个指向URL连接对象。并强制转化成httpURLConnectionn
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        // connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        try {
            // 创建一个字节数组输出流对象
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 从打开的连接中读取数据流
            InputStream in = connection.getInputStream();
            // 如果返回数据不等于，HTTP_OK,抛出异常。
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with" +urlSpec);
            }

            int byteRead = 0;
            byte[] buffer = new byte[1024];
            // in.read(buffer) 返回读取的数据数量，-1 代表没有读取任何数据
            while ((byteRead = in.read(buffer)) > 0) {
                out.write(buffer,0,byteRead);
            }

            out.close();
            // 将获取的数据拷贝到Byte数组中
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 此方法是用来从网络获取数据，获取的数据是JSON 实例的形式。此外，此方法还调用了
     * parseItems(List<GalleryItems> , JSONObject) 方法，进行解析。并返回一个结合。
     * */

    public List<GalleryItem> fetchItems(){
        // 返回一个GalleryItem的list

        List<GalleryItem> items = new ArrayList<>();
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    // builderUpon 方法返回的是一个builder实例。
                    .buildUpon()
                    // 以下方法builder实例调用
                    .appendQueryParameter("method","flickr.photos.getRecent")
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback" ,"1")
                    .appendQueryParameter("extras" ,"url_sq")
                    .appendQueryParameter("page", "2")
                    //.appendQueryParameter("extras" ,"url_s")
                    // 合并成一个URl
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG , "Received JSON :" + jsonString);
            // 进行数据解析 ,解成json
            JSONObject jsonObject = new JSONObject(jsonString);

            // 获取的JsonObject进一步的解析，调用的是下面的parseItems() 方法。
            parseItems(items , jsonObject);
        } catch (JSONException je) {
            Log.e(TAG , "Failed to parse JSON" , je);
        } catch (IOException e) {
            Log.e(TAG , "Filed to fetch items" , e);
        }

        return items;
    }

    /**
     * 解析JSONObject 实例并获取其中数据的方法，将内部包含的photo JSONObject提取出来成为独立的对象，并储存在一个集合中
     * */

    private void parseItems(List<GalleryItem> items , JSONObject jsonBody) throws IOException , JSONException{

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray jsonArray = photosJsonObject.getJSONArray("photo");
        // 转换成jsonArray。
        String jsonToString = jsonArray.toString();
        Gson gson = new Gson();
        // 获取Collection 中的type类型。
        Type collectionType = new TypeToken<Collection<GalleryItem>>(){}.getType();
        //
        List<GalleryItem> galleryItem = gson.fromJson(jsonToString , collectionType);

        /*JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");*/

        for (int i = 0 ; i < galleryItem.size(); i++) {
            GalleryItem item = new GalleryItem();
            // System.out.println("这是打印item对象" +galleryItem2.get(i).toString());
            item.setId(galleryItem.get(i).getId());
            item.setTitle(galleryItem.get(i).getTitle());
            item.setUrl_sq(galleryItem.get(i).getUrl_sq());
            // 将获取到的GalleryItem 对象存入集合中。
            items.add(item);
        }
    }
}
