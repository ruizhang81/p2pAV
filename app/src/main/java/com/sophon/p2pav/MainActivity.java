package com.sophon.p2pav;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.sophon.p2pav.decode.BaseDecode;
import com.sophon.p2pav.utils.CameraUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST = 1;
    private SurfaceView surfaceView;
    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.sfv);
        mTextureView = findViewById(R.id.tv);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}
                    , REQUEST);
        }

        init();
    }


    private void init() {

        new CameraUtils(this,mTextureView);
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(@NonNull SurfaceHolder holder) {
//                baseDecodeList.clear();
//                ExecutorService mExecutorService = Executors.newFixedThreadPool(2);
//                baseDecodeList.add(videoDecode);
//                baseDecodeList.add(audioEncode);
//                mExecutorService.execute(videoDecode);
//                mExecutorService.execute(audioEncode);
//            }
//
//            @Override
//            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//                for (BaseDecode baseDecode : baseDecodeList) {
//                    baseDecode.stop();
//                }
//            }
//        });
//        surfaceView.setVisibility(View.VISIBLE);
    }
}