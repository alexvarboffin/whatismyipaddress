package com.walhalla.whatismyipaddress.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.domain.IpInfoRepositoryExternal;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;
import com.walhalla.whatismyipaddress.interactor.RemoteIPInfoInteractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;


public class DataHandler extends BasePresenter {
    private RemoteIPInfoInteractor mTask;

    public DataHandler(Context context, Handler handler) {
        super(handler);
        this.mTask = new RemoteIPInfoInteractor(context, BackgroundExecutor.getInstance(),
                MainThreadImpl.getInstance(), (success, data) -> {
            dataRemote = data;
            dataRemoteError = !success;
            dataRemoteLoaded(success, data);
        });
    }

    private interface Callback {
        void errorHandler(String err);
    }

    public void loadDataRemote() {
        if (!this.dataRemoteLoading) {
            DLog.d("Starting...");
            dataRemoteStarted();
            this.dataRemoteLoading = true;
            this.mTask.execute();
        }
    }

    public interface IDataRemoteLoadedListener extends Callback {
        void dataRemoteLoadedHandler(boolean z, IPInfoRemote iPInfoRemote);

        void dataRemoteLoaderStarted();
    }

    public EntityWrapper dataConnection = null;

    public IPInfoRemote dataRemote = null;
    public boolean dataRemoteError = false;
    private final List<IDataRemoteLoadedListener> dataRemoteLoadedListeners = new ArrayList<>();
    public boolean dataRemoteLoading;

    public void setDataRemoteLoadedListener(IDataRemoteLoadedListener listener) {
        this.dataRemoteLoadedListeners.add(listener);
    }

    private void dataRemoteLoaded(boolean success, IPInfoRemote data) {
        this.dataRemoteLoading = false;
        for (IDataRemoteLoadedListener dataRemoteLoadedListener : this.dataRemoteLoadedListeners) {
            dataRemoteLoadedListener.dataRemoteLoadedHandler(success, data);
        }
    }

    private void dataRemoteStarted() {
        for (IDataRemoteLoadedListener dataRemoteLoadedListener : this.dataRemoteLoadedListeners) {
            dataRemoteLoadedListener.dataRemoteLoaderStarted();
        }
    }




    public EntityWrapper loadDataConnection(Context context) {
        this.dataConnection = getConnectionInfo(context);
        return this.dataConnection;
    }

