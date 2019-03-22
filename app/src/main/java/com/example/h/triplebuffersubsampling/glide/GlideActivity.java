package com.example.h.triplebuffersubsampling.glide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.h.triplebuffersubsampling.R;

public class GlideActivity extends AppCompatActivity {

    private Button btn_gelid_50,btn_gelid150,btn_gelid;
    private ImageView image_50,image_150,image;
    private String url = "http://upload-images.jianshu.io/upload_images/1753433-9068356a51b5f777.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/198/format/webp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        //初始化控件
        initView();

        btn_gelid_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage50();
            }
        });

        btn_gelid150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage150();
            }
        });

        btn_gelid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

    }

    private void getImage50() {
        Glide.with(this)
                .load(url)
                .skipMemoryCache(true)//内存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//磁盘缓存,(只缓存处理图)
                .into(image_50);
    }

    private void getImage150() {
        Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(image_150);
    }

    private void getImage() {
        Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘缓存,(缓存处理图和原图)
                .into(image);
    }

    private void initView() {
        btn_gelid_50 = (Button) findViewById(R.id.btn_gelid_50);
        btn_gelid150 = (Button) findViewById(R.id.btn_gelid150);
        btn_gelid = (Button) findViewById(R.id.btn_gelid);
        image_50 = (ImageView) findViewById(R.id.image_50);
        image_150 = (ImageView) findViewById(R.id.image_150);
        image = (ImageView) findViewById(R.id.image);
    }

}
