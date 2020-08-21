package com.dongxl.camera;

import android.graphics.SurfaceTexture;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class CusSurfaceTexture extends SurfaceTexture {
    public CusSurfaceTexture(int texName) {
        super(texName);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public CusSurfaceTexture(int texName, boolean singleBufferMode) {
        super(texName, singleBufferMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CusSurfaceTexture(boolean singleBufferMode) {
        super(singleBufferMode);
    }
}
