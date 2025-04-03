package com.walhalla.whatismyipaddress.adapter.cert;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.databinding.ItemCertBinding;

public class CertViewHolder extends RecyclerView.ViewHolder {


    public final ItemCertBinding binding;

    public CertViewHolder(@NonNull ItemCertBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Certificate obj) {
        binding.textViewIssuerNameValue.setText(obj.getIssuerName());
        binding.textViewCommonNameValue.setText(obj.getCommonName());
        binding.textViewNameValue.setText(obj.getNameValue());


        binding.textViewEntryTimestampValue.setText(obj.getEntryTimestamp());

        binding.textViewNotBeforeValue.setText(obj.getNotBefore());
        binding.textViewNotAfterValue.setText(obj.getNotAfter());

        binding.textViewIdValue.setText(String.valueOf(obj.getId()));

        binding.textViewSerialNumberValue.setText(obj.getSerialNumber());
        binding.textViewResultCountValue.setText(String.valueOf(obj.getResultCount()));
    }
}