package com.dongxl.imageload.module;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

//@Excludes(com.dongxl.imageload.module.MyLibraryGlideModule)
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    /**
     * 清单解析
     * 为了维持对 Glide v3 的 GlideModules 的向后兼容性，Glide 仍然会解析应用程序和所有被包含的库中的 AndroidManifest.xml 文件，并包含在这些清单中列出的旧 GlideModules 模块类。
     * <p>
     * 如果你已经迁移到 Glide v4 的 AppGlideModule 和 LibraryGlideModule ，你可以完全禁用清单解析。这样可以改善 Glide 的初始启动时间，并避免尝试解析元数据时的一些潜在问题。要禁用清单解析，请在你的 AppGlideModule 实现中复写 isManifestParsingEnabled() 方法：
     *
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        //资源重用错误的征兆
        int bitmapPoolSizeBytes = 1024 * 1024 * 0; // 0mb
        int memoryCacheSizeBytes = 1024 * 1024 * 0; // 0mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));
        //解码格式
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
        super.applyOptions(context, builder);
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
