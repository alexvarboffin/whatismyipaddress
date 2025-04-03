package com.walhalla.whatismyipaddress.features.portscanning;


import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

import java.net.UnknownHostException;
import java.util.ArrayList;

public interface PortScanContract {

//    void showProgress();
//    void hideProgress();
//    void displayScanResult(String result);

    interface View {
        void showProgress();

        void hideProgress();

        void displayScanProgress(String result);

        void handleException(String ipText, Exception e0);

        void displayScanResult(ArrayList<ViewModel> dataModels);

        void init0(String ip);
    }
    // Другие методы для взаимодействия с UI
}

