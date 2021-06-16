package com.sophon.p2pav;

import android.Manifest;
import android.os.Bundle;
import android.view.SurfaceView;

import com.sophon.p2pav.utils.CameraUtil;
import com.sophon.p2pav.utils.FileUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST = 1;
    private SurfaceView surfaceView;
    private SurfaceView surfaceView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.sfv);
        surfaceView2 = findViewById(R.id.sfv2);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.CAMERA}
                , REQUEST);

        init();
    }

    private void init() {
        new CameraUtil(surfaceView,surfaceView2);
    }

}