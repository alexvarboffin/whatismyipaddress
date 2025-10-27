package com.walhalla.whatismyipaddress.features.base

import android.os.Handler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BasePresenter(@JvmField protected val handler: Handler?) {
    @JvmField
    protected val executor: ExecutorService? = Executors.newSingleThreadExecutor()
}
