package com.example.universalrefresh.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.universalrefresh.R;
import com.example.universalrefresh.listview.Item;
import com.example.universalrefresh.listview.ItemGroup;
import com.example.universalrefresh.listview.ItemMember;
import com.example.universalrefresh.listview.MyAdapter;
import com.example.universalrefresh.scrollview.ScrollViewWrapper;
import com.example.universalrefresh.scrollview.UnScrollableListView;

import java.util.ArrayList;
import java.util.List;

public class TestScrollViewActivity extends Activity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollViewWrapper refreshableScrollView = new ScrollViewWrapper(this);
        setContentView(refreshableScrollView);
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_test_scroll_view_content, null);
        refreshableScrollView.getContentView().addView(contentView);
        UnScrollableListView listView = (UnScrollableListView) contentView.findViewById(R.id.list_view_wrapper);
        listView.setOnItemClickListener(this);
        List<Item> list = new ArrayList<Item>();
        final int groupCount = 12;

        for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
            ItemGroup group = new ItemGroup();
            group.setGroupName(String.valueOf(groupPosition));
            list.add(group);

            for (int memberPosition = 0; memberPosition < 5; memberPosition++) {
                ItemMember member = new ItemMember();
                member.setText(groupPosition + "-" + memberPosition);
                list.add(member);
            }
        }

        MyAdapter adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getAdapter().getItem(position);
        if (item instanceof ItemMember) {
            ItemMember member = (ItemMember) item;
            Toast.makeText(this, member.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
