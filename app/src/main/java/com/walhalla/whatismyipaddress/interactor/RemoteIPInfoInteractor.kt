package com.walhalla.whatismyipaddress.interactor

import android.content.Context
import android.webkit.WebView
import com.walhalla.boilerplate.domain.executor.Executor
import com.walhalla.boilerplate.domain.executor.MainThread
import com.walhalla.boilerplate.domain.interactors.base.AbstractInteractor
import com.walhalla.ui.DLog.d
import com.walhalla.whatismyipaddress.domain.IpInfoRepositoryExternal
import com.walhalla.whatismyipaddress.helper.IPInfoRemote

class RemoteIPInfoInteractor(
    private val context: Context,
    threadExecutor: Executor,
    mainThread: MainThread,
    private val mCallback: Callback
) : AbstractInteractor(threadExecutor, mainThread) {
    private val ua: String

    interface Callback {
        fun onMessageRetrieved(success: Boolean, data: IPInfoRemote?)
    }

    init {
        val raw0 = WebView(context).settings.userAgentString
        ua = raw0.replace("; wv)", ")")
    }


    //cancel this.mHelper.onMessageRetrieved(false, null);
    public override fun run() {
        val repositoryExternal = IpInfoRepositoryExternal()
        val result = repositoryExternal.getRemoteInfo(context, ua)
        this.mCallback.onMessageRetrieved(result != null, result)
    }

    fun execute() {
        d("[execute]: mark this interactor as running")
        //this.mIsRunning = true

        // start running this interactor in a background thread
        mThreadExecutor.execute(this)
    }
}

