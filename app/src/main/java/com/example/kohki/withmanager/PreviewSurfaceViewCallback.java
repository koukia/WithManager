package com.example.kohki.withmanager;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Kohki on 2016/07/25.
 */
public class PreviewSurfaceViewCallback implements SurfaceHolder.Callback {

        private SurfaceHolder holder;
        private Context context;
        private String preview_video_path;
        public MediaPlayer mMediaPlayer = null;

        public PreviewSurfaceViewCallback(Context context){
            this.context = context;
        }
        public void palyVideo(String path){
            preview_video_path = path;
            try {
                // MediaPlayerを生成
                mMediaPlayer = new MediaPlayer();
                // 動画ファイルをMediaPlayerに読み込ませる
                mMediaPlayer.setDataSource(preview_video_path);
                // 読み込んだ動画ファイルを画面に表示する
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IllegalArgumentException e) {
                Toast.makeText(context, "IllegalArgumentException", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(context, "SecurityException", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Toast.makeText(context, "IllegalStateException", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(context, "IOException", Toast.LENGTH_LONG).show();
            } finally {

            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
            this.holder = holder;
            /*
            Canvas mCanvas = holder.lockCanvas();
            Paint mPaint = new Paint();
        //    mPaint.setColor(Color.RED);
            mPaint.setAlpha(130);
            mCanvas.drawPaint(mPaint);

            float right = (float)mCanvas.getWidth();
            float bottom = (float)mCanvas.getHeight();
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.hitagi);
            int x = (int)right/2 - image.getWidth()/2;
            int y = (int)bottom/2 - image.getHeight()/2;
            mCanvas.drawBitmap(image, x,y, null);
            holder.unlockCanvasAndPost(mCanvas);
            */
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        //    Toast.makeText(context, " surfaceDestroyed()", Toast.LENGTH_SHORT).show();
            this.holder = null;
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

}
