package com.walhalla.whatismyipaddress.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walhalla.ui.DLog;

import com.walhalla.ui.plugins.Launcher;
import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.adapter.entity.CountryObj;

import com.walhalla.whatismyipaddress.ui.adapter.entity.GooglePlayViewModel;
import com.walhalla.whatismyipaddress.ui.adapter.entity.Header0;
import com.walhalla.whatismyipaddress.ui.adapter.entity.Header1;
import com.walhalla.whatismyipaddress.ui.adapter.exobj.ExtendedViewHolder;
import com.walhalla.whatismyipaddress.ui.adapter.map.MapObj;
import com.walhalla.whatismyipaddress.ui.adapter.map.MapViewHolder;
import com.walhalla.whatismyipaddress.ui.adapter.menu.MSpeedTest;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ToolsMenu;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;
import com.walhalla.whatismyipaddress.ui.adapter.exobj.ExObj;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ExObj2;
import com.walhalla.whatismyipaddress.ui.adapter.entity.GatewayObj2;
import com.walhalla.whatismyipaddress.ui.adapter.entity.MyIpObj;

import java.util.ArrayList;
import java.util.List;

public class ComplexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SIMPLE = R.layout.note_list_row;
    private static final int TYPE_EXTENDED_ = R.layout.note_list_simple;
    private static final int TYPE_MAP_OBJ_ = 443;

    private static final int TYPE_MENU_TOOLS = R.layout.item_menu_simple;
    private static final int TYPE_SPEED_TEST = R.layout.item_speed_test;

    private static final int TYPE_MY_IP = R.layout.note_list_row_ip;
    private static final int TYPE_GATEWAY = R.layout.note_list_row_gateway;
    private static final int TYPE_GOOGLE_PLAY = R.layout.note_list_google_play;
    private static final int TYPE_NONE = android.R.layout.simple_list_item_1;
    private static final int TYPE_COUNTRY = R.layout.item_country;


    private static final int TYPE_HEADER_0 = R.layout.row_header;
    private static final int TYPE_HEADER_1 = R.layout.item_header_view;


    private final List<ViewModel0> data;
    private CallbackDefault callbackDefault;
    private Callback2Menu callback2Menu;

    private final Context mContext;

    private final String err_not_available;

    public ComplexAdapter(List<ViewModel0> data, Context context) {
        this.data = (data == null) ? new ArrayList<>() : data;
        mContext = context;
        err_not_available = context.getString(R.string.err_not_available);
    }


    public void setDefaultCallback(CallbackDefault callback) {
        this.callbackDefault = callback;
    }

    public void setMenuCallback(Callback2Menu callback) {
        this.callback2Menu = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_MAP_OBJ_) {
            viewHolder = new MapViewHolder(
                    inflater.inflate(R.layout.note_list_simple, parent, false), err_not_available
            );

            //999
        } else if (viewType == TYPE_COUNTRY) {
            viewHolder = new ExtendedViewHolder(
                    inflater.inflate(R.layout.item_country, parent, false), err_not_available
            );

            //000
        } else if (viewType == TYPE_EXTENDED_) {
            View view = inflater.inflate(R.layout.note_list_simple, parent, false);
            viewHolder = new ExtendedViewHolder(view, err_not_available);
        } else if (viewType == TYPE_MENU_TOOLS) {
            viewHolder = new MenuItemViewHolder(inflater.inflate(R.layout.item_menu_simple, parent, false), callback2Menu);
        } else if (viewType == TYPE_SPEED_TEST) {
            viewHolder = new SpeedViewHolder(inflater.inflate(TYPE_SPEED_TEST, parent, false), callback2Menu);
        } else if (viewType == TYPE_MY_IP) {
            View view;
            view = inflater.inflate(R.layout.note_list_row_ip, parent, false);
            viewHolder = new IpViewHolder(view);
        } else if (viewType == TYPE_GATEWAY) {
            View view;
            view = inflater.inflate(R.layout.note_list_row_gateway, parent, false);
            viewHolder = new GatewayViewHolder(view);
        } else if (viewType == TYPE_SIMPLE) {
            View view;
            view = inflater.inflate(R.layout.note_list_row, parent, false);
            viewHolder = new SimpleViewHolder(view);
        } else if (viewType == TYPE_HEADER_0) {
            View view;
            view = inflater.inflate(TYPE_HEADER_0, parent, false);
            viewHolder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_HEADER_1) {
            View view;
            view = inflater.inflate(TYPE_HEADER_1, parent, false);
            viewHolder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_GOOGLE_PLAY) {
            View view;
            view = inflater.inflate(R.layout.note_list_google_play, parent, false);
            viewHolder = new GooglePlayViewHolder(view);
        } else {
            View view;
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder = new RecyclerViewSimpleTextViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewModel0 obj = data.get(position);

        if (holder.getItemViewType() == TYPE_MAP_OBJ_) {
            MapViewHolder holder41 = (MapViewHolder) holder;
            holder41.bind(data.get(position), callbackDefault, mContext);
        } else if (holder.getItemViewType() == TYPE_COUNTRY) {
            ExtendedViewHolder holder4 = (ExtendedViewHolder) holder;
            holder4.bind(data.get(position), callbackDefault, true, mContext);
        } else if (holder.getItemViewType() == TYPE_EXTENDED_) {
            ExtendedViewHolder extendedViewHolder = (ExtendedViewHolder) holder;
            extendedViewHolder.bind(data.get(position), callbackDefault, false, mContext);
        } else if (holder.getItemViewType() == TYPE_MENU_TOOLS) {
            MenuItemViewHolder extendedViewHolder0 = (MenuItemViewHolder) holder;
            extendedViewHolder0.bind(data.get(position), mContext);
        } else if (holder.getItemViewType() == TYPE_SPEED_TEST) {
            ((SpeedViewHolder) holder).bind(data.get(position), mContext);
        } else if (holder.getItemViewType() == TYPE_MY_IP) {
            IpViewHolder holder1 = (IpViewHolder) holder;
            holder1.bind(obj);

            //Open web browser
            if (mContext.getString(R.string.err_not_available).equals(obj.content) && !BuildConfig.DEBUG) {
                ((IpViewHolder) holder).web.setVisibility(View.GONE);

                //small
                holder1.icon.setOnClickListener(v ->
                        callbackDefault.copyToClipboardPressed(v, obj.content));
            } else {
                ((IpViewHolder) holder).web.setVisibility(View.VISIBLE);
                holder1.web.setOnClickListener(v -> {
                    callbackDefault.makeIpInfo(v, obj);
                });

                //full
                holder1.icon.setOnClickListener(v ->
                        callbackDefault.publicIpMenuPressed(v, obj.content));
            }
        } else if (holder.getItemViewType() == TYPE_GATEWAY) {
            GatewayViewHolder holder3 = (GatewayViewHolder) holder;
            holder3.bind(obj);
            holder3.icon.setOnClickListener(v -> callbackDefault.copyToClipboardPressed(v, obj.content));
            holder3.web.setOnClickListener(v -> callbackDefault.makeGateway(obj));
        } else if (holder.getItemViewType() == TYPE_GOOGLE_PLAY) {
            GooglePlayViewHolder holder2 = (GooglePlayViewHolder) holder;
            holder2.bind((GooglePlayViewModel) obj);
        } else if (holder.getItemViewType() == TYPE_SIMPLE) {
            SimpleViewHolder simpleViewHolder = (SimpleViewHolder) holder;
            simpleViewHolder.bind(data.get(position));
        } else if (holder.getItemViewType() == TYPE_HEADER_0) {
            HeaderViewHolder holder5 = (HeaderViewHolder) holder;
            holder5.bind(data.get(position));
        } else if (holder.getItemViewType() == TYPE_HEADER_1) {
            ((HeaderViewHolder) holder).bind(data.get(position));
        } else {
            RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) holder;
            configureDefaultViewHolder(vh, position);
        }
    }

    private void configureDefaultViewHolder(RecyclerViewSimpleTextViewHolder vh, int position) {
        vh.text1.setText((CharSequence) data.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        ViewModel0 viewModel = data.get(position);
        if (viewModel instanceof MyIpObj) {
            return TYPE_MY_IP;
        } else if (viewModel instanceof ExObj) {
            return TYPE_EXTENDED_;
        } else if (viewModel instanceof ToolsMenu) {
            return TYPE_MENU_TOOLS;
        } else if (viewModel instanceof MSpeedTest) {
            return TYPE_SPEED_TEST;
        } else if (viewModel instanceof ExObj2) {
            return TYPE_SIMPLE;
        } else if (viewModel instanceof Header0) {
            return TYPE_HEADER_0;
        } else if (viewModel instanceof Header1) {
            return TYPE_HEADER_1;
        } else if (viewModel instanceof GatewayObj2) {
            return TYPE_GATEWAY;
        } else if (viewModel instanceof GooglePlayViewModel) {
            return TYPE_GOOGLE_PLAY;
        } else if (viewModel instanceof CountryObj) {
            return TYPE_COUNTRY;
        } else if (viewModel instanceof MapObj) {
            return TYPE_MAP_OBJ_;
        } else {
            return TYPE_NONE;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void swap(List<ViewModel0> arr) {
        data.clear();
        data.addAll(arr);
        this.notifyDataSetChanged();
    }


    /**
     * ViewHolders
     */

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final TextView content;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.tv_label);
            content = itemView.findViewById(R.id.tv_description);
        }

        public void bind(ViewModel0 obj) {
            label.setText(obj.label);
            if (obj.content.isEmpty()) {
                content.setVisibility(View.GONE);
                return;
            }
            content.setText(obj.content);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public TextView content;

        HeaderViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.tv_label);
            content = itemView.findViewById(R.id.tv_description);
        }

        public void bind(ViewModel0 obj) {
            label.setText(obj.label);
            if (content != null) {
                content.setText(obj.content);
            }
        }
    }


    private static class IpViewHolder extends SimpleViewHolder {

        private final View icon;
        private final View web;

        IpViewHolder(View view) {
            super(view);

            icon = itemView.findViewById(R.id.icon);
            web = itemView.findViewById(R.id.web);
        }
    }

    private static class GatewayViewHolder extends SimpleViewHolder {

        private final View icon;
        private final View web;

        GatewayViewHolder(View view) {
            super(view);

            icon = itemView.findViewById(R.id.icon);
            web = itemView.findViewById(R.id.web);
        }
    }

    private static final class RecyclerViewSimpleTextViewHolder extends RecyclerView.ViewHolder {

        private final TextView text1;

        RecyclerViewSimpleTextViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }


    public static class GooglePlayViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ViewGroup rV;
        private final ImageView image;
        public TextView label;
        public TextView content;
        private GooglePlayViewModel obj;

        public GooglePlayViewHolder(View view) {
            super(view);
            label = itemView.findViewById(R.id.tv_label);
            content = itemView.findViewById(R.id.tv_description);
            image = itemView.findViewById(R.id.icon_market);
            rV = itemView.findViewById(R.id.rootView);
            rV.setOnClickListener(this);
        }

        public void bind(GooglePlayViewModel obj) {
            this.obj = obj;

            //label.setText(obj.label);
            SpannableString content = new SpannableString(obj.label);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            label.setText(content);
            try {
                image.setImageResource(obj.icon);
            } catch (Exception e) {
                DLog.handleException(e);
            }
            if (obj.content.isEmpty()) {
                this.content.setVisibility(View.GONE);
                return;
            }
            this.content.setText(obj.content);
        }

        @Override
        public void onClick(View view) {
            Launcher.openMarketApp(view.getContext(), obj.packageName);
        }
    }

    public interface CallbackDefault {

        void copyToClipboardPressed(View v, String content);

        void makeIpInfo(View v, ViewModel0 format);

        void makeGateway(ViewModel0 obj);


        void locationItemSelected(View v, String content);

        void publicIpMenuPressed(View v, String content);
    }

    public interface Callback2Menu {

        void menuItemSelected(int adapterPosition);
    }
}
