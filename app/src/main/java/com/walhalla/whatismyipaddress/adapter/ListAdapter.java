package com.walhalla.whatismyipaddress.adapter;

import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_2;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_CERT;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_CHECKHOST;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_HEADER;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_HEADER2;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_LNK0;
import static com.walhalla.whatismyipaddress.adapter.items.ViewModel.TYPE_ITEM_SINGLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.adapter.cert.CertViewHolder;
import com.walhalla.whatismyipaddress.adapter.cert.Certificate;
import com.walhalla.whatismyipaddress.adapter.chekhostitem.CheckHostItem;
import com.walhalla.whatismyipaddress.adapter.chekhostitem.CheckHostViewHolder;
import com.walhalla.whatismyipaddress.adapter.header.Header2Item;
import com.walhalla.whatismyipaddress.adapter.header.Header2ViewHolder;
import com.walhalla.whatismyipaddress.adapter.header.HeaderItem;
import com.walhalla.whatismyipaddress.adapter.header.HeaderViewHolder;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.lnk.LnkItem;
import com.walhalla.whatismyipaddress.adapter.lnk.LnkItemViewHolder;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItemViewHolder;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColViewHolder;
import com.walhalla.whatismyipaddress.databinding.ItemCertBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<ViewModel> models;
    private final Context context;
    private OnItemClickListener listener;

    public ListAdapter(Context context) {
        this.models = new ArrayList<>();
        this.context = context;
    }

    public List<ViewModel> getModels() {
        return models;
    }

    @Override
    public int getItemViewType(int position) {
        return models.get(position).getType();
    }

    public ListAdapter(List<ViewModel> data, Context context, String fromFragment) {
        this.models = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == TYPE_ITEM_2) {
            itemView = inflater.inflate(R.layout.list_item, parent, false);
            return new TwoColViewHolder(itemView);
        } else if (viewType == TYPE_ITEM_CHECKHOST) {
            itemView = inflater.inflate(R.layout.list_item_checkhost, parent, false);
            return new CheckHostViewHolder(itemView);
        } else if (viewType == TYPE_ITEM_HEADER) {
            itemView = inflater.inflate(R.layout.list_item_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_ITEM_HEADER2) {
            itemView = inflater.inflate(R.layout.list_item_header2, parent, false);
            return new Header2ViewHolder(itemView);
        } else if (viewType == TYPE_ITEM_LNK0) {
            itemView = inflater.inflate(R.layout.list_item_lnk, parent, false);
            return new LnkItemViewHolder(itemView);
        } else if (viewType == TYPE_ITEM_CERT) {
            @NonNull ItemCertBinding binding = ItemCertBinding.inflate(inflater, parent, false);
            return new CertViewHolder(binding);
        }

        //viewType == TYPE_ITEM_1
        itemView = inflater.inflate(R.layout.single_item, parent, false);
        return new SingleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewModel dataModel = models.get(position);

//        if (!fromFragment.equals("Dashboard")) {
//            Animation animation = AnimationUtils.loadAnimation(mContext, (
//                    holder.getAdapterPosition() > lastPosition)
//                    ? R.anim.up_from_bottom : R.anim.down_from_top);
//            holder.itemView.startAnimation(animation);
//            lastPosition = holder.getAdapterPosition();
//        }

        switch (holder.getItemViewType()) {

            case ViewModel.TYPE_ITEM_CHECKHOST:
                CheckHostItem item = (CheckHostItem) dataModel;
                CheckHostViewHolder checkHostViewHolder = (CheckHostViewHolder) holder;
                checkHostViewHolder.bind(item);
                checkHostViewHolder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onListItemClick(dataModel);
                    }
                });
//                if (item.isFirstLaunch()) {
////                    Glide.with(context)
////                            .asGif()
////                            .load(R.drawable.your_gif_resource)
////                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
////                            .into(checkHostViewHolder.gif);
//                    checkHostViewHolder.showProgress();
//                }
                break;

            case TYPE_ITEM_2:
                TwoColViewHolder h = (TwoColViewHolder) holder;
                h.bind((TwoColItem) dataModel);
                h.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onListItemClick(dataModel);
                    }
                });
                break;

            case TYPE_ITEM_HEADER:
                HeaderViewHolder holder1 = (HeaderViewHolder) holder;
                holder1.bind((HeaderItem) dataModel);
                break;

            case TYPE_ITEM_HEADER2:
                Header2ViewHolder holder11 = (Header2ViewHolder) holder;
                holder11.bind((Header2Item) dataModel);
                break;

            case TYPE_ITEM_LNK0:
                LnkItemViewHolder holder2 = (LnkItemViewHolder) holder;
                holder2.bind((LnkItem) dataModel);
                break;

            case TYPE_ITEM_CERT:
                CertViewHolder certViewHolder = (CertViewHolder) holder;
                certViewHolder.bind((Certificate) dataModel);
//                certViewHolder.itemView.setOnClickListener(v -> {
//                    if (listener != null) {
//                        listener.onListItemClick(dataModel);
//                    }
//                });
                //@@@
                certViewHolder.binding.overflowMenu.setOnClickListener(view ->
                        showPopupMenu(view, (Certificate) dataModel));
                break;

            case TYPE_ITEM_SINGLE:
                SingleItemViewHolder holder3 = (SingleItemViewHolder) holder;
                holder3.bind((SingleItem) dataModel);
                break;

            default:
                DLog.d("@@@@@@" + holder.getItemViewType());
                //SingleItemViewHolder checkHostViewHolder = (SingleItemViewHolder) holder;
                //checkHostViewHolder.bind((SingleItem) dataModel, colors.get(holder.getAdapterPosition()));

                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) holder;
                vh.bind(models.get(position).toString());
                break;
        }
    }

    private void showPopupMenu(View view, Certificate dataModel) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(view.getContext(), view);
        android.view.MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.popup_cert_item, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int itemId = menuItem.getItemId();
            //                case R.id.action_save_icon:
//                    if (mView != null) {
//                        mView.saveIconRequest(resource);
//                    }
//                    break;
            if (itemId == R.id.actionCopyCommonName) {
                if (listener != null) {
                    listener.copyToBuffer(dataModel.getCommonName());
                }
            } else if (itemId == R.id.actionCopyNameValue) {
                if (listener != null) {
                    listener.copyToBuffer(dataModel.getNameValue());
                }
            }
            return false;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void swap(List<ViewModel> dataModels) {
        models.clear();
        models.addAll(dataModels);
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onListItemClick(ViewModel dataModel);

        void copyToBuffer(String commonName);
    }

    private static final class RecyclerViewSimpleTextViewHolder extends RecyclerView.ViewHolder {

        private final TextView text1;

        RecyclerViewSimpleTextViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }

        public void bind(CharSequence charSequence) {
            text1.setText(charSequence);
        }
    }
}