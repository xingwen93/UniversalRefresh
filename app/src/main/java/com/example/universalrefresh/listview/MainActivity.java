package com.example.universalrefresh.listview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universalrefresh.R;
import com.example.universalrefresh.listview.StickyListView.PinnedSectionListAdapter;

public class MainActivity extends Activity implements OnItemClickListener {
	
	StickyListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list_view);
		
		Log.w("MainActivity", "cache: " + getCacheDir().getAbsolutePath());
		Log.w("MainActivity", "database: " + getDatabasePath("temp").getAbsolutePath());
		Log.w("MainActivity", "file: " + getFilesDir().getAbsolutePath());
		
		listView = (StickyListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(this);
		listView.setShadowVisible(false);
		
		TextView headerView1 = new TextView(this);
		headerView1.setText("这是第一个头布局");
		headerView1.setTextSize(28f);
		listView.addHeaderView(headerView1);
		
		TextView headerView2 = new TextView(this);
		headerView2.setText("这是第二个头布局");
		headerView2.setTextSize(28f);
		listView.addHeaderView(headerView2);
		
		TextView headerView3 = new TextView(this);
		headerView3.setText("这是第三个头布局");
		headerView3.setTextSize(28f);
		listView.addHeaderView(headerView3);
		
		MyAdapter adapter = new MyAdapter(this, generateDataset());
		listView.setAdapter(adapter);
		
	}
	
	public List<Item> generateDataset() {
		List<Item> list = new ArrayList<Item>();
				
		final int groupCount = 20;

		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
			ItemGroup group = new ItemGroup();
			group.setGroupName(String.valueOf(groupPosition));
			group.setGroupPosition(groupPosition);
			list.add(group);

			final int memberCount = (int) (Math.random() * 5 + 1);
			for (int memberPosition = 0; memberPosition < memberCount; memberPosition++) {
				ItemMember member = new ItemMember();
				member.setGroupPosition(groupPosition);
				member.setMemberPosition(memberPosition);
				member.setText(groupPosition + "-" + memberPosition);
				list.add(member);
			}
		}
		
		return list;
	}
	
	static class MyAdapter extends ArrayAdapter<Item> implements PinnedSectionListAdapter {

		private static final int[] COLORS = new int[] { Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED };

		public MyAdapter(Context context, List<Item> list) {
			super(context, 0, list);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HeaderViewListAdapter adapter = (HeaderViewListAdapter) parent.getAdapter();
		int headerCount = adapter.getHeadersCount();
		if (position < headerCount) {
			TextView tv = (TextView) view;
			Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
		} else {
			position = position - headerCount;
			MyAdapter myAdapter = (MyAdapter) adapter.getWrappedAdapter();
			if (myAdapter.getItemViewType(position) == Item.TYPE_GROUP) {
//				ItemGroup group = (ItemGroup) myAdapter.getItem(position);
//				Toast.makeText(this, "Group: " + group.getGroupName(), Toast.LENGTH_SHORT).show();
			} else {
				ItemMember member = (ItemMember) myAdapter.getItem(position);
				Toast.makeText(this, member.getText(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}