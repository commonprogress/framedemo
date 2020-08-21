package com.dongxl.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.dongxl.library.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CusTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = CusTextureView.class.getSimpleName();
    private Context mContext;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;
    private String mCameraId = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
    private Size mPreviewSize;
    private CameraManager mCameraManager;// 相机管理者
    private CameraDevice mCameraDevice;//相机对象
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;


    /* 让Activity能得到TextureView的SurfaceTexture
     * @see android.view.TextureView#getSurfaceTexture() 获取当前TextureVie的SurfaceTexture
     */
    @Override
    public SurfaceTexture getSurfaceTexture() {
        return super.getSurfaceTexture();
    }

    public CusTextureView(Context context) {
        super(context);
        init(context);
    }

    public CusTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CusTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.setSurfaceTextureListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void onTextureResume() {
        startCameraThread();
    }

    public void onTexturePause() {
        stopPreView();
    }

    /**
     * 开启摄像头线程
     * 初始化执行camera动作的线程和handler
     */
    private void startCameraThread() {
        mCameraThread = new HandlerThread(TAG);
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    /**
     * 停止摄像头线程
     */
    private void stopCameraThread() {
        mCameraThread.quitSafely();
        try {
            mCameraThread.join();
            mCameraThread = null;
            mCameraHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtils.i(TAG, "onSurfaceTextureAvailable...width:" + width + ", height:" + height);
        //当SurefaceTexture可用的时候，设置相机参数并打开相机
        setupCamera(width, height);
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtils.i(TAG, "onSurfaceTextureSizeChanged...width:" + width + ", height:" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        LogUtils.i(TAG, "onSurfaceTextureDestroyed...");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        LogUtils.i(TAG, "onSurfaceTextureUpdated...");
    }

    /**
     * 获取摄像头的管理者CameraManager
     *
     * @return
     */
    private CameraManager getCameraManager() {
        if (null == mCameraManager) {
            mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        }
        return mCameraManager;
    }

    /**
     *
     * @param width
     * @param height
     */
    private void setupCamera(int width, int height) {
        try {
            //遍历所有摄像头  查询摄像头属性 检测摄像头设备ID，有几个摄像头
            for (String cameraId : getCameraManager().getCameraIdList()) {
                //获取相机特征对象
                CameraCharacteristics cameraCharacteristics = getCameraManager().getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);//获取相机特征对象
                //此处默认打开后置摄像头
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT)
                    continue;
                // 获取相机输出流配置Map 他是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                //将最合适的预览尺寸设置给surfaceView或者textureView
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mCameraId = cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取预览的最适宜的长宽比例
     *
     * @param sizeMap
     * @param width
     * @param height
     * @return
     */
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }

    /**
     * 打开摄像头
     */
    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mCameraHandler 指定回调执行的线程。传 null 时默认使用当前线程的 Looper，我们通常创建一个后台线程来处理。
        try {
            getCameraManager().registerAvailabilityCallback(availabilityCallback, mCameraHandler);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCameraManager().registerTorchCallback(torchCallback, mCameraHandler);
            }
            getCameraManager().openCamera(mCameraId, mStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当一个相机设备的闪光灯的 Torch 模式可用状态发生变化时，就会回调这个类的 o
     * nTorchModeChanged(String cameraId, boolean enabled) 和 onTorchModeUnavailable(String cameraId) 方法。
     * 通过 setTorchMode(String cameraId, boolean enabled) 方法设置 Torch 模式。
     */
    private CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(@NonNull String cameraId) {
            super.onTorchModeUnavailable(cameraId);
            LogUtils.i(TAG,"CameraManager.TorchCallback onTorchModeUnavailable");
        }

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            LogUtils.i(TAG,"CameraManager.TorchCallback onTorchModeChanged");
        }
    };

    /**
     * 当一个相机设备的可用状态发生变化时，就会回调这个类的
     * onCameraAvailable(String cameraId) 和 onCameraUnavailable(String cameraId) 方法。
     */
    private CameraManager.AvailabilityCallback availabilityCallback = new CameraManager.AvailabilityCallback() {
        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            super.onCameraAvailable(cameraId);
            //
            LogUtils.i(TAG,"CameraManager.AvailabilityCallback onCameraAvailable");
        }

        @Override
        public void onCameraUnavailable(@NonNull String cameraId) {
            super.onCameraUnavailable(cameraId);
            LogUtils.i(TAG,"CameraManager.AvailabilityCallback onCameraUnavailable");
        }

        @Override
        public void onCameraAccessPrioritiesChanged() {
            super.onCameraAccessPrioritiesChanged();
            LogUtils.i(TAG,"CameraManager.AvailabilityCallback onCameraAccessPrioritiesChanged");
        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            LogUtils.i(TAG,"CameraDevice.StateCallback onOpened");
            //成功打开时的回调，此时 camera 就准备就绪，并且可以得到一个 CameraDevice 实例。
            mCameraDevice = camera;
            startPreView();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            LogUtils.i(TAG,"CameraDevice.StateCallback onDisconnected");
            //当 camera 不再可用或打开失败时的回调，通常在该方法中进行资源释放的操作
            if (mCameraDevice != null) {
                mCameraDevice.close();
                camera.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            LogUtils.i(TAG,"CameraDevice.StateCallback onError");
            //当 camera 打开失败时的回调，error 为具体错误原因，定义在 CameraDevice.StateCallback 类中。通常在该方法中也要进行资源释放的操作。
            if (mCameraDevice != null) {
                mCameraDevice.close();
                camera.close();
                mCameraDevice = null;
            }
        }
    };

    /**
     * 开始预览
     */
    private void startPreView() {
        //设置预览尺寸 设置SurfaceTexture默认的缓冲区大小，为上面得到的预览的size大小
        getSurfaceTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(getSurfaceTexture());
        try {
            //创建CaptureRequest对象，并且声明类型为TEMPLATE_PREVIEW，可以看出是一个预览类型
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);// 添加输出的surface 设置请求的结果返回到到Surface上
            // 创建CameraCaptureSession
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    LogUtils.i(TAG,"CameraDevice.createCaptureSession onConfigured");
                    // 设置为自动对焦
                    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    //设置相机的控制模式为自动，方法具体含义点进去（auto-exposure, auto-white-balance, auto-focus）
//                    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    // 设置关闭闪光灯
                    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.FLASH_MODE_OFF);

                    mCaptureRequest = mCaptureRequestBuilder.build();
                    mCameraCaptureSession = session;
                    try {
                        //设置成预览 设置重复捕获图片信息
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, captureCallback, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    LogUtils.i(TAG,"CameraDevice.createCaptureSession onConfigureFailed");
                    stopPreView();
                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            //当相机设备开始为请求捕捉输出图时
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureStarted");
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            //当图像捕获部分进行时就会回调该方法，此时一些(但不是全部)结果是可用的
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            //当图像捕捉完全完成时，并且结果已经可用时回调该方法
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureCompleted");
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            //对应 onCaptureCompleted 方法，当相机设备产生 TotalCaptureResult 失败时就回调该方法
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureFailed");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            //当一个捕捉段完成时并且所有 CaptureResult 或者 captureFailure 都通过该监听器返回时被回调，这个方法独立于 CaptureCallback 的其他方法
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureSequenceCompleted");
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            //当 CaptureResult 或者 captureFailure 没有通过该监听器被返回而被中断时被回调，这个方法同样独立于 CaptureCallback 的其他方法
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureSequenceAborted");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
            //当捕捉的缓冲没有被送到目标 surface 时被回调
            LogUtils.i(TAG,"CameraCaptureSession.CaptureCallback onCaptureBufferLost");
        }
    };

    /**
     * 停止预览
     */
    private void stopPreView() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onTextureStop() {
        stopCameraThread();
    }

    public void onTextureDestroy() {
        getCameraManager().unregisterAvailabilityCallback(availabilityCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getCameraManager().unregisterTorchCallback(torchCallback);
        }
        mCameraManager = null;
    }
}
