package com.walhalla.whatismyipaddress.features.traceroute;

import java.util.List;

public interface TraceRoutePresenter {
    void startTraceRoute(String host);
    void onTraceRouteSuccess(List<String> results);
    void onTraceRouteError(String errorMessage);
}
