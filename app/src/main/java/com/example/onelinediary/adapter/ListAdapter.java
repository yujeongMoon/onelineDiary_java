package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.BasicItemBtnViewHolder;
import com.example.onelinediary.adapter.viewholder.BasicItemSwitchViewHolder;
import com.example.onelinediary.adapter.viewholder.ItemImageTextViewHolder;
import com.example.onelinediary.adapter.viewholder.ItemTextViewHolder;
import com.example.onelinediary.databinding.ViewholderItemBasicBtnBinding;
import com.example.onelinediary.databinding.ViewholderItemBasicSwitchBinding;
import com.example.onelinediary.databinding.ViewholderItemImageTextBinding;
import com.example.onelinediary.databinding.ViewholderItemTextBinding;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.ImageTextItem;
import com.example.onelinediary.dto.Item;
import com.example.onelinediary.dto.TextItem;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public enum itemType {
        TEXT(0), IMAGE_TEXT(1), BASIC_BTN(2), BASIC_SWITCH(3);

        public final int value;
        itemType(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public ArrayList<Item> items = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == itemType.TEXT.value) {
            ViewholderItemTextBinding binding = ViewholderItemTextBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemTextViewHolder(binding);
        } else if (viewType == itemType.BASIC_BTN.value) {
            ViewholderItemBasicBtnBinding binding = ViewholderItemBasicBtnBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new BasicItemBtnViewHolder(binding);
        } else if (viewType == itemType.BASIC_SWITCH.value) {
            ViewholderItemBasicSwitchBinding binding = ViewholderItemBasicSwitchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new BasicItemSwitchViewHolder(binding);
        }
        else if (viewType == itemType.IMAGE_TEXT.value) {
            ViewholderItemImageTextBinding binding = ViewholderItemImageTextBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemImageTextViewHolder(binding);
        } else {
            // not found view holder type
            throw new IllegalStateException("not found view holder type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemTextViewHolder) {
            ((ItemTextViewHolder) holder).onBind((TextItem)items.get(position));
        } else if (holder instanceof BasicItemBtnViewHolder) {
            ((BasicItemBtnViewHolder) holder).onBind((BasicItemBtn)items.get(position));
        } else if (holder instanceof BasicItemSwitchViewHolder) {
            ((BasicItemSwitchViewHolder) holder).onBind((BasicItemSwitch)items.get(position));
        } else if (holder instanceof ItemImageTextViewHolder) {
            ((ItemImageTextViewHolder) holder).onBind((ImageTextItem) items.get(position));
        } else {
            // not found view holder type
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = items.get(position);
        int type = -1;

        if (item instanceof TextItem) {
            type = itemType.TEXT.value;
        } else if (item instanceof BasicItemBtn) {
            type = itemType.BASIC_BTN.value;
        } else if (item instanceof BasicItemSwitch) {
            type = itemType.BASIC_SWITCH.value;
        } else if (item instanceof ImageTextItem) {
            type = itemType.IMAGE_TEXT.value;
        } else {
            // not found view holder type
        }

        return type;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Item item) {
        items.add(item);
        this.notifyDataSetChanged();
    }

    public void updateItem(int position, Item item) {
        items.set(position, item);
        // 포지션과 객체를 같이 넘기는 notifyItemChanged()를 사용하면 애니메이션 없이 해당 위치의 아이템을 변경할 수 있다.
        this.notifyItemChanged(position, item);
    }
}