    public static IPInfo getIPInfo(Context context) {
        String a = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0";
        IPInfo ret = new IPInfo();
        ret.InfoRemote = new IpInfoRepositoryExternal().getRemoteInfo(context, a);
        ret.InfoLocal = requestLocalInfo(context);
        ret.InfoConnection = getConnectionInfo(context);
        return ret;
    }


//    public static String getJSON() {
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) new URL(Config.IP_INFO_URL).openConnection();
//            conn.setInstanceFollowRedirects(true);
//            HttpURLConnection.setFollowRedirects(true);
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("User-Agent", Config.ua);
//            conn.setRequestProperty("Accept", "*/*");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.connect();
//
//            int status = conn.getResponseCode();
//
////            boolean redirect = false;
////            if (status != HTTP_OK) {
////                if (status == HttpURLConnection.HTTP_MOVED_TEMP
////                        || status == HttpURLConnection.HTTP_MOVED_PERM
////                        || status == HttpURLConnection.HTTP_SEE_OTHER)
////                    redirect = true;
////            }
////            if (redirect) {
////
////                // get redirect IP_INFO_URL from "location" header field
////                String newUrl = conn.getHeaderField("Location");
////
////                // get the cookie if need, for login
////                String cookies = conn.getHeaderField("Set-Cookie");
////
////                // open the new connnection again
////                conn = (HttpURLConnection) new URL(newUrl).openConnection();
////                conn.setRequestProperty("Cookie", cookies);
////                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
////                conn.addRequestProperty("User-Agent", Config.ua);
////                conn.addRequestProperty("Referer", "google.com");
////            }
//            String content = "";
//            if (status == 200 || status == 201) {
//                content = convertStreamToString(new BufferedInputStream(conn.getInputStream()));
//                Log.i(TAG, "getJSON: " + content);
//            }
//            conn.disconnect();
//            return content;
//        } catch (SocketTimeoutException e) {
//            e.printStackTrace();
//            return null;
//        } catch (Exception e2) {
//            e2.printStackTrace();
//            return null;
//        } catch (Throwable th) {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//        return null;
//    }


    public static IPInfoLocal requestLocalInfo(Context context) {
        try {
            IPInfoLocal ret = new IPInfoLocal();
            NetworkInterface networkInterface = getLocalNetworkInterface();
            if (networkInterface != null) {
//                Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
//                while (enumIpAddr.hasMoreElements()) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//
//
//                    Log.i(TAG, ">>>: "+inetAddress.getClass().getSimpleName());
//
//                    if (!inetAddress.isLoopbackAddress()) {
//                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//                        Log.i(TAG, "***** IP="+ ip);
//                    }
//

//
//                    //String сс = inetAddress.getLocalHost().getHostAddress();
//                }

                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();

//                        Log.i(TAG, ">>>: Host: "+inetAddress.getClass().getSimpleName());
//                        Log.i(TAG, ">>>: "+inetAddress.getHostAddress());

                        if (!inetAddress.isLoopbackAddress()) {
//                            String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//                            Log.i(TAG, "***** IP="+ ip);
                            if (inetAddress instanceof Inet4Address) {
                                ret.localIp = inetAddress.getHostAddress();
                            } else if (inetAddress instanceof Inet6Address) {
                                ret.localIPv6 = inetAddress.getHostAddress();
                            }
                        }
                    }
                }


            }
            DhcpInfo dhcp = getDhcpInfo(context);
            DLog.d("@dhcp@" + (dhcp == null));

            if (dhcp == null) {
                return ret;
            }
            ret.gateway = AssetUtils.intToIp(dhcp.gateway);
            ret.mask = AssetUtils.intToIp(dhcp.netmask);
            ret.dns1 = AssetUtils.intToIp(dhcp.dns1);
            ret.dns2 = AssetUtils.intToIp(dhcp.dns2);
            return ret;
        } catch (Exception ex) {
            DLog.e("Error when getting local ip" + ex);
            return null;
        }
    }

    private static DhcpInfo getDhcpInfo(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(CONNECTIVITY_SERVICE);
            if (cm != null && cm.getActiveNetworkInfo() != null) {

                //NetworkInfo WiFiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                NetworkInfo aa = cm.getActiveNetworkInfo();

                //DLog.d("@dhcp@" + aa.toString() + " " + (aa.getType() == ConnectivityManager.TYPE_MOBILE));
                //DLog.d("@dhcp@" + aa.toString() + " " + (aa.getType() == ConnectivityManager.TYPE_WIFI));

                switch (aa.getType()) {

//                    case ConnectivityManager.TYPE_BLUETOOTH:
//                        result = NETWORK_BLUETOOTH;
//                        break;
//
//                    case ConnectivityManager.TYPE_MOBILE:
//                        WifiManager wifiManager0 = ((WifiManager) context.getApplicationContext()
//                                .getSystemService(Context.WIFI_SERVICE));
//                        return (wifiManager0 == null) ? null : wifiManager0.getDhcpInfo();

                    case ConnectivityManager.TYPE_WIFI:
                        WifiManager wifiManager = ((WifiManager) context.getApplicationContext()
                                .getSystemService(Context.WIFI_SERVICE));
                        return (wifiManager == null) ? null : wifiManager.getDhcpInfo();


                    default:
                        return null;
                }
            }
            DLog.d("Error when getting Dhcp info: ConnectivityManager is null");
            return null;
        } catch (Exception ex) {
            DLog.d("Error when getting Dhcp info" + ex);
            return null;
        }
    }

    private static EntityWrapper getConnectionInfo(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                EntityWrapper connection = new EntityWrapper();


                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Network[] allNetworks = connectivityManager.getAllNetworks();
                    for (Network network : allNetworks) {
                        DLog.i("NetId: " + network.toString());
                    }
                }

                if (networkInfo != null) {
                    DLog.d("@@@!!!" + networkInfo);
                }
                if (networkInfo != null /*&& networkInfo.isConnectedOrConnecting()*/) {
                    connection.connection_type = networkInfo.getTypeName();
                    connection.connection_subtype = networkInfo.getSubtypeName();

//                    connection.state = networkInfo.getState().toString();
//                    connection.reason = networkInfo.getReason();
//                    connection.extra = networkInfo.getExtraInfo();
//                    connection.failover = networkInfo.isFailover();
//                    connection.available = networkInfo.isAvailable();
//                    connection.roaming = networkInfo.isRoaming();

                    switch (networkInfo.getType()) {
                        case ConnectivityManager.TYPE_MOBILE:
                            TelephonyManager telephonyManager = ((TelephonyManager) context
                                    .getSystemService(TELEPHONY_SERVICE));
                            if (telephonyManager != null) {
                                connection.operator = telephonyManager.getNetworkOperatorName();

                            }
                            return connection;


                        case ConnectivityManager.TYPE_WIFI:

                            WifiManager wifiManager = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
                            if (wifiManager != null) {

                                WifiInfo cc = wifiManager.getConnectionInfo();
                                connection.SSID = cc.getSSID();//"" empty
                                connection.BSSID = cc.getBSSID();//02:00:00:00:00:00
                                //connection.mMacAddress = NotUsed.getWifiMac(cc);02:00:00:00:00:00
                                connection.mRssi = cc.getRssi();
                                connection.mLinkSpeed = cc.getLinkSpeed();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    connection.mTxLinkSpeed = cc.getTxLinkSpeedMbps();
                                    connection.mRxLinkSpeed = cc.getRxLinkSpeedMbps();
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    connection.mFrequency = cc.getFrequency();
                                }
                                connection.mNetworkId = cc.getNetworkId();
                                //connection.mMeteredHint=cc.getMeteredHint();
                                //connection.mNetworkId=cc.get();
                                // Level of current connection
                                int numberOfLevels = 5;
                                int level = WifiManager.calculateSignalLevel(cc.getRssi(), numberOfLevels);
                                DLog.d("!!!!: " + cc.toString());
                            }
                            return connection;
                        default:
                            return connection;
                    }
                }
            }
            DLog.e("Error when getting connection info: ConnectivityManager is null");
            return null;
        } catch (Exception ex) {
            DLog.e("Error when getting connection info" + ex);
            return null;
        }
    }

    public static NetworkInterface getLocalNetworkInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    if (!enumIpAddr.nextElement().isLoopbackAddress()) {
                        return networkInterface;
                    }
                }
            }
        } catch (SocketException ex) {
            DLog.handleException(ex);
        }
        return null;
    }

    private static String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }
}
