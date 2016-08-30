package com.example.kohki.withmanager;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by Kohki on 2016/07/28.
 */
public class GameTimer extends CountDownTimer {
    private TextView tv_game_timer;

    public GameTimer(long millisInFuture, long countDownInterval,TextView timer){
        super(millisInFuture, countDownInterval);
        tv_game_timer = timer;
        tv_game_timer.setText("0:00");
    }
    @Override
    public void onFinish() {
        // 完了
        tv_game_timer.setText("0:00");
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