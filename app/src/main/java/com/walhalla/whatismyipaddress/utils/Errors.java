package com.walhalla.whatismyipaddress.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;

import es.dmoral.toasty.Toasty;

public class Errors {

    public static void showError(Context context, String message) {

        ComV19 comv19 = new ComV19();

        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Toasty.custom(context, message,
                comv19.getDrawable(context, R.drawable.ic_cancel),
                ContextCompat.getColor(context, R.color.error),
                ContextCompat.getColor(context, R.color.white),
                Toasty.LENGTH_SHORT, true, true).show();

    }

}
