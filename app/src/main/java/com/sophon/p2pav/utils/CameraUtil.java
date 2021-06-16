package com.sophon.p2pav.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sophon.p2pav.Config;
import com.sophon.p2pav.MainActivity;
import com.sophon.p2pav.decode.AvcDecode;
import com.sophon.p2pav.encode.ViedoEncode;

import java.io.IOException;

import androidx.annotation.NonNull;

public class CameraUtil {

    private Camera camera;
    private Camera.Parameters parameters;
    private SurfaceHolder surfaceHolder;
    private ViedoEncode viedoEncode;
    private AvcDecode avcDecode;


    //很多过程都变成了异步的了，所以这里需要一个子线程的looper
    public CameraUtil(SurfaceView surfaceviewOut,SurfaceView surfaceviewIn) {
        surfaceHolder = surfaceviewIn.getHolder();
//        surfaceHolder.setFixedSize(width, height);

//        String filePath = FileUtils.getPath(SurfaceView.this) + "/output.h264";

        surfaceviewOut.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                avcDecode = new AvcDecode(holder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

//        avcDecode = new AvcDecode(width, height, surfaceviewOut.getHolder().getSurface());
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                try {
                    camera = Camera.open(1); // attempt to get a Camera instance
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    camera.setPreviewCallback((data, camera) -> {
                        viedoEncode.encoder(data, h264 -> {
                            if(h264 != null && avcDecode != null){
                                avcDecode.decodeH264(h264);
                            }
                        });
                    });
                    camera.setDisplayOrientation(90);
                    if (parameters == null) {
                        parameters = camera.getParameters();
                    }
                    parameters = camera.getParameters();
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    parameters.setPreviewSize(Config.mImageWidth, Config.mImageHeight);
                    parameters.setPreviewFrameRate(Config.frameRate);

                    findBestPreviewSize(parameters);
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viedoEncode = new ViedoEncode();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (null != camera) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    viedoEncode.StopThread();
                }
            }
        });
    }


    /**
     * 找到最合适的显示分辨率 （防止预览图像变形）
     *
     * @param parameters
     * @return
     */
    private Camera.Size findBestPreviewSize(Camera.Parameters parameters) {

        // 系统支持的所有预览分辨率
        String previewSizeValueString = null;
        previewSizeValueString = parameters.get("preview-size-values");

        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        float bestX = 0;
        float bestY = 0;

        float tmpRadio = 0;
        float viewRadio = 0;

        String[] COMMA_PATTERN = previewSizeValueString.split(",");
        for (String prewsizeString : COMMA_PATTERN) {
            prewsizeString = prewsizeString.trim();

            int dimPosition = prewsizeString.indexOf('x');
            if (dimPosition == -1) {
                continue;
            }

            float newX = 0;
            float newY = 0;

            try {
                newX = Float.parseFloat(prewsizeString.substring(0, dimPosition));
                newY = Float.parseFloat(prewsizeString.substring(dimPosition + 1));
            } catch (NumberFormatException e) {
                continue;
            }

            float radio = Math.min(newX, newY) / Math.max(newX, newY);
            if (tmpRadio == 0) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            } else if (tmpRadio != 0 && (Math.abs(radio - viewRadio)) < (Math.abs(tmpRadio - viewRadio))) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            }
            Log.e("camera","x:"+bestX + "  y:"+bestY);
        }

        if (bestX > 0 && bestY > 0) {
            return camera.new Size((int) bestX, (int) bestY);
        }
        return null;
    }


}
