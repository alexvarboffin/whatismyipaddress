package com.walhalla.whatismyipaddress.features.websniffer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.walhalla.whatismyipaddress.R;

public class TextFragment extends Fragment {
    private TextView textViewResult;

    public static TextFragment newInstance(String result) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString("result", result);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.websniffer_fragment_text, container, false);
        textViewResult = view.findViewById(R.id.textViewResult);

        Bundle args = getArguments();
        if (args != null) {
            String result = args.getString("result");
            textViewResult.setText(result);
        }

        return view;
    }
}
