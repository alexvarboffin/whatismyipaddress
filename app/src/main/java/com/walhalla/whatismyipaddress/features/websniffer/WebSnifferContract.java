package com.walhalla.whatismyipaddress.features.websniffer;

public interface WebSnifferContract {
    interface View {
        void showValidationError(String errorMessage);
        void showResult(RespWrapper result);
    }

    interface Presenter {
        void submitButtonClicked(String url, String requestType, String httpVersion, String userAgent);
    }
}