package com.aaronjwood.portauthority.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wandroid.traceroute.R;
import com.aaronjwood.portauthority.network.Host;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHolder> {
    private final List<Host> data;
    private OnItemClickListener listener;

    public HostAdapter(List<Host> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.host_list_item, parent, false);
        return new HostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder holder, int position) {
        Host item = data.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void sortDataByHostname() {
        Collections.sort(data, (lhs, rhs) -> rhs.getHostname().toLowerCase().compareTo(lhs.getHostname().toLowerCase()));
        notifyDataSetChanged();
    }

    public void sortDataByHostnameLR() {
        Collections.sort(data, (lhs, rhs) -> lhs.getHostname().toLowerCase().compareTo(rhs.getHostname().toLowerCase()));
        notifyDataSetChanged();
    }

    public void sortDataByVendorAscending() {
        Collections.sort(data, (lhs, rhs) -> rhs.getVendor().toLowerCase().compareTo(lhs.getVendor().toLowerCase()));
        notifyDataSetChanged();
    }

    public void sortDataByVendorDescending() {
        Collections.sort(data, (lhs, rhs) -> lhs.getVendor().toLowerCase().compareTo(rhs.getVendor().toLowerCase()));
        notifyDataSetChanged();
    }

    public Host getItem(int i) {
        return data.get(i);
    }

    public interface OnItemClickListener {
        void onItemClick(Host dataModel);
    }

    public void sortDataByIpAddressRL() {
        Collections.sort(data, (lhs, rhs) -> {
            int leftIp = ByteBuffer.wrap(lhs.getAddress()).getInt();
            int rightIp = ByteBuffer.wrap(rhs.getAddress()).getInt();
            return rightIp - leftIp;
        });
        notifyDataSetChanged();
    }

    public void sortDataByIpAddressLR() {
        Collections.sort(data, (lhs, rhs) -> {
            int leftIp = ByteBuffer.wrap(lhs.getAddress()).getInt();
            int rightIp = ByteBuffer.wrap(rhs.getAddress()).getInt();
            return leftIp - rightIp;
        });
        notifyDataSetChanged();
    }

    public static class HostViewHolder extends RecyclerView.ViewHolder {
        private TextView hostname;
        private TextView hostIp;
        private TextView hostMac;
        private TextView hostMacVendor;

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
            hostname = itemView.findViewById(R.id.hostname);
            hostIp = itemView.findViewById(R.id.hostIp);
            hostMac = itemView.findViewById(R.id.hostMac);
            hostMacVendor = itemView.findViewById(R.id.hostMacVendor);
        }

        public void bind(Host item) {
            hostname.setText(item.getHostname());
            hostIp.setText(item.getIp());
            hostMac.setText(item.getMac());
            hostMacVendor.setText(item.getVendor());
        }
    }
}
