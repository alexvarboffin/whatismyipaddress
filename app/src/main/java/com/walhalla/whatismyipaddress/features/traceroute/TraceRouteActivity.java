package com.walhalla.whatismyipaddress.features.traceroute;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.ActivityTracerouteBinding;

import java.util.List;

public class TraceRouteActivity extends AppCompatActivity implements TraceRouteView {

    private TraceRoutePresenter presenter;

    public ActivityTracerouteBinding getBinding() {
        return binding;
    }

    private ActivityTracerouteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTracerouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new TraceRoutePresenterImpl(this);
        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            onTraceRouteButtonClicked("www.google.com");
        });
    }

    // Метод для запуска трассировки маршрута по нажатию кнопки
    public void onTraceRouteButtonClicked(String host) {
        presenter.startTraceRoute(host);
    }

    @Override
    public void showError(String errorMessage) {
        // Отобразить сообщение об ошибке в пользовательском интерфейсе
    }

    // Инициализация презентера
    public void setPresenter(TraceRoutePresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void showTraceRouteResults(List<String> results) {
        // Отобразить результаты трассировки маршрута в пользовательском интерфейсе
    }

}
