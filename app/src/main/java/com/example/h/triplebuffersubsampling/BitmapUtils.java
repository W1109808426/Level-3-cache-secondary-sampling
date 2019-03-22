package com.example.h.triplebuffersubsampling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {

    private static BitmapUtils instance;

    //1.安全懒汉单例模式,synchronized方法
    public static BitmapUtils getInstance() {
        synchronized (BitmapUtils.class) {
            if (instance == null) {
                instance = new BitmapUtils();
            }
        }
        return instance;
    }

    //2.双重锁定,只在第一次初始化的时候加上同步锁
//    public static BitmapUtils getInstance(){
//        if (instance==null){
//            synchronized (BitmapUtils.class){
//                if (instance==null){
//                    instance=new BitmapUtils();
//                }
//            }
//        }
//        return instance;
//    }

    //3.内部类Holder式,内部类实现单例模式 延迟加载，减少内存开销
//    public static class SingletonInner{
//        private static class SingletonHolder{
//            private static BitmapUtils instance=new BitmapUtils();
//        }
//        private SingletonInner(){
//
//        }
//        public static BitmapUtils getInstance(){
//            return SingletonHolder.instance;
//        }
//        protected void method(){
//            System.out.println("SingletonInner");
//        }
//    }

    public Bitmap getBitmap(String filePath, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int sampleSize = 1;
        while ((outWidth / sampleSize > destWidth) || (outHeight / sampleSize > destHeight)) {
            sampleSize *= 2;
        }

        //二次采样
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(filePath, options);
    }


}

