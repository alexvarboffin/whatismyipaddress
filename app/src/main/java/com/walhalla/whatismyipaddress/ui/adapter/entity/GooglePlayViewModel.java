package com.walhalla.whatismyipaddress.ui.adapter.entity;

public class GooglePlayViewModel extends ViewModel0 {

    public final String packageName;

    public GooglePlayViewModel(String title, String content, String packageName, int icon) {
        super(title, content, icon);
        this.packageName = packageName;
    }
}
