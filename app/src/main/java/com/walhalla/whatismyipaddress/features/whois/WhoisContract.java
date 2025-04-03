package com.walhalla.whatismyipaddress.features.whois;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

import java.util.ArrayList;

public interface WhoisContract {
    interface View{
        void showProgress();

        void hideProgress();

        void displayScanProgress(String result);

        void handleException(String ipText, Exception e0);

        void displayScanResult(ArrayList<ViewModel> dataModels);
    }
}
