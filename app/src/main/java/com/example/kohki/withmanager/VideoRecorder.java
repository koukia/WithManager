package com.example.kohki.withmanager;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kohki on 2016/07/25.
 */
public class VideoRecorder implements SurfaceHolder.Callback {
    private final static String TAG = "VideoRecorderClass";

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera = null;

    private Context context;
    private boolean is_recording = false;

    private String mSavaPath;
    private String mMovieFilePath;

    Resources resources;
    public VideoRecorder(Context context, String path, SurfaceView sv, Resources resources){
        this.context = context;
        mSavaPath = path;
        surfaceView = sv;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        this.resources = resources;
        resume();
    }

    public void resume(){
        int  numberOfCameras = Camera.getNumberOfCameras();
        Log.d("Camera num", numberOfCameras+"");
        // 各カメラの情報を取得
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo caminfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, caminfo);
            Log.v("CameraID", ""+i);
            // カメラの向きを取得
            int facing = caminfo.facing;
            if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) { // facing is 0
                try{
                    Log.v(TAG, "1");
                    mCamera = Camera.open(i);
                    Log.v(TAG, "2");
                    surfaceHolder = surfaceView.getHolder();
                    Log.v(TAG, "3");
                    surfaceHolder.addCallback(this);
                    Log.v(TAG, "4");
                } catch (RuntimeException ex){
                    Log.d("Err","Camera cant open");
                    Toast.makeText(context, "RuntimeException", Toast.LENGTH_LONG).show();
                }
            }else if(facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                Log.v("FRONT_CAMERA", "cameraId=" + Integer.toString(i));
            }
        }
    }
    public void pause(){
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void start(){
        if(mCamera == null)
            resume();
        try {
            mMediaRecorder = new MediaRecorder();
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);

            // TODO:他端末での対応 corresponding each device
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            //    Toast.makeText(context,Integer.toString(camcorderProfile.videoCodec),Toast.LENGTH_SHORT).show();
            camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
            mMediaRecorder.setProfile(camcorderProfile);

                /*decide file name*/
            Date date = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mSavaPath = mSavaPath + sdf1.format(date) + ".mp4";
            mMediaRecorder.setOutputFile(mMovieFilePath);
                /* --- */
            mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());// プレビューに利用するサーフェイスを指定する
      //      mrec.setVideoSize(getWidth(), getHeight()); //=> start failed -19
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            is_recording = true;
        }catch (IOException e) {
            Log.e(TAG,""+e);
            Toast.makeText(context, "IOe:" + e, Toast.LENGTH_SHORT).show();
        }catch (RuntimeException e) {
            Toast.makeText(context, "Re:" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        if(mrec != null) {
            try {
                mrec.stop();
                mrec.reset();
                mrec.release();
                mrec = null;
            }catch (RuntimeException e){
                Toast.makeText(context,e+"",Toast.LENGTH_SHORT).show();
            }
        }
        is_recording = false;
    }

    public String save() {

        VideoEdit mVideoEdit = new VideoEdit();
        File origin_file = originMovies.get(originMovies.size() - 1);
        String[] arr_origin_filename = origin_file.getAbsoluteFile().toString().split("/");
        String edit_filename = "";
        for (int i = 0; i < arr_origin_filename.length; i++) {
            edit_filename += arr_origin_filename[i];
            if (i != arr_origin_filename.length - 1)
                edit_filename += "/";
            if (i == arr_origin_filename.length - 2)
                edit_filename += "Edited_";
        }
        int origin_movie_time = getDuration(origin_file);
        File edit_file = new File(edit_filename); //修正後ファイル
        if (origin_movie_time > VideoActivity.sMovieTime) {
            int cut_start_time = (origin_movie_time - VideoActivity.sMovieTime) / 1000 * 1000 - 500;
            //Toast.makeText(context, "movie_time:" + movie_time + "\ncut_start_time:" + cut_start_time, Toast.LENGTH_LONG).show();
            if (cut_start_time <= 0) {
                //   Toast.makeText(context, "ノーカット", Toast.LENGTH_LONG).show();
            } else {//5秒以下に編集
                try {
                    //     Toast.makeText(context, "カットあり", Toast.LENGTH_LONG).show();
                    mVideoEdit.startTrim(origin_file, edit_file, cut_start_time, origin_movie_time);
                } catch (IOException e) {
                    Log.d("IOException", "startTrim");
                } catch (Exception e) {
                    Log.d("Exception", "startrim");
                }
            }
            //Toast.makeText(context, edit_file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
            // Toast.makeText(context, getDuration(new File(after_edit_file.getAbsolutePath().toString())), Toast.LENGTH_LONG).show();
        } else if (origin_movie_time < VideoActivity.sMovieTime) {
            //   Toast.makeText(context, "within 5000ms\n"+editedMovies.size(), Toast.LENGTH_SHORT).show();
            if (editedMovies.size() >= 1) { //editedMovies.get(editedMovies.size() - 1).exists()
                try {
                    boolean result = mVideoEdit.appendMovie(
                            editedMovies.get(editedMovies.size()-1).getAbsolutePath().toString(),
                            origin_file.getAbsolutePath(),
                            edit_file.getAbsolutePath() );
                    if(!result) {
                        //   Toast.makeText(context, "結合失敗", Toast.LENGTH_LONG).show();
                    }
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(context, "IndexOutOfBoundsException", Toast.LENGTH_LONG).show();
                }
            } else {
                copyFile(origin_file, edit_file);
                //       Toast.makeText(context, "5秒未満の動画を生成(origin copy)", Toast.LENGTH_LONG).show();
            }
        }
        editedMovies.add(edit_file);
        return edit_filename;
    }
    private static int getDuration(File audioFile) {
        MediaPlayer mp = new MediaPlayer();
        FileInputStream fs = null;
        FileDescriptor fd;
        int length = 0;
        try {
            fs = new FileInputStream(audioFile);
            fd = fs.getFD();
            mp.setDataSource(fd);
            mp.prepare();
            length = mp.getDuration();
            mp.release();
        }catch (IOException e){Log.v("Err","IOException");}
        return length;
    }
    private void copyFile(File before_edit_file, File after_edit_file){
        try {
            FileChannel inChannel  = new FileInputStream(before_edit_file).getChannel();
            FileChannel outChannel = new FileOutputStream(after_edit_file).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } catch (IOException e) {
                throw e;
            } finally {
                if (inChannel  != null)  inChannel.close();
                if (outChannel != null) outChannel.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /* these 3 method are needed */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        //    Toast.makeText(context, " surfaceChanged()", Toast.LENGTH_SHORT).show();
        startPreview(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //    Toast.makeText(context, " surfaceCreated()", Toast.LENGTH_SHORT).show();
        if (mCamera != null){
        } else {
            //finish();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //    Toast.makeText(context, " surfaceDestroyed()", Toast.LENGTH_SHORT).show();
    }
    /* --- */

    private void startPreview(SurfaceHolder holder) {
        try {
            Log.i(TAG, "starting preview");

            Camera.CameraInfo camInfo = new Camera.CameraInfo();

            /*
               if(params.getSupportedFocusModes().contains(
                    params.FOCUS_MODE_CONTINUOUS_VIDEO)){
                params.setFocusMode(params.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
         */
            int camera_id = findFrontFacingCameraID();
            Camera.getCameraInfo(camera_id, camInfo);
            int cameraRotationOffset = camInfo.orientation;
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size previewSize = null;
            float closestRatio = Float.MAX_VALUE;

            int targetPreviewWidth = isLandscape() ? getWidth() : getHeight();
            int targetPreviewHeight = isLandscape() ? getHeight() : getWidth();
            //          int targetPreviewWidth  = getHeight();
            //          int targetPreviewHeight = getWidth();

            float targetRatio = targetPreviewWidth / (float) targetPreviewHeight;

            Log.v(TAG, "target size: " + targetPreviewWidth + " / " + targetPreviewHeight + " ratio:" + targetRatio);

            for (Camera.Size candidateSize : previewSizes) {
                float whRatio = candidateSize.width / (float) candidateSize.height;
                if (previewSize == null || Math.abs(targetRatio - whRatio) < Math.abs(targetRatio - closestRatio)) {
                    closestRatio = whRatio;
                    previewSize = candidateSize;
                }
            }

            int degrees = getSurfaceDegrees();
            int displayRotation = getDisplayRotation(camInfo, cameraRotationOffset, degrees);
            Log.d(TAG, "displayRotation:" + displayRotation);
            mCamera.setDisplayOrientation(displayRotation);
            int rotate = getRotation(camInfo, cameraRotationOffset, degrees);
            Log.v(TAG, "rotate: " + rotate);
            Log.v(TAG, "preview size: " + previewSize.width + " / " + previewSize.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setRotation(rotate);

            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

            Log.d(TAG, "preview started");
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    private boolean isFrontFacingCam(Camera.CameraInfo camInfo){

        boolean isFrontFacingCam;

        if(camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            isFrontFacingCam = true;
        }else{
            isFrontFacingCam = false;
        }

        return isFrontFacingCam;
    }
    private int getHeight(){
    //    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
    //    int height = dm.heightPixels;
    //    return height;
        return surfaceView.getHeight();
    }
    private int getWidth(){
    //    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
    //    int width = dm.widthPixels;
    //    return width;
        return surfaceView.getWidth();
    }
    private boolean isLandscape(){
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else{
            return false;
        }
    }

    private int getSurfaceDegrees(){
        Configuration config = resources.getConfiguration();
        int degrees = 90;
        switch(config.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                degrees = 0;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                degrees = 90;
                break;
        }
        return degrees;
    }

    private int getRotation(Camera.CameraInfo camInfo, int cameraRotationOffset, int degrees){
        int rotate;
        if (isFrontFacingCam(camInfo)) {
            rotate = (360 + cameraRotationOffset + degrees) % 360;
        } else {
            rotate = (360 + cameraRotationOffset - degrees) % 360;
        }
        return rotate;
    }

    private int getDisplayRotation(Camera.CameraInfo camInfo, int cameraRotationOffset, int degrees){
        int displayRotation;
        if (isFrontFacingCam(camInfo)) {
            displayRotation = (cameraRotationOffset + degrees) % 360;
            displayRotation = (360 - displayRotation) % 360;
        } else { // back-facing
            displayRotation = (cameraRotationOffset - degrees + 360) % 360;
        }
        return displayRotation;
    }

    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
