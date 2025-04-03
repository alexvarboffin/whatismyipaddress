package com.walhalla.whatismyipaddress.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.FragmentInfo1Binding;

public class Info1Fragment extends Fragment {

    private FragmentInfo1Binding binding;
    private Animation smallToBigAnimation;
    private Animation nothingToComeAnimation;

    public static Info1Fragment newInstance() {
        return new Info1Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smallToBigAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.small_to_big);
        nothingToComeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.nosingtocome);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInfo1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.imageView.startAnimation(smallToBigAnimation);
        binding.textView.startAnimation(nothingToComeAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
