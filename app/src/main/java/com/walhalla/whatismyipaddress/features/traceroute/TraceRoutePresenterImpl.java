package com.walhalla.whatismyipaddress.features.traceroute;

import com.wandroid.traceroute.TraceRouteModelImpl;

import java.util.List;

public class TraceRoutePresenterImpl implements TraceRoutePresenter {
    private final TraceRouteModel model;
    private final TraceRouteView view;

    public TraceRoutePresenterImpl(TraceRouteView view) {
        this.view = view;
        this.model = new TraceRouteModelImpl(this);
    }

    @Override
    public void startTraceRoute(String host) {
        model.startTraceRoute(host);
    }

    @Override
    public void onTraceRouteSuccess(List<String> results) {
        view.showTraceRouteResults(results);
    }

    @Override
    public void onTraceRouteError(String errorMessage) {
        view.showError(errorMessage);
    }
}
