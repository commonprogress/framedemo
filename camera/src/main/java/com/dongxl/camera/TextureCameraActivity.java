package com.dongxl.camera;

import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.dongxl.library.mvp.BaseActivity;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TextureCameraActivity extends BaseActivity {
    CusPicTextureView textureView;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected int getResourceId() {
        return 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textureView = new CusPicTextureView(this);
        setContentView(textureView);
        mSurfaceTexture = textureView.getSurfaceTexture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textureView.onTextureResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        textureView.onTexturePause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        textureView.onTextureStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureView.onTextureDestroy();
    }
}
