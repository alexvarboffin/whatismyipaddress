package com.wandroid.traceroute

import com.walhalla.ui.DLog
import com.walhalla.whatismyipaddress.features.traceroute.TraceRouteModel
import com.walhalla.whatismyipaddress.features.traceroute.TraceRoutePresenter

// Модель (Model)
class TraceRouteModelImpl(private val presenter: TraceRoutePresenter) : TraceRouteModel {

    override fun startTraceRoute(host: String) {
        try {
            val results = performTraceRoute(host)
            presenter.onTraceRouteSuccess(results)
        } catch (e: Exception) {
            presenter.onTraceRouteError(e.message)
        }
    }

    private fun performTraceRoute(host: String): List<String> {
        val results: List<String> = ArrayList()

//        val traceRoute = TraceRouteJ()
//        traceRoute.setCallback(object : TraceRouteCallback {
//            override fun onSuccess(traceRouteResult: TraceRouteResult) {
//                DLog.d("@Success@$traceRouteResult")
//            }
//
//            override fun onUpdate(text: String) {
//                DLog.d("@Update@$text")
//            }
//
//            override fun onFailed(code: Int, reason: String) {
//                DLog.d("@@@@@@@@@@$reason")
//            }
//        })
//        val aa = traceRoute.traceRoute(host, true)
//
//
//        //        new TraceRouteJ().setCallback {
////            success { Log.d("tag", "\ntraceroute finish") }
////            update { text -> Log.d("tag", text) }
////            failed { code, reason -> Log.d("tag", """\ntraceroute failed.code:$code, reason:$reason""") }
////        }
        return results
    }
}