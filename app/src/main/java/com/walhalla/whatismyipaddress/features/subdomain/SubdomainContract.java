package com.walhalla.whatismyipaddress.features.subdomain;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

import java.util.List;

public interface SubdomainContract {
    interface View {
        void showValidationError(String errorMessage);

        void successSubdomainsResult(List<ViewModel> dataModels);

        void hideProgress();

        void showProgress();

        void init(String ip);
    }

    interface Presenter {
        void submitButtonClicked(String url, int requestType);
    }
}