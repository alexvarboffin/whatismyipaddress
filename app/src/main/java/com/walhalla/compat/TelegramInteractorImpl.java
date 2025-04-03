//package com.walhalla.compat;
//
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.walhalla.boilerplate.domain.executor.Executor;
//import com.walhalla.boilerplate.domain.executor.MainThread;
//import com.walhalla.boilerplate.domain.interactors.base.AbstractInteractor;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//public class TelegramInteractorImpl extends AbstractInteractor {
//
//    private final TelegramClient telegramClient;
//
//    public TelegramInteractorImpl(Executor threadExecutor, MainThread mainThread, TelegramClient client) {
//        super(threadExecutor, mainThread);
//        this.telegramClient = client;
//    }
//
//    @Override
//    public void run() {
//
//    }
//
//    public interface QCallback<T> {
//        void onMessageRetrieved(T message);
//
//        void onRetrievalFailed(String error);
//    }
//
//    public void screen(final String message, QCallback<String> callback) {
//        this.mThreadExecutor.submit(() -> {
//            try {
//                telegramClient.sendMessage(message, new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        mMainThread.post(() -> {
//                                    callback.onRetrievalFailed(e.getLocalizedMessage());
//                                });
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        ResponseBody responseBody = response.body();
//                                String json = (responseBody == null) ? "" : responseBody.string();
//
//                                if (!response.isSuccessful()) {
//                                    mMainThread.post(() -> {
//                                        callback.onRetrievalFailed("Telegram error! " + json);
//                                    });
//                                    return;
//                                }
//                                if (responseBody != null) {
//                                    mMainThread.post(() -> {
//                                        callback.onMessageRetrieved(json);
//                                    });
//                                }
//                    }
//                });
//            } catch (Exception e) {
//                Log.d("err -->", ""+e.getMessage());
//            }
//        });
//    }
//
////    public void sendDocument(final List<File> data, String caption, Callback<String> callback) {
////        this.mThreadExecutor.submit(() -> {
////            for (File document : data) {
////                telegramClient.sendDocument(document.getAbsolutePath(), caption, new okhttp3.Callback() {
////
////                            @Override
////                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
////                                mMainThread.post(() -> {
////                                    callback.onRetrievalFailed(e.getLocalizedMessage());
////                                });
////                            }
////
////                            @Override
////                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
////
////                                ResponseBody responseBody = response.body();
////                                String json = (responseBody == null) ? "" : responseBody.string();
////
////                                if (!response.isSuccessful()) {
////                                    mMainThread.post(() -> {
////                                        callback.onRetrievalFailed("Telegram error! " + json);
////                                    });
////                                    return;
////                                }
////                                if (responseBody != null) {
////                                    mMainThread.post(() -> {
////                                        callback.onMessageRetrieved(json);
////                                    });
////                                }
////
////                            }
////
////                        }
////                );
////            }
////        });
////    }
//}
