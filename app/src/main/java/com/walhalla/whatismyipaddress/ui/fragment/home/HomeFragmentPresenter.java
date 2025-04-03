package com.walhalla.whatismyipaddress.ui.fragment.home;

import android.content.Context;
import android.os.Handler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.SinglePagination;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class HomeFragmentPresenter extends BasePresenter {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    // http://ip-api.com/json/
    public static int[] ipapi = new int[]{47, 110, 111, 115, 106, 47, 109, 111, 99, 46, 105, 112, 97, 45, 112, 105, 47, 47, 58, 112, 116, 116, 104};

    private static final String FIELDS =
            "?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,reverse,mobile,proxy,hosting,query";

    private String value_yes;
    private String value_no;

    public static String lat = "", lng = "", isp = "", asname = "", query = "";
    String message = "";

    private final HomeView view;
    ArrayList<ViewModel> models = new ArrayList<>();
    private final String NO_DATA_FOUND;

    public HomeFragmentPresenter(Context context, HomeView view, Handler handler) {
        super(handler);
        this.view = view;
        NO_DATA_FOUND = context.getString(R.string.no_data_found);
        value_yes = context.getString(R.string.value_yes);
        value_no = context.getString(R.string.value_no);
    }

    public void getIpInfo(Boolean isMyIp, String ip) {

        message = "";
        view.showProgress();

        executor.execute(() -> {
            try {
                OkHttpClient httpClient = NetworkUtils.makeOkhttp();
                String url = SinglePagination.dec0(ipapi) + URLEncoder.encode(ip, "UTF-8")
                        + FIELDS;
                //DLog.d("@@@map@@@" + url);
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .build();

                Response response = httpClient.newCall(request).execute();
                String networkResp = response.body().string();
                parseJSONStringToJSONObject0(networkResp);

            } catch (Exception e) {
                //UnknownHostException
                DLog.handleException(e);
            }

            handler.post(() -> {
                view.hideProgress();
                if (!message.equals("")) {
                    view.receivedErrorMessage(message);
                } else {
                    if (models.size() > 0) {
                        view.successResult(models);
                        view.setMap(lat, lng, asname, isp, query);
                    } else {
                        view.receivedErrorMessage(NO_DATA_FOUND);
                    }
                }
            });
        });
    }

    private void parseJSONStringToJSONObject0(String networkResp) {
        try {
            JSONObject jsonObject = new JSONObject(networkResp);
            String status = "";
            if (jsonObject.has(KEY_STATUS)) {
                status = jsonObject.getString(KEY_STATUS);
            }
            if (status.equals("success")) {
                models.clear();
                //values.clear();
                if (jsonObject.has("continent")) {
                    models.add(new TwoColItem("Continent", jsonObject.getString("continent")));
                }
                if (jsonObject.has("country")) {
                    models.add(new TwoColItem("Country", jsonObject.getString("country")));
                }
                if (jsonObject.has("countryCode")) {
                    models.add(new TwoColItem("Country Code", jsonObject.getString("countryCode")));
                }
                if (jsonObject.has("region")) {
                    models.add(new TwoColItem("Region", jsonObject.getString("region")));
                }
                if (jsonObject.has("regionName")) {
                    models.add(new TwoColItem("Region Name", jsonObject.getString("regionName")));
                }
                if (jsonObject.has("city")) {
                    models.add(new TwoColItem("City", jsonObject.getString("city")));
                }
                if (jsonObject.has("district")) {
                    models.add(new TwoColItem("District", jsonObject.getString("district")));
                }
                if (jsonObject.has("zip")) {
                    models.add(new TwoColItem("ZIP Code", jsonObject.getString("zip")));
                }
                if (jsonObject.has("lat")) {
                    models.add(new TwoColItem("Latitude", String.valueOf(jsonObject.getDouble("lat"))));
                    lat = String.valueOf(jsonObject.getDouble("lat"));
                }
                if (jsonObject.has("lon")) {
                    models.add(new TwoColItem("Longitude", jsonObject.getString("lon")));
                    lng = String.valueOf(jsonObject.getDouble("lon"));
                }
                if (jsonObject.has("timezone")) {
                    models.add(new TwoColItem("Time Zone", jsonObject.getString("timezone")));
                }
                if (jsonObject.has("currency")) {
                    models.add(new TwoColItem("Currency", jsonObject.getString("currency")));
                }
                if (jsonObject.has("isp")) {
                    models.add(new TwoColItem("ISP", jsonObject.getString("isp")));
                    isp = jsonObject.getString("isp");
                }
                if (jsonObject.has("org")) {
                    models.add(new TwoColItem("Organisation", jsonObject.getString("org")));
                }
                if (jsonObject.has("as")) {
                    models.add(new TwoColItem("AS", jsonObject.getString("as")));
                }
                if (jsonObject.has("asname")) {
                    models.add(new TwoColItem("AS Name", jsonObject.getString("asname")));
                    asname = jsonObject.getString("asname");
                }
                if (jsonObject.has("mobile")) {
                    models.add(new TwoColItem("Cellular/Wifi", String.valueOf(jsonObject.getBoolean("mobile")).equals("true") ? "Cellular" : "Wifi"));
                }
                if (jsonObject.has("hosting")) {
                    models.add(new TwoColItem("Is Hosting", String.valueOf(jsonObject.getBoolean("hosting")).equals("true") ? value_yes : value_no));
                }
                if (jsonObject.has("proxy")) {
                    models.add(new TwoColItem("Is Proxy", String.valueOf(jsonObject.getBoolean("proxy")).equals("true") ? value_yes : value_no));
                }
                if (jsonObject.has("query")) {
                    models.add(new TwoColItem("Public IP", jsonObject.getString("query")));
                    query = jsonObject.getString("query");
                }
            } else {
                if (jsonObject.has(KEY_MESSAGE)) {
                    message = jsonObject.getString(KEY_MESSAGE).toUpperCase();
                }
            }

        } catch (JSONException e) {
            message = NO_DATA_FOUND;
            DLog.handleException(e);
        }
    }

    public interface HomeView {

        void showProgress();

        void hideProgress();

        void receivedErrorMessage(String message);

        void successResult(ArrayList<ViewModel> models);

        void setMap(String lat, String lng, String asname, String isp, String query);
    }

}