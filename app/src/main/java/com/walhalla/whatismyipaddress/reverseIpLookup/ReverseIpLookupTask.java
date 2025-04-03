package com.walhalla.whatismyipaddress.reverseIpLookup;

import android.os.Handler;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.xbill.DNS.Address;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReverseIpLookupTask extends BasePresenter {
    private static final String LIVE_API_HOST = "easytaxi.com.br";
    private static final String LIVE_API_IP = "1.2.3.4";

    private boolean mInitialized;
    private InetAddress mLiveApiStaticIpAddress;
    private final View0 view;

    public ReverseIpLookupTask(View0 view, Handler handler) {
        super(handler);
        this.view = view;
    }

    public void lookup(String ipAddress) {

        String hostName = "2ip.ru";

        executor.execute(() -> {


            // I'm initializing the DNS resolvers here to take advantage of this method being called in a
            // background-thread managed by OkHttp
            init();

//            List<InetAddress> cc = null;
//            try {
//                cc = Collections.singletonList(Address.getByName(hostName));
//            } catch (UnknownHostException e) {
//                // fallback to the API's static IP
//                if (LIVE_API_HOST.equals(hostName) && mLiveApiStaticIpAddress != null) {
//                    cc = Collections.singletonList(mLiveApiStaticIpAddress);
//                } else {
//                    //throw e;
//                }
//            }
//
//            DLog.d("@@@" + cc);

            ArrayList<ViewModel> data = new ArrayList<>();
            List<String> domainNames = performReverseIpLookup(ipAddress);
            if (!domainNames.isEmpty()) {
                data.add(new SingleItem("Список доменных имен, связанных с IP-адресом " + ":", R.color.colorPrimaryDark));
                for (String domainName : domainNames) {
                    data.add(new SingleItem(domainName));
                }
            } else {
                data.add(new SingleItem("Reverse IP Lookup не удался или нет связанных доменных имен", R.color.colorPrimaryDark));
            }

            handler.post(() -> {
                view.hideProgress();
                view.displayScanResult(data);
            });
        });


    }

    private void init() {
        if (mInitialized) return;
        else mInitialized = true;

        try {
            mLiveApiStaticIpAddress = InetAddress.getByName(LIVE_API_IP);
        } catch (UnknownHostException e) {
            DLog.d("Couldn't initialize static IP address");
        }
        try {
            // configure the resolvers, starting with the default ones (based on the current network connection)
            Resolver defaultResolver = Lookup.getDefaultResolver();
            // use Google's public DNS services
            Resolver googleFirstResolver = new SimpleResolver("8.8.8.8");
            Resolver googleSecondResolver = new SimpleResolver("8.8.4.4");
            // also try using Amazon
            Resolver amazonResolver = new SimpleResolver("205.251.198.30");
            Lookup.setDefaultResolver(
                    new ExtendedResolver(new Resolver[]{
                            defaultResolver,
                            googleFirstResolver,
                            googleSecondResolver,
                            amazonResolver
                    })
            );
        } catch (UnknownHostException e) {
            DLog.d("Couldn't initialize custom resolvers");
        }
    }

    public static List<String> performReverseIpLookup(String ipAddress) {
        List<String> domainNames = new ArrayList<>();

        try {
            Name mm = ReverseMap.fromAddress(ipAddress);
            DLog.d("<>" + mm);
            Lookup lookup = new Lookup(mm, Type.PTR);
            //Lookup lookup = new Lookup(mm, Type.A);
            lookup.run();

            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (Record record : lookup.getAnswers()) {
                    if (record instanceof PTRRecord) {
                        PTRRecord ptrRecord = (PTRRecord) record;
                        String domainName = ptrRecord.getTarget().toString();
                        domainNames.add(domainName);
                    }
                }
            }
        } catch (UnknownHostException e) {
            DLog.handleException(e);
        }
        return domainNames;
    }

    public interface View0 {
        void showProgress();

        void hideProgress();

        void displayScanResult(ArrayList<ViewModel> dataModels);
        void displayScanProgress(String result);

        void handleException(String ipText, Exception e0);
    }
}
