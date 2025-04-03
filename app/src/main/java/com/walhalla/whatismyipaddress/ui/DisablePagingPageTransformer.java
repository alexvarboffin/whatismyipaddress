package com.walhalla.whatismyipaddress.ui;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class DisablePagingPageTransformer implements ViewPager2.PageTransformer {

    private final boolean isPagingEnabled;

    public DisablePagingPageTransformer(boolean isPagingEnabled) {
        this.isPagingEnabled = isPagingEnabled;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (!isPagingEnabled) {
            page.setAlpha(1.0f);
            page.setTranslationX(0);
            page.setTranslationY(0);
            page.setScaleX(1.0f);
            page.setScaleY(1.0f);
            page.setRotationY(0);
        }
    }
}