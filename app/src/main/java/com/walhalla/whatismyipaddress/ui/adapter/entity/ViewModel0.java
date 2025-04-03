package com.walhalla.whatismyipaddress.ui.adapter.entity;

public class ViewModel0 {
    public final String label;
    public final String content;
    public final Integer icon;

    public ViewModel0(String title, String content, int icon) {
        this.label = title;
        this.content = content;
        this.icon = icon;
    }

    public ViewModel0(String label, String content) {
        this.label = label;
        this.content = content;
        this.icon = null;
    }
}
