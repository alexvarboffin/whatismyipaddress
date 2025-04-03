package com.walhalla.whatismyipaddress.features.websniffer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.walhalla.whatismyipaddress.R;

public class WebFragment extends Fragment {
    private static final String TAG_RESULT = WebFragment.class.getSimpleName();
    private WebView webViewResult;

    public static WebFragment newInstance(String result) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(TAG_RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.websniffer_fragment_web, container, false);
        webViewResult = view.findViewById(R.id.webViewResult);

        Bundle args = getArguments();
        if (args != null) {
            String result = args.getString(TAG_RESULT);
            webViewResult.loadData(result, "text/html", null);
        }

        return view;
    }
}

