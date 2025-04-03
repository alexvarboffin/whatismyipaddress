package com.walhalla.netdiscover;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.activities.SpeedTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Graph extends AppCompatActivity {

    private final Handler mHandler = new Handler();

    private Double startRx = 0.0;
    private Double startTx = 0.0;
    int graphLastXValue = 0;
    private LineChart downChart, upChart;
    private final ArrayList<String> rxBytesArray = new ArrayList<>();
    private final ArrayList<String> txBytesArray = new ArrayList<>();
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        back = findViewById(R.id.back);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        downChart = findViewById(R.id.downChart);
        upChart = findViewById(R.id.upChart);

        startRx = TrafficStats.getTotalRxBytes() / 1024.0;
        startTx = TrafficStats.getTotalTxBytes() / 1024.0;

        showGraphDown();
        showGraphUp();

        if (startRx == TrafficStats.UNSUPPORTED || startTx == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(Graph.this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 50);
        }

        findViewById(R.id.speedTest).setOnClickListener(view ->
                startActivity(new Intent(Graph.this, SpeedTest.class)));

        back.setOnClickListener(view -> Graph.super.onBackPressed());
    }

    private void showGraphDown() {

        // no description text
        downChart.getDescription().setEnabled(false);

        // enable touch gestures
        downChart.setTouchEnabled(true);

        downChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        downChart.setDragEnabled(true);
        downChart.setScaleEnabled(true);
        downChart.setDrawGridBackground(false);
        downChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        downChart.setBackgroundColor(Color.WHITE);
        downChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = downChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = downChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(0f);
        xAxis.setTextColor(getResources().getColor(R.color.transparent));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(false);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = downChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(14f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = downChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void showGraphUp() {

        // no description text
        upChart.getDescription().setEnabled(false);

        // enable touch gestures
        upChart.setTouchEnabled(true);

        upChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        upChart.setDragEnabled(true);
        upChart.setScaleEnabled(true);
        upChart.setDrawGridBackground(false);
        upChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        upChart.setBackgroundColor(Color.WHITE);
        upChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = upChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = upChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(0f);
        xAxis.setTextColor(getResources().getColor(R.color.transparent));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(false);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = upChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(14f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = upChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setDataDown0(int count, ArrayList<String> range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int x = 0; x < count; x++) {
            String oldValue = range.get(x);
            float newValue = 0.00f;
            try {
                newValue = Float.parseFloat(oldValue);
            } catch (NumberFormatException e) {
                DLog.handleException(e);
            }
            values.add(new Entry(x, newValue));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setDrawFilled(true);
        set1.setFillColor(getResources().getColor(R.color.colorPrimaryLite));
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(16f);

        // set data
        downChart.setData(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            downChart.setElevation(2);
        }

        if (rxBytesArray.size() >= 19) {
            downChart.postDelayed(() -> downChart.moveViewTo(19, 0, YAxis.AxisDependency.RIGHT), 1000);
        } else {
            downChart.postDelayed(() -> downChart.moveViewTo(rxBytesArray.size() - 1, 0, YAxis.AxisDependency.RIGHT), 1000);
        }

        graphLastXValue = graphLastXValue + 1;
    }

    private void setDataUp0(int count, ArrayList<String> range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int x = 0; x < count; x++) {
            String oldValue = range.get(x);
            float newValue = 0.00f;
            try {
                newValue = Float.parseFloat(oldValue);
            } catch (NumberFormatException e) {
                DLog.handleException(e);
            }
            values.add(new Entry(x, newValue));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.error));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setDrawFilled(true);
        set1.setFillColor(getResources().getColor(R.color.errorLite));
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(16f);

        // set data
        upChart.setData(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            upChart.setElevation(2);
        }

        if (txBytesArray.size() >= 19) {
            upChart.postDelayed(() -> upChart.moveViewTo(19, 0, YAxis.AxisDependency.RIGHT), 1000);
        } else {
            upChart.postDelayed(() -> upChart.moveViewTo(txBytesArray.size() - 1, 0, YAxis.AxisDependency.RIGHT), 1000);
        }

        graphLastXValue = graphLastXValue + 1;
    }

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            Double rxBytes = ((TrafficStats.getTotalRxBytes() / 1024.0) - startRx);
            startRx = startRx + rxBytes;
            Double txBytes = ((TrafficStats.getTotalTxBytes() / 1024.0) - startTx);
            startTx = startTx + txBytes;

            if (rxBytesArray.size() >= 20) {
                rxBytesArray.remove(0);
                rxBytesArray.add(String.format(Locale.US, "%.2f", rxBytes));
            } else {
                rxBytesArray.add(String.format(Locale.US, "%.2f", rxBytes));
            }
            if (txBytesArray.size() >= 20) {
                txBytesArray.remove(0);
                txBytesArray.add(String.format(Locale.US, "%.2f", txBytes));
            } else {
                txBytesArray.add(String.format(Locale.US, "%.2f", txBytes));
            }

            setDataDown0(rxBytesArray.size(), rxBytesArray);//<=====
            setDataUp0(rxBytesArray.size(), txBytesArray);

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

}
