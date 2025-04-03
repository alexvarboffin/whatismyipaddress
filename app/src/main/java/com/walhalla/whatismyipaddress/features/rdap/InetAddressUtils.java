package com.walhalla.whatismyipaddress.features.rdap;


import com.walhalla.ui.DLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class InetAddressUtils {


    /**
     *
     * 8.8.8.8 = true
     * https://8.8.8.8 = false
     * http://8.8.8.8 = false
     *
     */
//    public static boolean isIPv4(final String ip) {
//        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
//
//        return ip.matches(PATTERN);
//    }


    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final Pattern IPV6_STD_PATTERN =
            Pattern.compile(
                    "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
            Pattern.compile(
                    "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    private static final String KEY_SRV = "services";

    public static boolean isIPv4Address(final String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6StdAddress(final String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6HexCompressedAddress(final String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6Address(final String input) {
        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
    }
    
    
    
    //==================================================
    //===== ipv4 or ipv6
    public static String resolveRdapFromIp(String jsonText, String ip) {
        try {
            // Преобразуем JSON строку в объект
            JSONObject jsonObject = new JSONObject(jsonText);
            JSONArray services = jsonObject.getJSONArray(KEY_SRV);

            // Преобразуем IP в числовое представление
            BigInteger ipNumeric = ipToNumeric(ip);

            // Проходим по всем сервисам
            for (int i = 0; i < services.length(); i++) {
                JSONArray service = services.getJSONArray(i);
                JSONArray ipRanges = service.getJSONArray(0);
                JSONArray urls = service.getJSONArray(1);

                // Проверяем каждый диапазон IP
                for (int j = 0; j < ipRanges.length(); j++) {
                    String ipRange = ipRanges.getString(j);

                    //DLog.d("[ip-range]"+ipRange);

                    if (isIpInRange(ipRange, ipNumeric)) {
                        return urls.getString(0); // Возвращаем первый URL
                    }
                }
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
        return null; // Если не найден подходящий сервер
    }

    private static BigInteger ipToNumeric(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] bytes = inetAddress.getAddress();
        return new BigInteger(1, bytes);
    }

    private static boolean isIpInRange(String range, BigInteger ipNumeric) throws UnknownHostException {
        String[] parts = range.split("/");
        String ipStart = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        BigInteger ipStartNumeric = new BigInteger(1, InetAddress.getByName(ipStart).getAddress());
        BigInteger mask = BigInteger.valueOf(2).pow(32).subtract(BigInteger.valueOf(2).pow(32 - prefixLength));

        return ipStartNumeric.and(mask).equals(ipNumeric.and(mask));
    }


    //    Метод resolve принимает JSON в качестве строки и ищет RDAP-сервер для
    //    заданного имени сайта. В данном примере используется имя сайта "music". Вы можете изменить значение переменной siteName на нужное вам имя сайта.

    public static String resolveRdapForDomain(String jsonText, String siteName) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonText);
            JSONArray services = jsonObject.getJSONArray(KEY_SRV);

            String rdapServerUrl = null;

            for (int i = 0; i < services.length(); i++) {
                JSONArray service = services.getJSONArray(i);
                JSONArray serviceNameArray = service.getJSONArray(0);
                JSONArray serverUrlArray = service.getJSONArray(1);

                String serviceName = serviceNameArray.getString(0);
                String serverUrl = serverUrlArray.getString(0);

                if (serviceName.equals(siteName)) {
                    DLog.d("@@" + serviceName + " " + siteName);
                    rdapServerUrl = serverUrl;
                    break;
                }
            }
            if (rdapServerUrl != null) {
                return rdapServerUrl;
            } else {
                return null;
            }
        } catch (JSONException e) {
            DLog.handleException(e);
            return null;
        }
    }
}
