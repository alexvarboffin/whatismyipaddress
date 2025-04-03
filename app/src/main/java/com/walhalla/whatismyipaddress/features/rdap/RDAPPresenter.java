package com.walhalla.whatismyipaddress.features.rdap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.walhalla.ui.BuildConfig;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.header.HeaderItem;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.lnk.LnkItem;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.WhoisPresenter;
import com.walhalla.whatismyipaddress.whois.verisign.Link;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDAPPresenter extends WhoisPresenter {

    private final String domainsRdapJsonTxt;
    private final String ipv4;
    private final String NO_DATA_FOUND;
    private final SharedPreferences preferences;
    private final String internet_connectivity_problem0;

    private final Map<String, Integer> headerMap = new HashMap<>();
    private final Resources resources;


    public RDAPPresenter(Context context, Handler handler, Callback callback) {
        super(context, handler, callback);
        this.domainsRdapJsonTxt = fileFromAsset(context, "rdap.json");
        this.ipv4 = fileFromAsset(context, "ipv4.json");
        this.NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.internet_connectivity_problem0 = context.getString(R.string.internet_connectivity_problem);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.resources = context.getResources();

        // Initialize the map with resource IDs
        headerMap.put("handle", R.string.domain_identifier);
        headerMap.put("startAddress", R.string.start_address);
        headerMap.put("endAddress", R.string.end_address);
        headerMap.put("ipVersion", R.string.ip_version);
        headerMap.put("name", R.string.name);
        headerMap.put("type", R.string.type);
        headerMap.put("parentHandle", R.string.parent_handle);
        headerMap.put("port43", R.string.port_43);
        headerMap.put("objectClassName", R.string.object_class_name);
        headerMap.put("events", R.string.events);
        headerMap.put("links", R.string.links);
        headerMap.put("entities", R.string.entities);
        headerMap.put("status", R.string.status);
        headerMap.put("cidr0_cidrs", R.string.cidr0_cidrs);
        headerMap.put("arin_originas0_originautnums", R.string.arin_originas0_originautnums);
        // Add other mappings as necessary
    }

    public String getHeader(String key) {
        Integer resourceId = headerMap.get(key);
        if (resourceId != null) {
            return resources.getString(resourceId);
        }
        return key; // Default to key if no mapping found
    }

    private String fileFromAsset(Context context, String jsonName) {
        String jsonString;
        try {
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(jsonName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e("@@@", "Error reading JSON file: " + e.getMessage());
            return null;
        }
        return jsonString;
    }

    //https://lookup.icann.org/ru


    public static String extractTopLevelDomain(String domainName) {
        String[] domainParts = domainName.split("\\.");

        if (domainParts.length > 1) {
            return domainParts[domainParts.length - 1];
        } else {
            return "";
        }
    }


    //    public String performWhoisLookup(String domainName) {
    //        WhoisClient whoisClient = new WhoisClient();
    //        String result = "";
    //
    //        try {
    //            whoisClient.connect(WhoisClient.DEFAULT_HOST);
    //            result = whoisClient.query(domainName);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        } finally {
    //            try {
    //                whoisClient.disconnect();
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //
    //        return result;
    //    }

    List<ViewModel> nameValue;

    @Override
    public void whois(String userInput) {

        nameValue = new ArrayList<>();

        //onPreExecute
        executor.execute(() -> {

            //getRdapservers();

            boolean iPv4Address;
            boolean iPv6Address;

            try {
                boolean isLink = isLink(userInput);

                String domain = "";
                String topLevelDomain = "";
                String entities = "";

                iPv4Address = InetAddressUtils.isIPv4Address(userInput);
                iPv6Address = InetAddressUtils.isIPv6Address(userInput);

                if (iPv4Address || iPv6Address) {
                    domain = null;
                } else if (isLink) {
                    domain = extractDomain(userInput);
                    DLog.d("[domain]=>" + domain);

                    if (domain != null) {
                        topLevelDomain = extractTopLevelDomain(domain);
                        if (TextUtils.isEmpty(topLevelDomain)) {
                            topLevelDomain = domain;
                        }
                    }
                } else {
                    boolean isDomain = isDomain(userInput);
                    if (isDomain) {
                        domain = userInput;
                        topLevelDomain = extractTopLevelDomain(domain);
                        if (TextUtils.isEmpty(topLevelDomain)) {
                            topLevelDomain = domain;
                        }
                    } else {
                        entities = userInput;
//                        domain = userInput;
//                        topLevelDomain = extractTopLevelDomain(domain);
//                        if (TextUtils.isEmpty(topLevelDomain)) {
//                            topLevelDomain = domain;
//                        }
                        DLog.d("Введено обычное значение=> " + domain + " " + topLevelDomain);
                    }
                }
                if (!TextUtils.isEmpty(entities)) {
                    Set<String> resolvedServers = new TreeSet<>();

                    resolvedServers.add("https://rdap.db.ripe.net/entities?fn=" + entities);
                    resolvedServers.add("https://rdap.db.ripe.net/entities?handle=" + entities);

                    for (String requestUrl : resolvedServers) {
                        String networkResp = makeRequest(requestUrl);
                        if (isValidResponse(networkResp)) {
                            List<ViewModel> tmp = readFromInternet(networkResp);
                            if (!tmp.isEmpty()) {
                                String zz = String.valueOf(requestUrl);
                                nameValue.add(new LnkItem(zz, zz));
                                nameValue.addAll(tmp);
                            }
                        }
                    }

                } else if (!TextUtils.isEmpty(domain)) {

                    Set<String> resolvedServers = new TreeSet<>();
                    String rdapTmp = InetAddressUtils.resolveRdapForDomain(domainsRdapJsonTxt, topLevelDomain);

                    if (BuildConfig.DEBUG) {
                        DLog.d("Resolved rdap: -> " + rdapTmp);
                    }

                    if (TextUtils.isEmpty(rdapTmp)) {// inurl:rdap.*.ru
                        if ("ru".equalsIgnoreCase(topLevelDomain)) {
                            //rdap = "https://rdap.tcinet.ru/";
                            //rdap = "https://rdap.nic.ru/";
                            //not work ->rdap = "https://rdap.ripn.net/";
                            resolvedServers.add("https://rdap.db.ripe.net/" + "domain/" + domain);
                        } else {
                            resolvedServers.add("https://rdap.verisign.com/com/v1/" + "domain/" + domain);
                        }
                    } else {
                        //@@@https://rdap.db.ripe.net/domain/193.0.6.139.in-addr.arpa
                        resolvedServers.add(rdapTmp + "domain/" + domain);
                    }

                    for (String requestUrl : resolvedServers) {
                        String networkResp = makeRequest(requestUrl);

                        //networkResp = fromAsset();
                        if (isValidResponse(networkResp)) {
                            List<ViewModel> tmp = readFromInternet(networkResp);
                            if (!tmp.isEmpty()) {
                                String zz = String.valueOf(requestUrl);
                                nameValue.add(new LnkItem(zz, zz));
                                nameValue.addAll(tmp);
                            }
                        } else {
//                            //https://rdap.db.ripe.net/domains?name=196.46.95.in-addr.arpa
//                            rdap = "https://rdap.db.ripe.net/";
//                            requestUrl = rdap + "domain?name=" + domain;
//                            networkResp = makeRequest(requestUrl);
//                            if (isValidResponse(networkResp)) {
//                                nameValue = readFromInternet(networkResp);
//                            }
                        }
                    }
                } else {

                    DLog.d("@@@" + iPv4Address + " " + iPv6Address);

                    if (iPv4Address || iPv6Address) {


                        Set<String> resolvedServers = new TreeSet<>();

                        if (iPv4Address) {
                            String m = InetAddressUtils.resolveRdapFromIp(ipv4, userInput);

                            if (!TextUtils.isEmpty(m)) {
                                String requestUrl = m + "ip/" + userInput;
                                resolvedServers.add(requestUrl);
                            }
                        }

                        String defIprdap = "https://rdap.db.ripe.net/ip/" + userInput;
                        resolvedServers.add(defIprdap);

                        DLog.d("@@@@@@@" + resolvedServers);

                        for (String rdap : resolvedServers) {
                            String networkResp = makeRequest(rdap);
                            //networkResp = fromAsset();
                            if (isValidResponse(networkResp)) {
                                List<ViewModel> tmp = readFromInternet(networkResp);
                                if (!tmp.isEmpty()) {
                                    String zz = String.valueOf(rdap);
                                    nameValue.add(new LnkItem(zz, zz));
                                    nameValue.addAll(tmp);
                                }
                                DLog.d("@@@" + nameValue);

                                //https://rdap.arin.net/registry/ip/8.8.8.8
                                //break;
                            }

                        }
                    }
                }

            } catch (Exception e) {
                DLog.handleException(e);
                if (e instanceof UnknownHostException) {
                    nameValue.add(new SingleItem(internet_connectivity_problem0, R.color.colorPrimaryDark));
                } else {
                    nameValue.add(new SingleItem(NO_DATA_FOUND, R.color.colorPrimaryDark));
                }
            }
            if (callback != null) {
                handler.post(() -> {
                    callback.hideProgress();
                    callback.successResult(nameValue);
                });
            }
        });
    }

    private String makeRequest(String requestUrl) throws IOException {
        DLog.d("<**>" + requestUrl);
        String networkResp = "";
        OkHttpClient httpClient = NetworkUtils.makeOkhttp();
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0")
                //.addHeader("Accept", "application/json, application/rdap+json")
                .addHeader("Accept", "application/json")

                //                        .addHeader("Accept-Language", "en-US,en;q=0.5")
                //                        .addHeader("Accept-Encoding", "gzip, deflate, br")
                //                        .addHeader("Origin", "https://lookup.icann.org")
                //                        .addHeader("DNT", "1")
                //                        .addHeader("Connection", "keep-alive")
                //                        .addHeader("Sec-Fetch-Dest", "empty")
                //                        .addHeader("Sec-Fetch-Mode", "cors")
                //                        .addHeader("Sec-Fetch-Site", "cross-site")
                //                        .addHeader("Sec-GPC", "1")
                //                        .addHeader("Pragma", "no-cache")
                //                        .addHeader("Cache-Control", "no-cache")
                .build();


        Response response = httpClient.newCall(request).execute();
        networkResp = response.body().string();

        if (BuildConfig.DEBUG) {
            DLog.d("{}{}{}{}" + networkResp);
        }
        return networkResp;
    }

    private boolean isValidResponse(String networkResp) {
        return !TextUtils.isEmpty(networkResp);
    }

    private void getRdapservers() {
        String tld = "ya.ru"; // Замените на нужную зону
        String[] rdapServers = getRdapServers(tld);

        DLog.d("@@@@@" + "RDAP-серверы для зоны ." + tld + ":");
        for (String server : rdapServers) {
            DLog.d("@@@@@" + server);
        }
    }

    public static String[] getRdapServers(String tld) {
        String[] rdapServers = {};
        try {
            // Подключение к WHOIS-серверу
            Socket socket = new Socket("whois.iana.org", 43);
            // Отправка запроса на получение информации о зоне
            socket.getOutputStream().write((tld + "\r\n").getBytes());
            // Чтение ответа
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Поиск строки с RDAP-серверами
                if (line.startsWith("rdap://")) {
                    rdapServers = line.split(" ");
                    break;
                } else {
                    DLog.d("{}{}" + line);
                }
            }
            // Закрытие соединения
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rdapServers;
    }

    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (MalformedURLException e) {
            DLog.handleException(e);
            return null;
        }
    }

    /**
     * http://8.8.8.8 - false
     * 8.8.8.8 - false
     */
    public static boolean isDomain(String value) {
        String domainPattern = "^([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(domainPattern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean isLink(String value) {
        // Регулярное выражение для проверки ссылки
        String linkPattern = "^(http|https)://.*$";
        Pattern pattern = Pattern.compile(linkPattern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private List<ViewModel> readFromInternet(String networkResp) {
        List<ViewModel> nameValue = new ArrayList<>();
        try {
            Gson gson = new Gson();
            //DomainInfo obj = gson.fromJson(networkResp, DomainInfo.class);
            //String objectClassName = obj.objectClassName;
            JSONObject ob1 = new JSONObject(networkResp);

            if (ob1.has("errorCode")) {
                ErrorResponse error = gson.fromJson(networkResp, ErrorResponse.class);


                item0("Error Code:", String.valueOf(error.errorCode), nameValue);
                item0("Title:", error.title, nameValue);
                item0("port43:", error.port43, nameValue);

                List<Link> links = error.links;
                if (links != null) {
                    for (Link link : links) {


                        if (!TextUtils.isEmpty(link.rel)) {
                            String rel = link.rel;
                            if ("self".equals(rel)) {
                                item0(context.getString(R.string.registry_server_url), link.href, nameValue);
                            } else if ("related".equals(rel)) {
                                //registrar_server_url = link.href;
                            } else if ("copyright".equals(rel)) {
                                item0("Copyright:", link.href, nameValue);
                            } else if ("alternate".equals(rel)) {
                                //registrar_server_url = link.href;
                            }
                            //                else if ("help".equals(rel)) {
                            //                    registrar_server_url = link.getString("href");
                            //                }
                        }


//                        "rdapConformance" : [ "cidr0", "rdap_level_0", "nro_rdap_profile_0", "redacted" ],
//                        "notices" : [ {
//                            "title" : "Terms and Conditions",
//                                    "description" : [ "This is the RIPE Database query service. The objects are in RDAP format." ],
//                            "links" : [ {
//                                "rel" : "terms-of-service",
//                                        "href" : "http://www.ripe.net/db/support/db-terms-conditions.pdf",
//                                        "type" : "application/pdf"
//                            } ]
//                        } ],
                    }
                }
            } else {


                addentitySearchResults(ob1, nameValue);


                String ldhName = "";

                if (ob1.has("ldhName")) {
                    ldhName = ob1.getString("ldhName");
                }


                nameValue.add(new HeaderItem(context.getString(R.string.domain_info)));
                nameValue.add(new TwoColItem(context.getString(R.string.domain), ldhName));

                String domain_id = domaineId(ob1);
                if (!domain_id.isEmpty()) {
                    nameValue.add(new TwoColItem(context.getString(R.string.domain_id), domain_id));
                }

                //ip rdap
                // Проверка других ключей
                addStringToMap(ob1, "handle", nameValue);
                addStringToMap(ob1, "startAddress", nameValue);
                addStringToMap(ob1, "endAddress", nameValue);
                addStringToMap(ob1, "ipVersion", nameValue);
                addStringToMap(ob1, "name", nameValue);
                addStringToMap(ob1, "type", nameValue);
                addStringToMap(ob1, "parentHandle", nameValue);


                //addArrayToMap(ob1, "events", nameValue);

                addStringToMap(ob1, "port43", nameValue);
                addArrayToMap(ob1, "status", nameValue);
                addStringToMap(ob1, "objectClassName", nameValue);
                addArrayToMap(ob1, "cidr0_cidrs", nameValue);
                addArrayToMap(ob1, "arin_originas0_originautnums", nameValue);
                //end


                nameValue.add(new TwoColItem(context.getString(R.string.domain_status), ""));

                if (ob1.has("status")) {
                    JSONArray status = ob1.getJSONArray("status");
                    for (int i = 0; i < status.length(); i++) {
                        nameValue.add(new TwoColItem("", status.getString(i)));
                    }
                }
                nameValue.add(new TwoColItem(context.getString(R.string.domain_name_servers), ""));
                if (ob1.has("nameservers")) {
                    JSONArray nameservers = ob1.getJSONArray("nameservers");
                    for (int i = 0; i < nameservers.length(); i++) {
                        JSONObject nameserver = nameservers.getJSONObject(i);
                        //nameserver.getString("objectClassName");
                        nameValue.add(new TwoColItem("", nameserver.getString("ldhName")));
                    }
                }

                //entities

//                String registrar_abuse_phone = "";
//                String registrar_abuse_email = "";


                //Events
                String rdap_database_update_date = "";
                List<Event> m = processEvents(ob1);
                if (!m.isEmpty()) {



//                    Map<String, Integer> eventMap = new HashMap<>();
//                    eventMap.put("expiration", R.string.domain_registration_expiry);
//                    eventMap.put("last changed", R.string.domain_update_date);
//                    eventMap.put("registration", R.string.domain_creation_date);


                    nameValue.add(new TwoColItem(context.getString(R.string.dates_label), ""));

                    String domain_update_date = "";
                    String domain_registration_expiry = "";
                    String domain_creation_date = "";


                    for (Event event : m) {
                        if ("last changed".equals(event.eventAction)) {
                            domain_update_date = formatDate(event.eventDate);
                        } else if ("last update of RDAP database".equals(event.eventAction)) {
                            rdap_database_update_date = formatDate(event.eventDate);
                        } else if ("expiration".equals(event.eventAction)) {
                            domain_registration_expiry = formatDate(event.eventDate);
                        } else if ("registration".equals(event.eventAction)) {
                            domain_creation_date = formatDate(event.eventDate);
                        } else {
                            nameValue.add(new TwoColItem("events[" + event.eventAction + "]", event.eventDate));
                        }
                    }

                    if(!TextUtils.isEmpty(domain_registration_expiry)){
                        nameValue.add(new TwoColItem(context.getString(R.string.domain_registration_expiry), domain_registration_expiry));

                    }
                    if(!TextUtils.isEmpty(domain_update_date)){
                        nameValue.add(new TwoColItem(context.getString(R.string.domain_update_date), domain_update_date));

                    }
                    if(!TextUtils.isEmpty(domain_creation_date)){
                        nameValue.add(new TwoColItem(context.getString(R.string.domain_creation_date), domain_creation_date));

                    }

                }


                nameValue.add(new HeaderItem(context.getString(R.string.registration_info)));

                //                    Информация о регистраторе
                //
                //                    Домен: MarkMonitor Inc.
                //                    Код IANA: 292
                //                    Адрес электронной почты контактного лица по жалобам на злоупотребления: abusecomplaints@markmonitor.com
                //                    Телефон контактного лица по жалобам на злоупотребления: tel:+

                //--addArrayToMap(ob1, "entities", nameValue);

                processEntities(ob1, nameValue);


                nameValue.add(new HeaderItem(context.getString(R.string.dnssec_signed_info)));
                //==>>nameValue.add(new TwoColItem(context.getString(R.string.dnssec_signed_zone), "@@@"));

                //nameValue.add(new TwoColItem(context.getString(R.string.url_adresses), ""));
                //nameValue.add(new TwoColItem("@@@", "@@@"));

                String registry_server_url = "";
                String registrar_server_url = "";

                addArrayToMap(ob1, "links", nameValue);

                if (ob1.has("links")) {
                    JSONArray links = ob1.getJSONArray("links");
                    for (int i = 0; i < links.length(); i++) {
                        JSONObject link = links.getJSONObject(i);
                        if (link.has("rel")) {
                            String rel = link.getString("rel");
                            if ("self".equals(rel)) {
                                registry_server_url = link.getString("href");
                            } else if ("related".equals(rel)) {
                                registrar_server_url = link.getString("href");
                            } else if ("alternate".equals(rel)) {
                                registrar_server_url = link.getString("href");
                            }
                            //                else if ("help".equals(rel)) {
                            //                    registrar_server_url = link.getString("href");
                            //                }
                        }
                    }
                }
                nameValue.add(new HeaderItem(context.getString(R.string.dnssec_authoritative_servers)));
                nameValue.add(new TwoColItem(context.getString(R.string.registry_server_url), registry_server_url));

                nameValue.add(new TwoColItem(context.getString(R.string.rdap_database_update_date), rdap_database_update_date));

                nameValue.add(new TwoColItem(context.getString(R.string.registrar_server_url), registrar_server_url));

                nameValue.add(new TwoColItem(context.getString(R.string.notifications_notes), ""));
                nameValue.add(new TwoColItem(context.getString(R.string.notifications_notes_notifications), ""));

               processNotices(ob1, nameValue);

                //            if (jsonObject.has("ErrorMessage")) {
                //                JSONObject errorMessage = jsonObject.getJSONObject("ErrorMessage");
                //                Iterator<String> keys = errorMessage.keys();
                //
                //                while (keys.hasNext()) {
                //                    String key = keys.next();
                //                    String value = errorMessage.getString(key);
                //                    nameValue.add(new ListItem(key.toUpperCase(), value));
                //                    if ("AUTHENTICATE_05".equals(value)) {
                //                        DLog.d("-->" + "AUTHENTICATE_05");
                //                    }
                //                }
                //            }

                //            if (jsonObject.has("WhoisRecord")) {
                //
                //
                //                if (jsonObject1.has("estimatedDomainAge")) {
                //                 }
                //            }
            }

        } catch (Exception ex) {
            DLog.handleException(ex);
            DLog.d("@" + ex);
        }
        return nameValue;
    }

    private void processNotices(JSONObject ob1, List<ViewModel> nameValue) throws JSONException {
        JSONArray notices = ob1.getJSONArray("notices");

        for (int i = 0; i < notices.length(); i++) {
            JSONObject notice = notices.getJSONObject(i);
            String title = notice.getString("title");
            JSONArray descriptions = notice.getJSONArray("description");


            nameValue.add(new TwoColItem("" + title, ""));
            for (int i1 = 0; i1 < descriptions.length(); i1++) {
                String description = descriptions.getString(i1);
                nameValue.add(new SingleItem(description));
            }

            if (notice.has("links")) {


//                "links" : [ {
//                    "value" : "https://rdap.db.ripe.net/entities?fn=RIPE-RIPE",
//                            "rel" : "inaccuracy-report",
//                            "href" : "https://www.ripe.net/contact-form?topic=ripe_dbm&show_form=true",
//                            "type" : "text/html"
//                } ]

                JSONArray links_1_all = notice.getJSONArray("links");
                for (int i1 = 0; i1 < links_1_all.length(); i1++) {
                    JSONObject links_1 = links_1_all.getJSONObject(i1);
                    String href = links_1.getString("href");
                    //String type = links_1.getString("type");
                    nameValue.add(new LnkItem(href, href));
                }
            }

        }
    }


    static class Event {


        String eventAction;
        String eventDate;

        public Event(String eventAction, String eventDate) {
            this.eventAction = eventAction;
            this.eventDate = eventDate;
        }
    }

    private List<Event> processEvents(JSONObject ob1) throws JSONException {

        List<Event> list = new ArrayList<>();
        if (ob1.has("events")) {

            JSONArray events = ob1.getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {


                JSONObject event = events.getJSONObject(i);

                Event event1 = new Event(
                        event.getString("eventAction"),
                        event.getString("eventDate")
                );
                list.add(event1);
            }
        }
        return list;
    }

    private void processEntities(JSONObject ob1, List<ViewModel> nameValue) throws JSONException {



        if (ob1.has("entities")) {
            //DLog.d("@@"+e.entities.toString());


            JSONArray entities = ob1.getJSONArray("entities");
            JSONObject entityObject = entities.getJSONObject(0);


            //====
            addStringToMap(entityObject, "handle", nameValue);
            addArrayToMap(entityObject, "vcardArray", nameValue);
            addArrayToMap(entityObject, "roles", nameValue);
            addArrayToMap(entityObject, "remarks", nameValue);
            addArrayToMap(entityObject, "links", nameValue);
            addArrayToMap(entityObject, "events", nameValue);
            addArrayToMap(entityObject, "status", nameValue);
            addStringToMap(entityObject, "port43", nameValue);
            addStringToMap(entityObject, "objectClassName", nameValue);

            // Обработка массива entities внутри entities
            JSONArray nestedEntitiesArray = entityObject.optJSONArray("entities");
            if (nestedEntitiesArray != null) {
                for (int i = 0; i < nestedEntitiesArray.length(); i++) {
                    try {
                        JSONObject nestedEntityObject = nestedEntitiesArray.getJSONObject(i);
                        processEntity(nestedEntityObject, nameValue);
                    } catch (JSONException e) {
                        DLog.handleException(e);
                    }
                }
            }
            //====

            processVCardArray(entityObject, nameValue);


            //================================

            //                    JSONArray item00 = vcardData.getJSONArray(2);
            //                    String key = item00.getString(0);
            //                    registrar_abuse_phone = item00.getString(3);
            //                    DLog.d("@@@@@@@@@@@" + key + " ");

            //                    JSONArray item = vcardData.getJSONArray(3);
            //                    registrar_abuse_email = item.getString(3);

            if (entityObject.has("publicIds")) {

                String identifier = "";
                JSONArray jj = entityObject.getJSONArray("publicIds");
                identifier = jj.getJSONObject(0).getString("identifier");

                if (!TextUtils.isEmpty(identifier)) {
                    nameValue.add(new TwoColItem(context.getString(R.string.registrar_iana_code), identifier));
                }
            }
        }
    }

    private void addentitySearchResults(JSONObject ob1, List<ViewModel> keyValueMap) {
        if (ob1.has("entitySearchResults")) {
            JSONArray results = ob1.optJSONArray("entitySearchResults");
            if (results != null) {
                int length = results.length();
                for (int i = 0; i < length; i++) {
                    try {
                        JSONObject child = results.getJSONObject(i);
                        addStringToMap(child, "handle", keyValueMap);
                        processVCardArray(child, keyValueMap);
                        processEntities(child, keyValueMap);

                        List<Event> m = processEvents(child);
                        if (!m.isEmpty()) {



//                    Map<String, Integer> eventMap = new HashMap<>();
//                    eventMap.put("expiration", R.string.domain_registration_expiry);
//                    eventMap.put("last changed", R.string.domain_update_date);
//                    eventMap.put("registration", R.string.domain_creation_date);


                            nameValue.add(new TwoColItem(context.getString(R.string.dates_label), ""));

                            String domain_update_date = "";
                            String domain_registration_expiry = "";
                            String domain_creation_date = "";


                            for (Event event : m) {
                                if ("last changed".equals(event.eventAction)) {
                                    domain_update_date = formatDate(event.eventDate);
                                }
//                                else if ("last update of RDAP database".equals(event.eventAction)) {
//                                    rdap_database_update_date = formatDate(event.eventDate);
//                                }
                                else if ("expiration".equals(event.eventAction)) {
                                    domain_registration_expiry = formatDate(event.eventDate);
                                } else if ("registration".equals(event.eventAction)) {
                                    domain_creation_date = formatDate(event.eventDate);
                                } else {
                                    nameValue.add(new TwoColItem("events[" + event.eventAction + "]", event.eventDate));
                                }
                            }

                            if(!TextUtils.isEmpty(domain_registration_expiry)){
                                nameValue.add(new TwoColItem(context.getString(R.string.domain_registration_expiry), domain_registration_expiry));

                            }
                            if(!TextUtils.isEmpty(domain_update_date)){
                                nameValue.add(new TwoColItem(context.getString(R.string.domain_update_date), domain_update_date));

                            }
                            if(!TextUtils.isEmpty(domain_creation_date)){
                                nameValue.add(new TwoColItem(context.getString(R.string.domain_creation_date), domain_creation_date));

                            }

                        }
                        processNotices(child, keyValueMap);

                    } catch (JSONException e) {
                        DLog.handleException(e);
                    }

                }
            }
        }
    }

    private void processEntity(JSONObject entityObject, List<ViewModel> keyValueMap) {
        addStringToMap(entityObject, "handle", keyValueMap);
        //addArrayToMap(entityObject, "vcardArray", keyValueMap);
        processVCardArray(entityObject, keyValueMap);

        addArrayToMap(entityObject, "roles", keyValueMap);
        addArrayToMap(entityObject, "remarks", keyValueMap);
        addArrayToMap(entityObject, "links", keyValueMap);
        addArrayToMap(entityObject, "events", keyValueMap);
        addArrayToMap(entityObject, "status", keyValueMap);
        addStringToMap(entityObject, "port43", keyValueMap);
        addStringToMap(entityObject, "objectClassName", keyValueMap);
    }

    private void processVCardArray(JSONObject entityObject, List<ViewModel> keyValueMap) {


        JSONArray vcardArray = entityObject.optJSONArray("vcardArray");
        if (vcardArray != null) {
//            for (int j = 0; j < vcardArray.length(); j++) {
//            }

            try {
                //JSONObject nestedEntityObject = vcardArray.getJSONObject(j);
                DLog.d("{}" + vcardArray.toString());
                // Извлечение значений из массива
                String vcardType = vcardArray.getString(0);

                JSONArray vcardData = vcardArray.getJSONArray(1);
                for (int i = 0; i < vcardData.length(); i++) {
                    JSONArray item00 = vcardData.getJSONArray(i);
                    String key = item00.getString(0);
                    String value = item00.getString(3);
                    //DLog.d("[" + i + "]" + key + " " + value);

                    DLog.d("[=========" + i + "]" + key + " " + value);

                    //                        if (key.equals("tel")) {
                    //                            registrar_abuse_phone = value;
                    //                        } else if (key.equals("email")) {
                    //                            registrar_abuse_email = value;
                    //                        } else


                    if (key.equals("tel")) {
                        String registrar_abuse_phone = value;
                        keyValueMap.add(new TwoColItem(context.getString(R.string.registrar_abuse_phone), registrar_abuse_phone));


                    } else if (key.equals("email")) {
                        String registrar_abuse_email = value;
                        keyValueMap.add(new TwoColItem(context.getString(R.string.registrar_abuse_email), registrar_abuse_email));

                    } else if (key.equals("fn")) {

                        String registrar_domain = "";
                        registrar_domain = value;
                        nameValue.add(new TwoColItem(context.getString(R.string.registrar_domain), registrar_domain));
                        //item0("vcard[" + i + "]", value, keyValueMap);

                    }  else if (key.equals("version")) {
                        item0("vcard[" + key + "]", value, keyValueMap);
                    }else {
                        item0("vcard[" + key + "]", value, keyValueMap);
                    }


                }
            } catch (JSONException e) {
                DLog.handleException(e);
            }
        }
    }

    private void addArrayToMap(JSONObject jsonObject, String key, List<ViewModel> nameValue) {
        JSONArray jsonArray = jsonObject.optJSONArray(key);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.opt(i);
                if (value != null) {
                    nameValue.add(new TwoColItem(key + "[" + i + "]", value.toString()));
                }
            }
        }
    }

    private void addStringToMap(JSONObject jsonObject, String key, List<ViewModel> nameValue) {
        String value = jsonObject.optString(key, null);
        if (!TextUtils.isEmpty(value)) {
            String header = getHeader(key);
            nameValue.add(new TwoColItem(header, value));
        }
    }

    private void item0(String key, String value, List<ViewModel> nameValue) {
        if (!TextUtils.isEmpty(key) || !TextUtils.isEmpty(value)) {
            String header = getHeader(key);
            nameValue.add(new TwoColItem(header, value));
        }
    }

    private String domaineId(JSONObject ob1) throws JSONException {
        String domain_id = null;
        if (ob1.has("handle")) {
            domain_id = ob1.getString("handle");
            //ip
        } else {
            // Ключ "handle" отсутствует в JSON-объекте
        }
        return domain_id;
    }

    private String formatDate(String eventDate) {
        String outputDateString = eventDate;
        SimpleDateFormat inputDateFormat = null;
        Date date = null;

        try {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            date = inputDateFormat.parse(eventDate);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
            if (date != null) {
                outputDateString = outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            DLog.handleException(e);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
                if (date != null) {
                    outputDateString = outputDateFormat.format(date);
                }
            }

        }
        return outputDateString;
    }
}