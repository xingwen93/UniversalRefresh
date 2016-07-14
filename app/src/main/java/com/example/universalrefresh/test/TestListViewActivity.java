package com.example.universalrefresh.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.universalrefresh.R;
import com.example.universalrefresh.base.BaseWrapper;
import com.example.universalrefresh.listview.*;

import java.util.ArrayList;
import java.util.List;

public class TestListViewActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListViewWrapper wrapper;
    private StickyListView listView;
    private MyAdapter adapter;

    private final int PAGE_COUNT = 4;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_listview);
        wrapper = (ListViewWrapper) findViewById(R.id.list_view_wrapper);
        wrapper.setOnOperatingListener(new BaseWrapper.OnOperatingListener() {

            ProgressDialog progress;

            @Override
            public void onPulling(int operationType, int maxvalue, int absValue) {

            }

            @Override
            public void onExecuting(int operationType) {
                if (progress == null) {
                    progress = new ProgressDialog(TestListViewActivity.this);
                    progress.setMessage("正在加载...");
                }
                progress.show();

                if (operationType == ListViewWrapper.OPERATION_PULL_DOWN) {
                    wrapper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refresh(totalPages);
                            wrapper.completeExecuting();
                        }
                    }, 1500);
                } else if (operationType == ListViewWrapper.OPERATION_PULL_UP) {
                    wrapper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMore(++totalPages);
                            wrapper.completeExecuting();
                        }
                    }, 1500);
                }
            }

            @Override
            public void onComplete(int operationType) {
                if (progress != null) {
                    progress.dismiss();
                }
            }
        });
        listView = wrapper.getContentView();
        listView.setOnItemClickListener(this);
        listView.setShadowVisible(true);

        refresh(totalPages);
    }

    void refresh(int totalPages) {
        List<Item> list = new ArrayList<Item>();
        int end = totalPages * PAGE_COUNT;
        for (int groupPosition = 0; groupPosition < end; groupPosition++) {
            ItemGroup group = new ItemGroup();
            group.setGroupName(String.valueOf(groupPosition));
            list.add(group);

            for (int memberPosition = 0; memberPosition < 3; memberPosition++) {
                ItemMember member = new ItemMember();
                member.setText(groupPosition + "-" + memberPosition);
                list.add(member);
            }
        }

        if (adapter == null) {
            adapter = new MyAdapter(this, list);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(list);
        }
    }

    void loadMore(int totalPages) {
        List<Item> list = new ArrayList<Item>();
        int start = (totalPages - 1) * PAGE_COUNT;
        int end = totalPages * PAGE_COUNT;
        for (int groupPosition = start; groupPosition < end; groupPosition++) {
            ItemGroup group = new ItemGroup();
            group.setGroupName(String.valueOf(groupPosition));
            list.add(group);

            for (int memberPosition = 0; memberPosition < 5; memberPosition++) {
                ItemMember member = new ItemMember();
                member.setText(groupPosition + "-" + memberPosition);
                list.add(member);
            }
        }
        if (adapter != null) {
            adapter.addAll(list);
        }
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
