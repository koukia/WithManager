package com.example.kohki.withmanager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Kohki on 2016/07/28.
 */
public class OverlayContent extends View {

    public String msg;

    //constructor
    public OverlayContent(Context context) {
        super(context);

        //初期値
        msg = "Hello Android";
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //canvas.drawColor(Color.TRANSPARENT);

        //四角形を描画
        Paint paint = new Paint();
 /*       paint.setStyle(Paint.Style.FILL);
        paint.setARGB(150, 0, 0, 255);

        //描画：x1,y1,x2,y2,paint
        canvas.drawRect(100, 100, 600, 200, paint);

        //テキストを描画
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(30);
        canvas.drawText(msg,300,150,paint);
*/
        //リソースから取得して描画
        Resources res = getResources();
        int res_id = R.drawable.play;

        //透明度設定
     //   paint.setAlpha(255);

        //ビットマップ描画
        Bitmap bmp = BitmapFactory.decodeResource(res,res_id);
        int weight = 500;
        int height = 100;
        canvas.drawBitmap(bmp,weight, height, paint);

    }
}