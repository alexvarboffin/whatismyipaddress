package com.walhalla.whatismyipaddress.interactor;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.walhalla.boilerplate.domain.executor.Executor;
import com.walhalla.boilerplate.domain.executor.MainThread;
import com.walhalla.boilerplate.domain.interactors.base.AbstractInteractor;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.domain.IpInfoRepositoryExternal;
import com.walhalla.whatismyipaddress.helper.IPInfoRemote;

public class RemoteIPInfoInteractor extends AbstractInteractor {

    private final Callback mCallback;
    private final Context context;
    private final String ua;

    public interface Callback {
        void onMessageRetrieved(boolean success, IPInfoRemote data);
    }

    public RemoteIPInfoInteractor(Context context, Executor threadExecutor, MainThread mainThread, Callback callback) {
        super(threadExecutor, mainThread);
        this.mCallback = callback;
        this.context = context;
        String raw0 = new WebView(context).getSettings().getUserAgentString();
        ua = raw0.replace("; wv)", ")");
    }


    //cancel this.mHelper.onMessageRetrieved(false, null);

    @Override
    public void run() {
        IpInfoRepositoryExternal repositoryExternal = new IpInfoRepositoryExternal();
        IPInfoRemote result = repositoryExternal.getRemoteInfo(context, ua);
        this.mCallback.onMessageRetrieved(result != null, result);
    }

    public void execute() {
        DLog.d("[execute]: mark this interactor as running");
        this.mIsRunning = true;

        // start running this interactor in a background thread
        mThreadExecutor.execute(this);
    }
}

