package com.mt178.loadbigimage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity:";
    private ImageView iv_showImage;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_showImage = (ImageView) findViewById(R.id.iv_showImage);

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.e(TAG, maxMemory + "KB");
        //设置图片缓存为程序内存的1/8
        int cacheSize = maxMemory / 8;
        //使用最近最少使用到的图片进行缓存
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;

            }
        };
        iv_showImage.setImageBitmap(decodeSampleBitmapFromReSource(getResources(), R.drawable.show, 100, 100));
        //      iv_showImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.show));

       /* BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.show, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;*/
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 计算出InsampleSize
     *
     * @param options
     * @param reqWidth  ImageView的宽度
     * @param reqHeight ImageView的高度
     * @return
     */
    public int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            Log.e(TAG, "height:" + height + "----" + "reqheight" + reqHeight);
            Log.e(TAG, "width:" + width + "----" + "reqWidth" + reqWidth);
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        Log.v(TAG, "insamplesize:" + inSampleSize);
        return inSampleSize;
    }

    /**
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromReSource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
     /*   int height = options.outHeight;
        int width = options.outWidth;*/
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = caculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
