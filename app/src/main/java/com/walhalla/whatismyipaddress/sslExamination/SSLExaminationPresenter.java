package com.walhalla.whatismyipaddress.sslExamination;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.header.Header2Item;
import com.walhalla.whatismyipaddress.adapter.header.HeaderItem;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.x500.X500Principal;

public class SSLExaminationPresenter extends BasePresenter {
    private ArrayList<ViewModel> models;

    private final View view;
    private final SharedPreferences preferences;

    public SSLExaminationPresenter(Context context, View view, Handler handler) {
        super(handler);
        this.view = view;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void examinate(String rawUrl) {

        String url = "";
        String host;
        int port;

        if (rawUrl.startsWith("https://")) {
            try {
                URL u = new URL(rawUrl);
                host = u.getHost();
                port = u.getPort();
                url = rawUrl;
            } catch (Throwable t) {
                throw new IllegalArgumentException("Malformed URL (HTTPS)");
            }
        } else if (rawUrl.startsWith("http://")) {
            try {
                url = rawUrl.replace("http://", "https://");
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                throw new IllegalArgumentException("Malformed URL (HTTPS)");
            }
        } else if (rawUrl.startsWith("//")) {
            try {
                url = rawUrl.replace("//", "https://");
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                throw new IllegalArgumentException("Malformed URL (HTTPS)");
            }
        } else {
            try {
                url = "https://" + rawUrl;
                URL u = new URL(url);
                host = u.getHost();
                port = u.getPort();
            } catch (Throwable t) {
                throw new IllegalArgumentException("Malformed URL (HTTPS)");
            }
        }

        models = new ArrayList<>();
        String finalUrl = url;
        executor.execute(() -> {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(finalUrl).openConnection();
                connection.connect();
                //connection.getLocalCertificates().toString()

//                SSLSocketFactory sslSocketFactory = connection.getSSLSocketFactory();
//                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();
//                sslSocket.connect(connection.getURL().toURI().getHost(), 443);
//                SSLSession sslSession = sslSocket.getSession();
//                String[] cipherSuites = sslSession.getCipherSuites();

//                SSLSession sslSession = connection.getSSLSession();
//                String[] cipherSuites = sslSession.getCipherSuite();
//                for (String cipherSuite : cipherSuites) {
//                    Log.d(TAG, "Cipher Suite: " + cipherSuite);
//                }
                Certificate[] certificates = connection.getServerCertificates();

                for (int i = 0; i < certificates.length; i++) {
                    Certificate certificate = certificates[i];
                    if (certificate instanceof X509Certificate) {
                        X509Certificate x509Certificate = (X509Certificate) certificate;
                        int j = i + 1;
                        models.add(new HeaderItem("Certificate Chain " + j));

                        Date currentDate = new Date();


                        //return thisX500Name
                        //models.add(new TwoColItem("Subject DN: ", x509Certificate.getSubjectDN().toString()));


                        //models.add(new TwoColItem("@@@Issuer DN: ", x509Certificate.getIssuerDN().toString()));

//                        X500Principal issuerPrincipal = x509Certificate.getIssuerX500Principal();
//                        String issuerDNString = issuerPrincipal.getName();
//                        String[] fields = issuerDNString.split(",");

                        Principal mm = x509Certificate.getSubjectDN();
                        String name = mm.getName();
                        String[] fields = name.split(",");

                        for (String field : fields) {
                            if (field.startsWith("C=")) {
                                models.add(new TwoColItem("Issuer Company Country:", field.trim(), R.color.colorPrimaryDark));
                            } else if (field.startsWith("O=")) {
                                models.add(new TwoColItem("Issuer Company Name:", field.trim(), R.color.colorPrimaryDark));
                            } else if (field.startsWith("CN=")) {
                                models.add(new TwoColItem("Subject DN:", field.trim(), R.color.colorPrimaryDark));
                            } else {
                                models.add(new TwoColItem("@" + i + "@ IssuerDN Field:", field.trim(), R.color.error));
                            }
                        }

                        models.add(new TwoColItem("Serial Number:", x509Certificate.getSerialNumber().toString()));

                        if (currentDate.before(x509Certificate.getNotBefore())) {
                            models.add(new Header2Item(R.string.cert_not_yet_valid, R.drawable.ic_cert_false));
                        } else if (currentDate.after(x509Certificate.getNotAfter())) {
                            models.add(new Header2Item(R.string.cert_has_expired, R.drawable.ic_cert_false));
                        } else {
                            models.add(new Header2Item(R.string.cert_valid, R.drawable.ic_cert_success));
                        }

                        models.add(new TwoColItem("Valid From: ", formatDate(x509Certificate.getNotBefore())));
                        models.add(new TwoColItem("Valid Until: ", formatDate(x509Certificate.getNotAfter())));

                        models.add(new TwoColItem("Signature Algorithm: ", x509Certificate.getSigAlgName()));
                        models.add(new TwoColItem("SHA-256 Thumbprint: ", getThumbprint(x509Certificate, "SHA-256")));
                        models.add(new TwoColItem("SHA-1 Thumbprint: ", getThumbprint(x509Certificate, "SHA-1")));
                    }
                }
            } catch (IOException | CertificateEncodingException | NoSuchAlgorithmException e) {
                models.add(new TwoColItem("Error examining SSL certificate", e.toString()));
            }

            handler.post(() -> {
                view.hideProgress();
                view.successResult(models);
            });
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    private String getThumbprint(X509Certificate certificate, String algorithm) throws CertificateEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] encodedCert = certificate.getEncoded();
        byte[] thumbprint = md.digest(encodedCert);
        return toHex(thumbprint);
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public interface View {
        void showProgress();

        void hideProgress();

        void successResult(ArrayList<ViewModel> dataModels);
    }
}
