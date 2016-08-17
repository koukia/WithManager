package com.example.kohki.withmanager;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class HomeActivity extends AppCompatActivity {
    private final static int SDKVER_LOLLIPOP = 21;
    private Button mBtnStart;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // フルスクリーン表示.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        context = this;
        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(mBtnStartClicked);
        //    ImageView iv = (ImageView)findViewById(R.id.imageView);
        //    iv.setImageResource(R.drawable.play02);

    }
    private final View.OnClickListener mBtnStartClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //    if (Build.VERSION.SDK_INT >= SDKVER_LOLLIPOP) {
            try {
                Intent ittView_video = new Intent(context, VideoActivity.class);
                startActivity(ittView_video);
            }catch (Exception e) {
                Log.v("IntentErr:", e.getMessage() + "," + e);
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}
