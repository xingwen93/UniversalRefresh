package com.example.universalrefresh.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.universalrefresh.R;

import java.util.List;

/**
 * @author dwj  2016/7/4 16:32
 */
public class MyAdapter extends ArrayAdapter<Item> implements StickyListView.PinnedSectionListAdapter {

    private static final int[] COLORS = new int[]{Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED};

    public MyAdapter(Context context, List<Item> list) {
        super(context, 0, list);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = getItemViewType(position);

        if (viewType == Item.TYPE_GROUP) {
            GroupViewHolder groupViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_group, null);
                groupViewHolder = new GroupViewHolder(convertView);
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            ItemGroup group = (ItemGroup) getItem(position);
            groupViewHolder.tvGroup.setText(group.getGroupName());

            convertView.setBackgroundColor(COLORS[group.getGroupPosition() % COLORS.length]);
        } else {
            MemberViewHolder memberViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_member, null);
                memberViewHolder = new MemberViewHolder(convertView);
                convertView.setTag(memberViewHolder);
            } else {
                memberViewHolder = (MemberViewHolder) convertView.getTag();
            }
            ItemMember member = (ItemMember) getItem(position);
            memberViewHolder.tvMember.setText(member.getText());
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemViewType();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.TYPE_GROUP;
    }

    private class GroupViewHolder {
        TextView tvGroup;

        public GroupViewHolder(View convertView) {
            tvGroup = (TextView) convertView.findViewById(R.id.tv_group_name);
        }
    }

    private class MemberViewHolder {
        TextView tvMember;

        public MemberViewHolder(View convertView) {
            tvMember = (TextView) convertView.findViewById(R.id.tv_member);
        }
    }
}
