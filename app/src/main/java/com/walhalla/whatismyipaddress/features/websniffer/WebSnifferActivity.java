package com.walhalla.whatismyipaddress.features.websniffer;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.ActivityWebSnifferBinding;

import java.util.Map;

public class WebSnifferActivity extends AppCompatActivity implements WebSnifferContract.View {

    private WebSnifferContract.Presenter presenter;

    private String textResult;
    private String htmlResult;


    private ActivityWebSnifferBinding binding;
    private String TAG_XFrameOptions = "X-Frame-Options";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebSnifferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Устанавливаем начальные значения для UI компонентов
        View aa = binding.requestTypeRadioGroup.getChildAt(0);
        if (aa instanceof RadioButton) {
            ((RadioButton) aa).setChecked(true);
        }

        // Инициализируем Presenter
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new WebSnifferPresenter(handler, this);

        // Обработчик кнопки Submit
        binding.submitButton.setOnClickListener(v -> {
            showProgress();
            String url = binding.urlEditText.getText().toString().trim();
            int selectedRadioButtonId = binding.requestTypeRadioGroup.getCheckedRadioButtonId();
            String requestType = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();
            String httpVersion = binding.httpVersionSpinner.getSelectedItem().toString();
            String userAgent = binding.userAgentSpinner.getSelectedItem().toString();

            binding.responseHeaderLabel.setVisibility(View.GONE);
            binding.requestHeaderLabel.setVisibility(View.GONE);
            binding.control.setVisibility(View.GONE);
            presenter.submitButtonClicked(url, requestType, httpVersion, userAgent);
        });

        binding.back.setOnClickListener(view -> super.onBackPressed());

        makeWV(binding.webViewResult);

        binding.textButton.setOnClickListener(v -> showTextResult());

        binding.webButton.setOnClickListener(v -> showWebResult());

