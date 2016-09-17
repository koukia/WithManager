package com.example.kohki.withmanager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

abstract public class CustomDialog extends DialogFragment {
    public CustomDialog(){}

    abstract public Dialog makeDialog();
    abstract public void onCancel(CustomDialog dialog);
    abstract public void onDismiss(CustomDialog dialog);

    @Override
    public void onCancel(DialogInterface dialog){
        onCancel(this);
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        onDismiss(this);
        super.onDismiss(dialog);
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState){
        return makeDialog();
    }

    @Override
    public final void show(FragmentManager manager, String tag){
        deleteDialogFragment(manager, tag);
        super.show(manager, tag);
    }

    public final void show(Activity activity, String tag){
        show(activity.getFragmentManager(), tag);
    }

    private final void deleteDialogFragment(FragmentManager manager, String tag){
        CustomDialog customDialog = (CustomDialog)manager.findFragmentByTag(tag);

        //フラグメントが表示されていなければスキップ
        if(customDialog == null) return;

        Dialog dialog = customDialog.getDialog();

        //ダイアログがなければスキップ
        if(dialog == null) return;

        // ダイアログが表示されていなければ処理なし
        if (!dialog.isShowing()) return;

        //ダイアログを閉じる
        customDialog.dismiss();
    }
}

