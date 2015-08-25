package com.shipchung.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class LoadingDialog extends Dialog{
    public LoadingDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }
}
