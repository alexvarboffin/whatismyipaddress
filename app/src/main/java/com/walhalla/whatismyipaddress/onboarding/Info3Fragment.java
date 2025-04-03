package com.walhalla.whatismyipaddress.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.FragmentInfo3Binding;

public class Info3Fragment extends Fragment {

    private FragmentInfo3Binding binding;
    private Animation smallToBigAnimation;

    public Info3Fragment() {
    }

    public static Info3Fragment newInstance() {
        return new Info3Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smallToBigAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.small_to_big);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfo3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.imageView.startAnimation(smallToBigAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

