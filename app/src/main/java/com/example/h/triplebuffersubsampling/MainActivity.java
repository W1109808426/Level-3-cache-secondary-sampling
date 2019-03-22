package com.example.h.triplebuffersubsampling;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.h.triplebuffersubsampling.glide.GlideActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String url = "http://upload-images.jianshu.io/upload_images/1753433-9068356a51b5f777.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/198/format/webp";
    private ImageView imageView;
    private LruCache<String, Bitmap> lruCache;
    private Button btn_Jump;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                imageView.setImageBitmap(bitmap);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_Show);
        btn_Jump = (Button) findViewById(R.id.btn_Jump);
        //跳转
        btn_Jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,GlideActivity.class));
            }
        });
        //获取运行时内存
        Long MaxSize = Runtime.getRuntime().maxMemory();

        //初始化LruCache
        lruCache = new LruCache<String, Bitmap>((int) (MaxSize / 8)) {
            //key 从内存中取出或存入的对象的名字
            //value 存取的对象
            //return 返回对象的缓存
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    //点击获取图片按钮
    public void Click(View view) {
        //当用户需要图片是先到缓存中去取
        Bitmap bitmap = lruCache.get(url);
        if (bitmap!= null){
            Log.e("TAG","在缓存中获取");
            imageView.setImageBitmap(bitmap);
        }else {
            //在文件中查找

            Bitmap bitmap1 = getBitmapFromSDCard(getName(url));
            if (bitmap1!= null){
                Log.e("TAG","在文件中获取");
                imageView.setImageBitmap(bitmap1);
                //如果图片在SD卡中找到，将图片放入内存缓存
                Log.e("TAG","文件中存在放入缓存");
                lruCache.put(getName(url),bitmap1);
            }else {

                getBitmapFromNet();
            }
        }
    }

    //从网络获取
    private void getBitmapFromNet() {
        Log.e("TAG","在网络中获取");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL path = new URL(url);
                    HttpURLConnection httpURLConn = (HttpURLConnection) path.openConnection();
                    if (httpURLConn.getResponseCode() == 200){
                        final Bitmap bitmap = BitmapFactory.decodeStream(httpURLConn.getInputStream());
                        //将图片放入缓存中
                        lruCache.put(getName(url),bitmap);
                        //将bitmap存入当前文件中
                        SaveBitmapToSDCard(getName(url),bitmap);
                        Message message = Message.obtain();
                        message.obj=1;
                        message.obj=bitmap;


                        handler.sendMessage(message);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //将Bitmap存入sd卡中
    private void SaveBitmapToSDCard(String name, Bitmap bitmap) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File(getExternalCacheDir(),name)));
            if (name.endsWith("png")||name.endsWith("PNG")){
                //把bitmap存入SdCard中
                Log.e("TAG","把bitmap存入SdCard中");
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
            }else {
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    //从sd卡中获取图片
    private Bitmap getBitmapFromSDCard(String name) {
        //  Log.e("TAG","从SD卡中获取图片");
        //如果需要原图就可以不调用二次采用方法
        //   Bitmap bitmap = BitmapFactory.decodeFile(getExternalCacheDir().getAbsolutePath()+ File.separator+name);
        //在SD卡中取出的Bitmap进行二次采样，传出去后会将二次采样好的图片放入缓存中
        Bitmap bitmap = BitmapUtils.getInstance().getBitmap(getExternalCacheDir().getAbsolutePath()+File.separator+name,100,100);
        return  bitmap;
    }

    //获取Bitmap为Url的名字
    public String getName(String url){
        String name = url.substring(url.lastIndexOf("/")+1,url.length());
        return name;
    }
}
