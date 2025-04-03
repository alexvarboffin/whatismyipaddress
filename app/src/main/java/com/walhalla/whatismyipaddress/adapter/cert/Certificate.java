package com.walhalla.whatismyipaddress.adapter.cert;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

@Keep
public class Certificate  extends ViewModel {
    @SerializedName("issuer_ca_id")
    @Expose
    private Integer issuerCaId;
    @SerializedName("issuer_name")
    @Expose
    private String issuerName;
    @SerializedName("common_name")
    @Expose
    private String commonName;
    @SerializedName("name_value")
    @Expose
    private String nameValue;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("entry_timestamp")
    @Expose
    private String entryTimestamp;
    @SerializedName("not_before")
    @Expose
    private String notBefore;
    @SerializedName("not_after")
    @Expose
    private String notAfter;
    @SerializedName("serial_number")
    @Expose
    private String serialNumber;
    @SerializedName("result_count")
    @Expose
    private Integer resultCount;

    public Integer getIssuerCaId() {
        return issuerCaId;
    }

    public void setIssuerCaId(Integer issuerCaId) {
        this.issuerCaId = issuerCaId;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(String entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    @Override
    public int getType() {
        return TYPE_ITEM_CERT;
    }
}
