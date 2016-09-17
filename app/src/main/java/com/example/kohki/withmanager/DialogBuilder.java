package com.example.kohki.withmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

public class DialogBuilder {
    abstract public static class OnCancelListener{
        abstract protected void onCancel(CustomDialog dialog);
    }

    abstract public static class OnDismissListener{
        abstract protected void onDismiss(CustomDialog dialog);
    }

    private final Activity activity;
    private final AlertDialog.Builder builder;
    private OnCancelListener onCancelListener = null;
    private OnDismissListener onDismissListener = null;
    private boolean canceledOnTouchOutside = false;

    private CustomDialog blueDialog;

    public DialogBuilder(Activity activity){
        this.activity = activity;
        builder = new AlertDialog.Builder(activity);
    }

    public DialogBuilder setIcon(int iconId){
        builder.setIcon(iconId);
        return this;
    }

    public DialogBuilder setTitle(CharSequence title){
        builder.setTitle(title);
        return this;
    }

    public DialogBuilder setTitle(int titleId){
        builder.setTitle(titleId);
        return this;
    }

    public DialogBuilder setMessage(String message){
        builder.setMessage(message);
        return this;
    }

    public DialogBuilder setMessage(int messageId){
        builder.setMessage(messageId);
        return this;
    }

    public DialogBuilder setView(View view){
        builder.setView(view);
        return this;
    }

    public DialogBuilder setItems(CharSequence[] items, OnClickListener listener){
        builder.setItems(items, listener);
        return this;
    }

    public DialogBuilder setItems(int itemsId, OnClickListener listener){
        builder.setItems(itemsId, listener);
        return this;
    }

    public DialogBuilder setPositiveButton(CharSequence text, OnClickListener listener){
        builder.setPositiveButton(text, listener);
        return this;
    }

    public DialogBuilder setPositiveButton(int textId, OnClickListener listener){
        builder.setPositiveButton(textId, listener);
        return this;
    }

    public DialogBuilder setNegativeButton(CharSequence text, OnClickListener listener){
        builder.setNegativeButton(text, listener);
        return this;
    }

    public DialogBuilder setNegativeButton(int textId, OnClickListener listener){
        builder.setNegativeButton(textId, listener);
        return this;
    }

    public DialogBuilder setOnCancelListener(OnCancelListener onCancelListener){
        this.onCancelListener = onCancelListener;
        return this;
    }

    public DialogBuilder setOnDismissListener(OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
        return this;
    }

    public DialogBuilder setCanceledOnTouchOutside(boolean cancel){
        canceledOnTouchOutside = cancel;
        return this;
    }

    public CustomDialog create(){
        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        final OnCancelListener onCancelListener = this.onCancelListener;
        final OnDismissListener onDismissListener = this.onDismissListener;

        blueDialog = new CustomDialog(){
            @Override
            public Dialog makeDialog(){
                return dialog;
            }

            @Override
            public void onCancel(CustomDialog dialog){
                if(onCancelListener != null)
                    onCancelListener.onCancel(dialog);
            }

            @Override
            public void onDismiss(CustomDialog dialog){
                if(onDismissListener != null)
                    onDismissListener.onDismiss(dialog);
            }
        };
        return blueDialog;
    }

    public void show(String tag){
        if(blueDialog == null)
            create();

        blueDialog.show(activity, tag);
    }

    public static void showErrorDialog(Activity activity, String message){
        new DialogBuilder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("エラー")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show("エラー");
    }
}

