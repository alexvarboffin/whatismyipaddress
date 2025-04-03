//package com.walhalla.compat;
//
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.File;
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//public class TelegramClient {
//
//    private final String var0;
//    private final String token;
//
//    public TelegramClient(String chatId, String token) {
//        this.var0 = chatId;
//        this.token = token;
//    }
//
//    public void sendMessage(String message, Callback callback) {
//
//
//        OkHttpClient httpClient = NetworkUtils.makeOkhttp();
//        String body = "chat_id=" + var0 + "&text=" + message;
//
//
//        RequestBody data;
//        data = MultipartBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8"), body);
//
//
//        Request request = new Request.Builder()
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0") //optional
//                .url("https://api.telegram.org/bot" + token + "/sendMessage")
//                .post(data) //call post
//                .build();
//
//
//        Call tk = client.newCall(request);
//        tk.enqueue(callback);
//    }
//}
//
