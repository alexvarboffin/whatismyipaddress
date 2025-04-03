package com.walhalla.whatismyipaddress.features.websniffer;

import android.os.Handler;
import android.text.TextUtils;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class WebSnifferPresenter extends BasePresenter implements WebSnifferContract.Presenter {
    private final WebSnifferContract.View view;

    public WebSnifferPresenter(Handler handler, WebSnifferContract.View view) {
        super(handler);
        this.view = view;
    }


    //"Tinkoff Mobile LLC" internet provider isp
    @Override
    public void submitButtonClicked(String url, String requestType, String httpVersion, String userAgent) {
        String host = "";
        int port = -1;
        boolean tryHTTP;
        boolean tryHTTPS;

        if (!isValidUrl(url)) {
            view.showValidationError("Invalid URL");
            return;
        }


        if (url.startsWith("http://")) {
            tryHTTP = true;
            try {
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                view.showValidationError("Malformed URL (HTTP)");
                return;
            }
        } else if (url.startsWith("https://")) {
            tryHTTPS = true;
            try {
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                view.showValidationError("Malformed URL (HTTPS)");
                return;
            }
        } else if (url.startsWith("//")) {
            tryHTTP = true;
            tryHTTPS = true;
            try {
                url = "http:" + url;
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                view.showValidationError("Malformed URL (HTTP/HTTPS)");
                return;
            }
        } else {
            try {
                url = "http://" + url;
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
                u.toURI();//validate url
                DLog.d("@@@" + url);

            } catch (Throwable t) {
                view.showValidationError("Malformed URL (Unknown or unspecified protocol)");
                return;
            }
        }

//        DLog.d("@@@" + isValidURL1(url));
//        DLog.d("@@@" + isValidUrl2(url));
//
//        DLog.d("@@@" + isValidURL1(""));
//        DLog.d("@@@" + isValidUrl2(""));
//
//        DLog.d("@@@nul" + isValidURL1(null));
//        DLog.d("@@@nul" + isValidUrl2(null));


        //        String result = "Request URL: " + url + "\n"
//                + "Request Type: " + requestType + "\n"
//                + "HTTP Version: " + httpVersion + "\n"
//                + "User Agent: " + userAgent;
//        view.showResult(result);
        request(url, requestType, httpVersion, userAgent);

    }

    boolean isValidURL1(String url) //throws MalformedURLException, URISyntaxException
    {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }


    //....NOT USE THIS...

//    boolean isValidUrl2(String url) //throws MalformedURLException
//    {
//        try {
//            // it will check only for scheme and not null input
//            new URL(url);
//            return true;
//        } catch (MalformedURLException e) {
//            return false;
//        }
//    }

    private void request(String url, String requestType, String httpVersion, String userAgent) {
        executor.execute(() -> {
            try {
                RespWrapper response = HttpRequestHelper.sendHttpRequest0(url, requestType, httpVersion, userAgent);
                handler.post(() -> {
                    view.showResult(response);
                });
            } catch (Exception e) {
                DLog.handleException(e);
                handler.post(() -> {
                    view.showValidationError("Failed to send HTTP request");
                });
            }
        });
    }

    private boolean isValidUrl(String url) {
        // Проверка валидности URL
        // Ваша реализация валидации URL
        return !TextUtils.isEmpty(url);
    }
}
