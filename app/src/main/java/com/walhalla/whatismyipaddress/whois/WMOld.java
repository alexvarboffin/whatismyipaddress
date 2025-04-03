//package com.walhalla.whatismyipaddress.whois;
//
//import android.content.Context;
//import android.os.Handler;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import com.walhalla.generated.WhoisApi;
//import com.walhalla.ui.DLog;
//import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
//import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class WMOld extends WhoisTaskManager {
//
//    @Override
//    public void whois(String ip) {
//        //onPreExecute
//        executor.execute(() -> {
//            List<ViewModel> nameValue0 = new ArrayList<>();
//            try {
//                OkHttpClient httpClient = NetworkUtils.makeOkhttp();
//                Request request = new Request.Builder()
//                        .url(WhoisApi.whois0(ip))
//                        .method("GET", null)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                String networkResp = response.body().string();
//                nameValue0 = parseJSONStringToJSONObject(networkResp);
//
//            } catch (Exception e) {
//                DLog.handleException(e);
//            }
//            onPostExecute(nameValue0);
//        });
//    }
//
//    public WMOld(Context context, Handler handler, Callback callback) {
//        super(context, handler, callback);
//    }
//
//    private List<ViewModel> parseJSONStringToJSONObject(String networkResp) {
//        List<ViewModel> nameValue = new ArrayList<>();
//        try {
//            JSONObject jsonObject = new JSONObject(networkResp);
//            if (jsonObject.has("ErrorMessage")) {
//                JSONObject errorMessage = jsonObject.getJSONObject("ErrorMessage");
//                Iterator<String> keys = errorMessage.keys();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    String value = errorMessage.getString(key);
//                    nameValue.add(new TwoColItem(key.toUpperCase(), value));
//                    if ("AUTHENTICATE_05".equals(value)) {
//                        DLog.d("-->" + "AUTHENTICATE_05");
//                    }
//                }
//            }
//
//            if (jsonObject.has("WhoisRecord")) {
//
//                JSONObject jsonObject1 = jsonObject.getJSONObject("WhoisRecord");
//                if (jsonObject1.has("createdDate")) {
//                    String val = jsonObject1.getString("createdDate");
//                    nameValue.add(new TwoColItem("Created Date", val));
//                }
//                if (jsonObject1.has("updatedDate")) {
//                    String val = jsonObject1.getString("updatedDate");
//                    nameValue.add(new TwoColItem("Updated Date", val));
//                }
//                if (jsonObject1.has("expiresDate")) {
//                    String val = jsonObject1.getString("expiresDate");
//                    nameValue.add(new TwoColItem("Expires Date", val));
//                }
//                if (jsonObject1.has("registrant")) {
//                    nameValue.add(new TwoColItem("Registrant:", ""));
//
//                    JSONObject jsonObject2 = jsonObject1.getJSONObject("registrant");
//                    Iterator<String> keys = jsonObject2.keys();
//
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        nameValue.add(new TwoColItem(key.toUpperCase(), jsonObject2.getString(key)));
//                    }
//                }
//                if (jsonObject1.has("administrativeContact")) {
//                    nameValue.add(new TwoColItem("Administrative Contact:", ""));
//                    JSONObject jsonObject2 = jsonObject1.getJSONObject("administrativeContact");
//                    Iterator<String> keys = jsonObject2.keys();
//
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        nameValue.add(new TwoColItem(key.toUpperCase(), jsonObject2.getString(key)));
//                    }
//                }
//                if (jsonObject1.has("technicalContact")) {
//                    nameValue.add(new TwoColItem("Technical Contact: ", ""));
//                    JSONObject jsonObject2 = jsonObject1.getJSONObject("technicalContact");
//                    Iterator<String> keys = jsonObject2.keys();
//
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        nameValue.add(new TwoColItem(key.toUpperCase(), jsonObject2.getString(key)));
//                    }
//                }
//                if (jsonObject1.has("nameServers")) {
//                    nameValue.add(new TwoColItem("Name Servers:", ""));
//
//                    JSONObject jsonObject2 = jsonObject1.getJSONObject("nameServers");
//                    Iterator<String> keys = jsonObject2.keys();
//
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        nameValue.add(new TwoColItem(key.toUpperCase(), jsonObject2.getString(key)));
//                    }
//                }
//                if (jsonObject1.has("domainName")) {
//                    nameValue.add(new TwoColItem("Domain Name", jsonObject1.getString("domainName")));
//                }
//                if (jsonObject1.has("contactEmail")) {
//                    nameValue.add(new TwoColItem("Contact Email", jsonObject1.getString("contactEmail")));
//                }
//                if (jsonObject1.has("domainNameExt")) {
//                    nameValue.add(new TwoColItem("Domain Name Ext", jsonObject1.getString("domainNameExt")));
//                }
//                if (jsonObject1.has("estimatedDomainAge")) {
//                    nameValue.add(new TwoColItem("Estimated Domain Age", jsonObject1.getString("estimatedDomainAge")));
//                }
//            }
//        } catch (Exception ex) {
//            DLog.handleException(ex);
//            ex.printStackTrace();
//        }
//        return nameValue;
//    }
//
//}