        // Проверьте наличие результатов и отобразите их
        if (textResult != null) {
            showTextResult();
        } else if (htmlResult != null) {
            showWebResult();
        }
    }


    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    private void makeWV(WebView mWView) {
        WebSettings settings = mWView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        //webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        String tmp = mWView.getSettings().getUserAgentString();
        mWView.getSettings().setUserAgentString(tmp.replace("; wv)", ")"));
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWView, true);
        }
        if (BuildConfig.DEBUG) {
            mWView.setBackgroundColor(Color.parseColor("#80000000"));
        }
        mWView.setWebChromeClient(new WebChromeClient());
        // mWebView.addJavascriptInterface(new MyJavascriptInterface(SubdomainActivity.this, mWebView), "Client");
        mWView.setWebViewClient(new CustomWebViewClient(this));
    }

    private void showTextResult() {
        binding.textContainer.setVisibility(View.VISIBLE);
        binding.webContainer.setVisibility(View.GONE);

        if (textResult != null) {
            binding.textViewResult.setText(textResult);
        }
    }


    private void showWebResult() {
        binding.textContainer.setVisibility(View.GONE);
        binding.webContainer.setVisibility(View.VISIBLE);
        if (htmlResult != null) {
            binding.webViewResult.loadDataWithBaseURL(null, htmlResult, "text/html", "UTF-8", null);
        }

        //scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void showValidationError(String errorMessage) {
        hideProgress();
        // Отображение сообщения об ошибке валидации
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(RespWrapper result) {
        hideProgress();
        // Отображение результатов запроса
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Web Sniffer Result");
//        builder.setMessage(result);
//        builder.setPositiveButton("OK", null);
//        builder.show();
        updateResults(result);
    }

    private void updateResults(RespWrapper result) {

        binding.responseHeaderLabel.setVisibility(View.VISIBLE);
        binding.requestHeaderLabel.setVisibility(View.VISIBLE);

        this.textResult = result.html;
        this.htmlResult = result.html;

        if (binding.textContainer != null && binding.webContainer != null) {
            binding.control.setVisibility(View.VISIBLE);
            if (binding.textContainer.getVisibility() == View.VISIBLE) {
                showTextResult();
            } else if (binding.webContainer.getVisibility() == View.VISIBLE) {
                showWebResult();
            }
        }

        binding.domainInfoLabel.setText("" + result.host + " " + result.ip);
        String m0 = String.format(getString(R.string.request_header_text), result.host, result.port,
                "\nResponse Code: " + result.responseCode);

        StringBuilder sb1 = new StringBuilder();
        //StringBuilder sb2 = new StringBuilder();
        SpannableStringBuilder sb2 = new SpannableStringBuilder();
        sb1.append(m0);

        for (Map.Entry<String, String> entry : result.map0.entrySet()) {
            sb1.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        String xFrameOptions = null;
        //X-Frame-Options: deny
        //X-Frame-Options: sameorigin
        //allow-from localhost:4321

        for (Map.Entry<String, String> entry : result.respHeaders.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (TAG_XFrameOptions.equals(key)) {
                xFrameOptions = value;
                //X-Frame-Options: deny
//                X-Frame-Options: sameorigin
//                X-Frame-Options: allow-from https://www.example.com/


                Spannable none = new SpannableStringBuilder(key + ": ");
                Spannable spanValue = new SpannableStringBuilder(xFrameOptions);

                if (xFrameOptions.equalsIgnoreCase("sameorigin")
                        || xFrameOptions.equalsIgnoreCase("deny")) {
                    none.setSpan(new ForegroundColorSpan(Color.BLUE), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    none.setSpan(new ForegroundColorSpan(Color.RED), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.RED), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                sb2.append(none).append(spanValue).append("\n");
                DLog.d("" + xFrameOptions);
            } else if ("X-XSS-Protection".equals(key)) {

                //X-XSS-Protection: 1; mode=block

                Spannable none = new SpannableStringBuilder(key + ": ");
                Spannable spanValue = new SpannableStringBuilder(value);

                if (value.equalsIgnoreCase("1; mode=block")) {
                    none.setSpan(new ForegroundColorSpan(Color.BLUE), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    none.setSpan(new ForegroundColorSpan(Color.RED), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.RED), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                sb2.append(none).append(spanValue).append("\n");
                DLog.d("" + value);
            } else if ("Content-Security-Policy".equals(key)) {
//                Content-Security-Policy: default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'none'
//                frame-ancestors 'none' и X-Frame-Options: deny
//                frame-ancestors 'self' и X-Frame-Options: sameorigin
//                frame-ancestors localhost:4321 и X-Frame-Options: allow-from localhost:4321
//                script-src 'self' без 'unsafe-inline' и X-XSS-Protection: 1

                Spannable none = new SpannableStringBuilder(key + ": ");
                Spannable spanValue = new SpannableStringBuilder(value);

                if (value.equalsIgnoreCase("frame-ancestors 'self'")) {
                    none.setSpan(new ForegroundColorSpan(Color.BLUE), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    none.setSpan(new ForegroundColorSpan(Color.RED), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.RED), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                sb2.append(none).append(spanValue).append("\n");
                DLog.d("" + value);
            } else if ("X-Content-Type-Options".equals(key)) {

                //X-Content-Type-Options: nosniff

                Spannable none = new SpannableStringBuilder(key + ": ");
                Spannable spanValue = new SpannableStringBuilder(value);

                if (value.equalsIgnoreCase("nosniff")) {
                    none.setSpan(new ForegroundColorSpan(Color.BLUE), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    none.setSpan(new ForegroundColorSpan(Color.RED), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanValue.setSpan(new ForegroundColorSpan(Color.RED), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                sb2.append(none).append(spanValue).append("\n");
                DLog.d("" + value);
            } else if ("Strict-Transport-Security".equals(key)) {

                //Strict-Transport-Security: max-age=15552001; includeSubDomains; preload; redirectHttpToHttps=true

                Spannable none = new SpannableStringBuilder(key + ": ");
                Spannable spanValue = new SpannableStringBuilder(value);

                none.setSpan(new ForegroundColorSpan(Color.BLUE), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                spanValue.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanValue.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                sb2.append(none).append(spanValue).append("\n");
                DLog.d("" + value);
            } else {
                sb2.append(key).append(": ").append(value).append("\n");
            }
        }

        if (TextUtils.isEmpty(xFrameOptions)) {
            Spannable none = new SpannableStringBuilder("Missing header");
            none.setSpan(new ForegroundColorSpan(Color.RED), 0, none.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            sb2.append(TAG_XFrameOptions).append(": ").append(none).append("\n");
        }
        binding.requestHeaderText.setText(sb1.toString());
        binding.responseHeaderText.setText(sb2);

        binding.domainNameText.setText(result.host);
        binding.ipAddressText.setText(result.ip);
        //nnn
    }

    private void showProgress() {
        binding.submitButton.setEnabled(false);
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.submitButton.setEnabled(true);
        binding.spinKit.setVisibility(View.GONE);
    }

    private static class CustomWebViewClient extends WebViewClient {
        public CustomWebViewClient(WebSnifferActivity webSnifferActivity) {
        }
    }
}

