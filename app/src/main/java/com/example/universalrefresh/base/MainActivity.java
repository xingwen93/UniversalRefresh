package com.example.universalrefresh.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.universalrefresh.R;
import com.example.universalrefresh.listview.Item;
import com.example.universalrefresh.listview.ItemGroup;
import com.example.universalrefresh.listview.ItemMember;
import com.example.universalrefresh.listview.MyAdapter;
import com.example.universalrefresh.listview.RefreshableListView;
import com.example.universalrefresh.scrollview.RefreshableScrollView;
import com.example.universalrefresh.scrollview.UnScrollableListView;

public class MainActivity extends Activity implements OnItemClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        testListView();
//        testScrollView();
	}

    public void testListView() {
        setContentView(R.layout.activity_main_list_view);
        RefreshableListView refreshableListView = (RefreshableListView) findViewById(R.id.list_view);
        refreshableListView.getRefreshContentView().setOnItemClickListener(this);
        refreshableListView.getRefreshContentView().setShadowVisible(true);
        refreshableListView.setOnRefreshListener(new RefreshableWrapper.OnRefreshListener() {
            @Override
            public void onPull(int refreshType, int maxValue, int offset) {
            }

            @Override
            public void onRefreshing(int refreshType) {
            }

            @Override
            public void onRefreshComplete(int refreshType) {
            }
        });

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

        MyAdapter adapter = new MyAdapter(this, list);
        refreshableListView.getRefreshContentView().setAdapter(adapter);
    }

    public void testScrollView() {
        RefreshableScrollView refreshableScrollView = new RefreshableScrollView(this);
        setContentView(refreshableScrollView);
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_main_scroll_view, null);
        refreshableScrollView.getRefreshContentView().addView(contentView);
        UnScrollableListView listView = (UnScrollableListView) contentView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        List<Item> list = new ArrayList<Item>();
        final int groupCount = 10;

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

        MyAdapter adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemMember member = (ItemMember) parent.getAdapter().getItem(position);
        Toast.makeText(this, member.getText(), Toast.LENGTH_SHORT).show();
	}
}