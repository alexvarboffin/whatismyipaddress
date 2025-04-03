package com.walhalla.whatismyipaddress.terms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.databinding.FragmentTerms0Binding;

public class TermsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private ITerms callback;

    private FragmentTerms0Binding binding;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        callback.isTermsAccepted(isChecked);
    }

    public interface ITerms {
        void isTermsAccepted(boolean b);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTerms0Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.checkboxAccept.setOnCheckedChangeListener(this);
        //WebView webView = rootView.findViewById(R.id.webview);
        //webView.loadDataWithBaseURL(null, loadTermsText(), "text/html", "UTF-8", null);
       binding.terms.setText(replacePlaceholders(AssetUtils.loadFromAsset(getActivity(), "terms_of_service.txt")));
    }


    private String replacePlaceholders(String termsText) {
        String appName = getString(R.string.app_name);
        String publisherName = getString(R.string.play_google_pub);

        // Replace placeholders with actual values
        termsText = termsText.replace("%app%", appName);
        termsText = termsText.replace("%dev%", publisherName);

        return termsText;
    }

    public boolean isTermsAccepted() {
        return binding.checkboxAccept.isChecked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ITerms) {
            callback = (ITerms) context;
        } else {
            throw new RuntimeException(context + " must implement ITerms");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
