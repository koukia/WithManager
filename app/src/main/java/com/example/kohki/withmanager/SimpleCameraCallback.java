package com.example.kohki.withmanager;

import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by kohki on 16/10/06.
 */
public class SimpleCameraCallback implements SurfaceHolder.Callback {
    private SurfaceView mySurfaceView;
    private Camera myCamera;


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        //CameraOpen
     //   myCamera = VideoActivity.mCamera;
        //出力をSurfaceViewに設定
        try{
            myCamera.setPreviewDisplay(surfaceHolder);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //プレビュースタート（Changedは最初にも1度は呼ばれる）
        myCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //片付け
        myCamera.release();
        myCamera = null;
    }
}
