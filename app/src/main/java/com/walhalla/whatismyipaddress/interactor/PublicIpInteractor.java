package com.walhalla.whatismyipaddress.interactor;

import static com.walhalla.whatismyipaddress.SinglePagination.dec0;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.boilerplate.domain.executor.Executor;
import com.walhalla.boilerplate.domain.executor.MainThread;
import com.walhalla.boilerplate.domain.interactors.base.AbstractInteractor;
import com.walhalla.ui.DLog;

public class PublicIpInteractor extends AbstractInteractor {

    // http://checkip.amazonaws.com
    public static int[] v0 = new int[] {109, 111, 99, 46, 115, 119, 97, 110, 111, 122, 97, 109, 97, 46, 112, 105, 107, 99, 101, 104, 99, 47, 47, 58, 112, 116, 116, 104};



    private final Callback callback;

    public interface Callback {
        void onMessageRetrieved(String message);

        void onRetrievalFailed(String error);
    }

    public PublicIpInteractor(Executor threadExecutor, MainThread mainThread, Callback callback) {
        super(threadExecutor, mainThread);
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            OkHttpClient httpClient = NetworkUtils.makeOkhttp();
            Request request = new Request.Builder()
                    .url(dec0(v0))
                    .method("GET", null)
                    .build();
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            String ip0 = body.string().trim();
            this.callback.onMessageRetrieved(ip0);
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    public void execute() {
        DLog.d("[execute]: mark this interactor as running");
        this.mIsRunning = true;

        // start running this interactor in a background thread
        mThreadExecutor.execute(this);
    }
}
