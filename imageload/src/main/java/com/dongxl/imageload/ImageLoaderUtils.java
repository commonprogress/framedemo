package com.dongxl.imageload;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoaderUtils {

    private static volatile ImageLoaderUtils instance;

    public ImageLoaderUtils getInstance() {
        if (null == instance) {
            synchronized (ImageLoaderUtils.class) {
                if (null == instance) {
                    instance = new ImageLoaderUtils();
                }
            }
        }
        return instance;
    }

    public void load(ImageView imageView, String url, int placeId, int errorId) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeId);
        options.error(errorId);
        Glide.with(imageView).load(url).apply(options).into(imageView);
    }

}
