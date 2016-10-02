package com.example.kohki.withmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by kohki on 16/10/02.
 */
public class AsyncTaskListener extends AsyncTask<String, Integer, Long>
        implements OnCancelListener{

    final String TAG = "MyAsyncTask";
    ProgressDialog dialog;
    Context context;

    FrameLayout mainLayout;
    SurfaceView mainSurfaceview;
    ImageView image_menu;
    boolean flg_image = false;

    public AsyncTaskListener(Context context, FrameLayout fl, SurfaceView sv){
        this.context = context;
        mainLayout = fl;
        mainSurfaceview = sv;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");

        mainSurfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
             //   Log.d(TAG,event.getX()+":"+event.getY());
                FrameLayout.LayoutParams mLayoutParms = new FrameLayout.LayoutParams(300, 300);

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("TouchEvent", "getAction()" + "ACTION_DOWN");

                    image_menu = new ImageView(context);
                    image_menu.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hitagi));

                    mLayoutParms.leftMargin = (int) event.getX() - 500;
                    mLayoutParms.topMargin  = (int) event.getY() - 500;

                    mainLayout.addView(image_menu, mLayoutParms);
                    flg_image = true;
                    image_menu.setTag(1);
                    //^^
               /*     image_menu.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(context,"image touch!",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                 */   /*
                    FrameLayout fl = (FrameLayout) findViewById(R.id.camera_screen);

                    for(int i=0;i<fl.getChildCount();i++){
                        //     if(fl.getChildAt(i).getTag(10) != null)
                        Log.d("TouchEvent", "V:"+i+fl.getChildAt(i));
                    }
                    ImageView im = (ImageView)fl.getChildAt(8);
                    im.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(context,"image touch!",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                    */
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("TouchEvent", "getAction()" + "ACTION_UP");
                    mainLayout.removeView(image_menu);
                    flg_image = false;

                }else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    //   Log.d("TouchEvent", "getAction()" + "ACTION_MOVE");

                }

                // MotionEvent.ACTION_CANCEL:
                // Log.d("TouchEvent", "getAction()" + "ACTION_CANCEL");

                return false;
            }
        });
    }

    @Override
    protected Long doInBackground(String... params) {
     /*   Log.d(TAG, "doInBackground - " + params[0]);

        try {
            for(int i=0; i<10; i++){
                if(isCancelled()){
                    Log.d(TAG, "Cancelled!");
                    break;
                }
                Thread.sleep(1000);
                publishProgress((i+1) * 10);
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException in doInBackground");
        }*/
        return 123L;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      //  Log.d(TAG, "onProgressUpdate - " + values[0]);
    }

    @Override
    protected void onCancelled() {
//        Log.d(TAG, "onCancelled");
    }

    @Override
    protected void onPostExecute(Long result) {
    //    Log.d(TAG, "onPostExecute - " + result);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
     //   Log.d(TAG, "Dialog onCancell... calling cancel(true)");
        this.cancel(true);
    }
}