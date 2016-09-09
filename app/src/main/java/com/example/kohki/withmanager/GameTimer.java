package com.example.kohki.withmanager;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kohki on 2016/07/28.
 */
public class GameTimer extends CountDownTimer {
    private TextView tv_game_timer;
    private Context context;

    public GameTimer(long millisInFuture, long countDownInterval,TextView timer, Context context){
        super(millisInFuture, countDownInterval);
        tv_game_timer = timer;
        tv_game_timer.setText("0:00");
        this.context = context;
    }
    @Override
    public void onFinish() {
        // 完了
        tv_game_timer.setText("0:00");
        Toast.makeText(context, "試合終了しました", Toast.LENGTH_LONG).show();
    }
    // インターバルで呼ばれる
    @Override
    public void onTick(long millisUntilFinished) {
        // 残り時間を分、秒、ミリ秒に分割
        long mm = millisUntilFinished / 1000 / 60;
        long ss = millisUntilFinished / 1000 % 60;

        tv_game_timer.setText(String.format("%1$02d:%2$02d", mm, ss));
    }

}