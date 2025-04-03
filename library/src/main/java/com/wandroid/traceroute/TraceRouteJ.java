package com.wandroid.traceroute;

import android.os.Handler;
import android.os.Looper;

public class TraceRouteJ {

    static {
        System.loadLibrary("traceroute");
    }

    private StringBuilder result;
    private com.wandroid.traceroute.TraceRouteCallback callback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void setCallback(TraceRouteCallback callback) {
        this.callback = callback;
    }

//    public void setCallback(SimpleTraceRouteCallback callbackBuilder) {
//        SimpleTraceRouteCallback simpleCallback = new SimpleTraceRouteCallback();
//        //simpleCallback.traceRouteCallback()
//        setCallback(simpleCallback);
//    }

    public void clearResult() {
        result = null;
    }

    public void appendResult(String text) {
        if (result == null) {
            result = new StringBuilder();
        }
        result.append(text);
        if (callback != null) {
            handler.post(() -> callback.onUpdate(text));
        }
    }

    public TraceRouteResult traceRoute(String hostname, boolean async) {
        String[] args = {"traceroute", hostname};
        if (async) {
            new Thread(() -> traceRoute(args), "trace_route_thread").start();
            return null;
        } else {
            return traceRoute(args);
        }
    }

    public TraceRouteResult traceRoute(String[] args) {
        TraceRouteResult traceRouteResult = new TraceRouteResult(-1, "");
        traceRouteResult.setCode(execute(args));
        if (traceRouteResult.getCode() == 0) {
            traceRouteResult.setMessage(result.toString());
            handler.post(() -> callback.onSuccess(traceRouteResult));
        } else {
            traceRouteResult.setMessage("execute traceroute failed.");
            handler.post(() -> callback.onFailed(traceRouteResult.getCode(), traceRouteResult.getMessage()));
        }
        return traceRouteResult;
    }

    private native int execute(String[] args);
}