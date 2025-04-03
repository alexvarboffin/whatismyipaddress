package com.walhalla.whatismyipaddress.whois;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.WhoisPresenter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RootWhoisManager extends WhoisPresenter {

    private static final String KEY_IP = RootWhoisManager.class.getSimpleName();

    private final String NO_DATA_FOUND;
    private final SharedPreferences preferences;
    private final String internet_connectivity_problem0;

    public RootWhoisManager(Context context, Handler handler, Callback callback) {
        super(context, handler, callback);
        this.NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.internet_connectivity_problem0 = context.getString(R.string.internet_connectivity_problem);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void whois(String ip) {
        //onPreExecute
        executor.execute(() -> {
            List<ViewModel> nameValue = new ArrayList<>();
            try {

                boolean isValid = false;

                Socket socket = new Socket("whois.iana.org", 43);// Подключение к WHOIS-серверу
                // Отправка запроса на получение информации о зоне
                socket.getOutputStream().write((ip + "\r\n").getBytes());
                // Чтение ответа
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(":");
                    if (data.length > 1) {
                        nameValue.add(new TwoColItem(data[0], data[1]));
                        isValid = true;
                    }
                }
                // Закрытие соединения
                socket.close();
                if (callback != null) {

                    handler.post(() -> {
                        callback.hideProgress();
                        callback.successResult(nameValue);
                    });

                    if (isValid) {
                        preferences.edit().putString(KEY_IP, ip).apply();
                    }
                }
            } catch (Exception e) {
                DLog.handleException(e);
                if (e instanceof UnknownHostException) {
                    showError(internet_connectivity_problem0);
                } else {
                    showError(NO_DATA_FOUND);
                }
            }
        });
    }

    private void showError(String err) {
        List<ViewModel> aa = new ArrayList<>();
        aa.add(new SingleItem(err, R.color.colorPrimaryDark));
        handler.post(() -> {
            callback.hideProgress();
            callback.successResult(aa);
        });
    }

    public void init() {
        String ip = preferences.getString(KEY_IP, "music");
        callback.init0(ip);
    }
}
