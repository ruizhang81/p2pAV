package com.sophon.p2pav.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class CameraUtils {


    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private Surface mPreviewSurface;
    //private String mCameraId;
    //private Handler mHandler;

    public CameraUtils(Context context, TextureView mTextureView) {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
                mPreviewSurface = new Surface(arg0);
                CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                try {
                    manager.openCamera("1", new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(CameraDevice arg0) {
                            mCameraDevice = arg0;
                            try {
                                mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface), new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession arg0) {
                                        mCameraCaptureSession = arg0;
                                        try {
                                            CaptureRequest.Builder builder;
                                            builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                            builder.addTarget(mPreviewSurface);
                                            mCameraCaptureSession.setRepeatingRequest(builder.build(), null, null);
                                        } catch (CameraAccessException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession arg0) {
                                    }
                                }, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(CameraDevice arg0, int arg1) {

                        }

                        @Override
                        public void onDisconnected(CameraDevice arg0) {

                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
                return false;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {

            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

            }

        });
    }
}
