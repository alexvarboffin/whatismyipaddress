package com.walhalla.whatismyipaddress.ipcalculator;

import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;

import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;

import com.walhalla.domain.interactors.AdvertInteractor;
import com.walhalla.ui.DLog;

import com.walhalla.whatismyipaddress.TApp;
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Создание корневого контейнера LinearLayout
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // Создание ScrollView
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Добавление ScrollView в корневой LinearLayout
        rootLayout.addView(scrollView);

        // Создание контента для прокручивающейся активности
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        // Добавление текстовых элементов в контент
        for (int i = 1; i <= 200; i++) {
            TextView textView = new TextView(this);
            textView.setText("@" + i);
            contentLayout.addView(textView);
        }
        scrollView.addView(contentLayout);
        setContentView(rootLayout);

        ContentFrameLayout content = findViewById(android.R.id.content);
        DLog.d("@@@@@@@@@"+content.getClass().getSimpleName());

        AdvertInteractorImpl o = new AdvertInteractorImpl(
                BackgroundExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                TApp.repository
        );
        o.selectView(content, new AdvertInteractor.Callback<View>() {
            @Override
            public void onMessageRetrieved(int id, View message) {

                ViewGroup content = findViewById(android.R.id.content);

                if (content != null) {
                    try {
                        //content.removeView(message);
                        if (message.getParent() != null) {
                            ((ViewGroup) message.getParent()).removeView(message);
                        }
                        content.setPadding(0, 0, 0, 50);

                        //ViewGroup.LayoutParams mm = content.getLayoutParams();
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
                        message.setLayoutParams(params);
                        content.addView(message);
                    } catch (Exception e) {
                        DLog.d("onMessageRetrieved: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onRetrievalFailed(String error) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}
