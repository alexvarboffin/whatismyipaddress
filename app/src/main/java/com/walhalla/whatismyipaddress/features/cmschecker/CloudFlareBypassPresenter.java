//package com.walhalla.whatismyipaddress.features.cmschecker;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Handler;
//
//import androidx.preference.PreferenceManager;
//
//import com.walhalla.ui.DLog;
//import com.walhalla.whatismyipaddress.R;
//import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
//import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
//import com.walhalla.whatismyipaddress.features.base.BasePresenter;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.Timer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class CloudFlareBypassPresenter extends BasePresenter {
//
//
//    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.63 Safari/537.31";
//    private static final String KEY_RESULT = "result";
//
//
//    private final String internet_connectivity_problem0;
//
//
//    // https://whatcms.org/APIEndpoint?key=745aaac9fc2d1acc0e20330469b1db3979be347b5542b3b5b790b42d10cb68cac78c2f&url=
//    public static int[] v0 = new int[]{61, 108, 114, 117, 38, 102, 50, 99, 56, 55, 99, 97, 99, 56, 54, 98, 99, 48, 49, 100, 50, 52, 98, 48, 57, 55, 98, 53, 98, 51, 98, 50, 52, 53, 53, 98, 55, 52, 51, 101, 98, 57, 55, 57, 51, 98, 100, 49, 98, 57, 54, 52, 48, 51, 51, 48, 50, 101, 48, 99, 99, 97, 49, 100, 50, 99, 102, 57, 99, 97, 97, 97, 53, 52, 55, 61, 121, 101, 107, 63, 116, 110, 105, 111, 112, 100, 110, 69, 73, 80, 65, 47, 103, 114, 111, 46, 115, 109, 99, 116, 97, 104, 119, 47, 47, 58, 115, 112, 116, 116, 104};
//
//    private static final String KEY_HOST = "key_CheckhostPresenter_ip0";
//    private static final int THREAD_COUNT = 4;
//
//    private final String NO_DATA_FOUND;
//    private final SharedPreferences preferences;
//    List<ViewModel> models = new ArrayList<>();
//
//    private final ClView view;
//
//    String ip;
//
//    private List<String> name, values;
//    private final String ERROR_MESSAGE = "ErrorMessage";
//    private Timer timer;
//    private final OkHttpClient httpClient = NetworkUtils.makeOkhttp();
//
//    public CloudFlareBypassPresenter(Context context, ClView view, Handler handler) {
//        super(handler);
//        this.view = view;
//        this.NO_DATA_FOUND = context.getString(R.string.no_data_found);
//        this.internet_connectivity_problem0 = context.getString(R.string.internet_connectivity_problem);
//        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
//    }
//
//
//    enum checktype {
//        ping, http, tcp, dns, udp
//    }
//
//    public void bypassCloudFlare(String site) {
//
//        try {
//            view.showProgress();
//            models = new ArrayList<>();
//            if (timer != null) {
//                timer.cancel();
//            }
//
//            executor.execute(() -> {
//                try {
//                    InetAddress ip = InetAddress.getByName(site);
//                    String ipAddress = ip.getHostAddress();
//                    SingleItem mm1 = new SingleItem("CloudFlare IP: " + ipAddress + "\n");
//
//                    String url = "https://dns-api.org/NS/" + site;
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .header("User-Agent", USER_AGENT)
//                            .build();
//
//                    Response response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        String networkResp = response.body().string();
//                        DLog.d("@@@@@@@@@@" + networkResp);
//
//                        Pattern pattern = Pattern.compile("\"value\": \"(.*?)\"");
//                        Matcher matcher = pattern.matcher(networkResp);
//                        Set<String> seen = new HashSet<>();
//                        while (matcher.find()) {
//                            String ns = matcher.group(1);
//                            if (seen.add(ns)) {
//                                SingleItem item = new SingleItem("NS: " + ns);
//                                models.add(item);
//                            }
//                        }
//
//
//                        try {
//                            url = "http://www.crimeflare.us/cgi-bin/cfsearch.cgi";
//                            RequestBody formBody = new FormBody.Builder()
//                                    .add("cfS", site)
//                                    .build();
//                            Request request0 = new Request.Builder()
//                                    .url(url)
//                                    .header("User-Agent", USER_AGENT)
//                                    .post(formBody)
//                                    .build();
//                            String response0 = sendPostRequest(url, request0, formBody);
//                            pattern = Pattern.compile("\">(.*?)</a>&nbsp");
//                            matcher = pattern.matcher(response0);
//
//                            if (matcher.find()) {
//                                String realIp = matcher.group(1);
//                                SingleItem singleItem = new SingleItem("Real IP: " + realIp);
//                                models.add(singleItem);
//                                ipAddress = realIp;
//                            } else if (response0.contains("not CloudFlare-user nameservers")) {
//                                SingleItem singleItem = new SingleItem("These Are Not CloudFlare-user Nameservers !!");
//                                models.add(singleItem);
//                                SingleItem singleItem1 = new SingleItem("This Website Not Using CloudFlare Protection");
//                                models.add(singleItem1);
//                            } else if (response0.contains("No direct-connect IP address was found for this domain")) {
//                                SingleItem item = new SingleItem("No Direct Connect IP Address Was Found For This Domain");
//                                models.add(item);
//                            } else {
//                                SingleItem aProblem = new SingleItem("There Is A Problem");
//                                models.add(aProblem);
//                                SingleItem singleItem = new SingleItem("1. Checking The Connection");
//                                models.add(singleItem);
//                                SingleItem singleItem1 = new SingleItem("2. Enter Website Without HTTP/HTTPs");
//                                models.add(singleItem1);
//                                SingleItem singleItem2 = new SingleItem("3. Check If Website Working");
//                                models.add(singleItem2);
//                            }
//
//                            url = "http://ipinfo.io/" + ipAddress + "/json";
//                            String response1 = sendGetRequest(url);
//                            printIpInfo(response1, models);
//
//                            JSONObject object = new JSONObject(networkResp);
////                            if (object.has("error")) {
////                                //"limit_exceeded"
////                                showError(object.getString("error"));
////                            } else
//                        } catch (Exception e) {
//                            showError(e.getLocalizedMessage());
//                        }
//                    }
//
//                    preferences.edit().putString(KEY_HOST, site).apply();
//
//                } catch (Exception e) {
//                    //UnknownHostException
//                    DLog.handleException(e);
//                    if (e instanceof UnknownHostException) {
//                        showError(internet_connectivity_problem0);
//                    } else {
//                        showError(NO_DATA_FOUND);
//                    }
//                }
//
//                if (!models.isEmpty()) {
//                    handler.post(() -> {
//                        view.hideProgress();
//                        List<ViewModel> aa = new ArrayList<>(models);
//                        view.successResult(aa);
//                    });
//                }
//
//            });
//        } catch (Exception e) {
//            DLog.d("<aaa> " + e.getLocalizedMessage());
//        }
//    }
//
//    private static void printIpInfo(String response, List<ViewModel> models) {
//        Pattern pattern;
//        Matcher matcher;
//        pattern = Pattern.compile("\"hostname\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem singleItem = new SingleItem("Hostname: " + matcher.group(1));
//            models.add(singleItem);
//        }
//        pattern = Pattern.compile("\"city\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem mm1 = new SingleItem("City: " + matcher.group(1));
//            models.add(mm1);
//        }
//        pattern = Pattern.compile("\"region\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem mm1 = new SingleItem("Region: " + matcher.group(1));
//            models.add(mm1);
//        }
//        pattern = Pattern.compile("\"country\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem mm1 = new SingleItem("Country: " + matcher.group(1));
//            models.add(mm1);
//        }
//        pattern = Pattern.compile("\"loc\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem mm1 = new SingleItem("Location: " + matcher.group(1));
//            models.add(mm1);
//        }
//        pattern = Pattern.compile("\"org\": \"(.*?)\"");
//        matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            SingleItem mm1 = new SingleItem("Organization: " + matcher.group(1));
//            models.add(mm1);
//        }
//    }
//
//    private String sendGetRequest(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .header("User-Agent", USER_AGENT)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            return response.body().string();
//        }
//    }
//
//    private String sendPostRequest(String url, Request request0, RequestBody formBody) throws IOException {
//        try (Response response = client.newCall(request0).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            return response.body().string();
//        }
//    }
//
//    public String dec0(int[] intArray) {
//        char[] strArray = new char[intArray.length];
//        for (int i = 0; i < intArray.length; i++) {
//            strArray[i] = (char) intArray[i];
//        }
//        return new StringBuilder((String.valueOf(strArray))).reverse().toString();
//    }
//
//    private void showError(String err) {
//        List<ViewModel> aa = new ArrayList<>();
//        aa.add(new SingleItem(err, R.color.colorPrimaryDark));
//        handler.post(() -> {
//            view.hideProgress();
//            view.successResult(aa);
//        });
//    }
//
//    public void stopSendingRequests() {
//        //DLog.d("Отменяем задачу и закрываем OkHttpClient");
//        timer.cancel();
//        client.dispatcher().cancelAll();
//        client.connectionPool().evictAll();
//    }
//
//
//    //Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//
//    public void init() {
//        String ip = preferences.getString(KEY_HOST, "wordpress.com");
//        view.init(ip);
//    }
//
//    // Presenter implementation
//
//
//    public interface ClView {
//
//        void showProgress();
//
//        void hideProgress();
//
//        void successResult(List<ViewModel> dataModels);
//
//        void init(String ip);
//    }
//}