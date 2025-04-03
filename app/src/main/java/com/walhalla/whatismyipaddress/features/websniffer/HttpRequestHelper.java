package com.walhalla.whatismyipaddress.features.websniffer;


import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.ui.DLog;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestHelper {
    private static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded");

    public static RespWrapper sendHttpRequest0(String url, String requestType, String httpVersion, String userAgent) throws IOException {


        //protocols.add(Protocol.HTTP_1_0);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        if ("HTTP/2.0".equals(httpVersion)) {
//            List<Protocol> protocols = new ArrayList<>();
//            protocols.add(Protocol.HTTP_2);
//            builder.protocols(protocols);
//        } else if ("HTTP/1.1".equals(httpVersion)) {
//            List<Protocol> protocols = new ArrayList<>();
//            protocols.add(Protocol.HTTP_1_1);
//            builder.protocols(protocols);
//        } else if ("h2_prior_knowledge".equals(httpVersion)) {
//            List<Protocol> protocols = new ArrayList<>();
//            protocols.add(Protocol.H2_PRIOR_KNOWLEDGE);
//            builder.protocols(protocols);
//        }

        //builder.addInterceptor(new MainInterceptor(context, signature));
        OkHttpClient httpClient = NetworkUtils.makeOkhttp();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Referer", "https://google.com/");


//        Request request = new Request.Builder()
//                .url(url)
//                .method(method, null)
//                .build();

//                .get() // Указание метода GET
//                .addHeader("Host", "example.com") // Пример добавления заголовка "Host"
//                .addHeader("User-Agent", "Your User-Agent") // Пример добавления заголовка "User-Agent"
//                .header("HTTP/1.0", null); // Установка версии HTTP на 1.0

        Request request = null;
        if (requestType.equalsIgnoreCase("POST")) {
            // Add request body if request type is POST
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_FORM, "");
            requestBuilder.post(requestBody);
            request = requestBuilder.build();
        } else if (requestType.equalsIgnoreCase("HEAD")) {
            request = new Request.Builder()
                    .url(url)
                    .head()
                    .build();
        } else {
            request = requestBuilder.build();
        }

//        if ("HTTP/1.0".equals(httpVersion)) {
//            // Set HTTP version if provided
//            //requestBuilder.header("HTTP-Version", httpVersion);
//            requestBuilder.header("HTTP/1.0", ""); // Установка версии HTTP на 1.0
//        }

        //java.lang.RuntimeException: Malformed URL

        //client.setProtocols(protocols);
        Response response = httpClient.newCall(request).execute();
//        if (response.isSuccessful()) {
//        } else {
//            throw new IOException("Unexpected response code: " + response.code());
//        }
        Response r0 = response.networkResponse();
        String networkResponse = r0.toString();

        String url0 = r0.request().url().toString();
        String host = r0.request().url().uri().getHost();

        int port = r0.request().url().port();


        String ip = "";
        Handshake mmm = response.handshake();
        if (mmm != null) {
            ip = mmm.peerPrincipal().getName();
            DLog.d("<@@@>" + mmm);
            DLog.d("<@@@>" + mmm.peerPrincipal());

        }

        Map<String, String> requestHeaders = toMap(request.headers());
        Map<String, String> responseHeaders = toMap(response.headers());
        String html = response.body().string();
        DLog.d("" + new RespWrapper(ip, port, url0, host, networkResponse, requestHeaders, responseHeaders, html, r0.code()));
        return new RespWrapper(ip, port, url0, host, networkResponse, requestHeaders, responseHeaders, html, r0.code());
    }

    private static Map<String, String> toMap(Headers headers) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String name : headers.names()) {
            map.put(name, headers.get(name));
        }
        return map;
    }

}
