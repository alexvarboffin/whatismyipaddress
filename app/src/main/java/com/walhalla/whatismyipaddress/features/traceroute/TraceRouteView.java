package com.walhalla.whatismyipaddress.features.traceroute;

import java.util.List;

public interface TraceRouteView {
    void showTraceRouteResults(List<String> results);
    void showError(String errorMessage);
}
