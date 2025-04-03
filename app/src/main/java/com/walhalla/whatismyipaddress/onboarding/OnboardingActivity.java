package com.walhalla.whatismyipaddress.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.ActivityOnboardBinding;

import com.walhalla.whatismyipaddress.terms.PrivacyPolicyActivity;
import com.walhalla.whatismyipaddress.terms.TermsFragment;

import com.walhalla.whatismyipaddress.ui.activities.Main.BottomHolder;


public class OnboardingActivity extends AppCompatActivity
        implements FragmentFirebasePolicy.IPrivacyPolicy, TermsFragment.ITerms {

    private int currentPage = 0;
    private ActivityOnboardBinding binding;
    private OnboardAdapter onboardAdapter;
    private OnboardingManager onboardingManager;

    private boolean areTermsAccepted;
    private Boolean isFirebaseAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingManager = new OnboardingManager(this);
        if (onboardingManager.isOnboarding()) {
            startActivity(new Intent(this, BottomHolder.class));
            finish();
            return;
        }

        binding = ActivityOnboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.viewPager.setUserInputEnabled(false);
        onboardAdapter = new OnboardAdapter(this);
        binding.viewPager.setAdapter(onboardAdapter);

        binding.btnNext.setOnClickListener(v -> handleNextButtonClick());

        binding.flexibleIndicator.initViewPager(binding.viewPager);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateNextButtonText();
            }
        });
    }

    private void handleNextButtonClick() {
        switch (currentPage) {
            case 0:
                binding.viewPager.setCurrentItem(currentPage + 1);
                break;
            case 1:
                if (areTermsAccepted) {
                    binding.viewPager.setCurrentItem(currentPage + 1);
                } else {
                    showSnackbar(R.string.error_msg_terms);
                }
                break;
            case 2:
                if (isFirebaseAccepted != null) {
                    binding.viewPager.setCurrentItem(currentPage + 1);
                } else {
                    showSnackbar(R.string.error_msg_firebase);
                }
                break;
            case 3:
                onboardingManager.isOnboarding(true);
                startActivity(new Intent(this, BottomHolder.class));
                finish();
                break;
        }
    }

    private void updateNextButtonText() {
        if (currentPage == 3) {
            binding.btnNext.setText(R.string.onboard_action_finish);
        } else {
            binding.btnNext.setText(R.string.onboard_action_next);
        }
    }

    private void showSnackbar(int messageResId) {
        Snackbar.make(binding.coordinator, messageResId, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.snackbar_background))
                .setAction(android.R.string.ok, null)
                .show();
    }

    @Override
    public void launchPrivacyPolicy() {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    @Override
    public void isFirebaseAccepted(boolean accepted) {
        isFirebaseAccepted = accepted;
    }

    @Override
    public void isTermsAccepted(boolean accepted) {
        areTermsAccepted = accepted;
    }

    public static class OnboardAdapter extends FragmentStateAdapter {

        public OnboardAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return Info1Fragment.newInstance();
                case 1:
                    return new TermsFragment();
                case 2:
                    return FragmentFirebasePolicy.newInstance();
                case 3:
                    return Info3Fragment.newInstance();
                default:
                    return Info1Fragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
