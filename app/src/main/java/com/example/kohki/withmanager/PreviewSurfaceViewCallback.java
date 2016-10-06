package com.example.kohki.withmanager;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Kohki on 2016/07/25.
 */
public class PreviewSurfaceViewCallback implements SurfaceHolder.Callback {
        private static final String TAG = "PreviewCallback";
        private SurfaceHolder holder;
        private Context context;
        public MediaPlayer mMediaPlayer = null;

        public PreviewSurfaceViewCallback(Context context){
            this.context = context;
        }
        public void palyVideo(String path){
            String data_sorce = path;
            try {
                // MediaPlayerを生成
                mMediaPlayer = new MediaPlayer();
                // 動画ファイルをMediaPlayerに読み込ませる
                mMediaPlayer.setDataSource(data_sorce);
                // 読み込んだ動画ファイルを画面に表示する
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IllegalArgumentException e) {
                Toast.makeText(context, "IllegalArgumentException:"+e, Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(context, "SecurityException:"+e, Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Log.e(TAG,e.getMessage()+", "+e);
                Toast.makeText(context, "IllegalStateException:"+e, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG,e+"");
                Toast.makeText(context, "IOException:"+e, Toast.LENGTH_LONG).show();
            } finally {

            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
            this.holder = holder;
        //    Toast.makeText(context,"surfaceChanged()",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            this.holder = holder;
        //    Toast.makeText(context,"surfaceCreated()",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        //    Toast.makeText(context, " surfaceDestroyed()", Toast.LENGTH_SHORT).show();
            this.holder = null;
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        //    Toast.makeText(context,"surfaceDestroyed()",Toast.LENGTH_SHORT).show();
        }

}
