package com.example.universalrefresh.footer;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.universalrefresh.base.BaseFooter;

/**
 * @author dwj  2016/7/4 18:17
 */
public class ZCMFooter extends BaseFooter {

    TextView footer;

    public ZCMFooter(Context context) {
        super(context);
    }

    @Override
    public View get() {
        footer = new TextView(getContext());
        int padding = (int) (8 * getContext().getResources().getDisplayMetrics().density);
        footer.setPadding(padding, padding, padding, padding);
        footer.setText("上拉加载更多");
        footer.setTextSize(20f);
        footer.setGravity(Gravity.CENTER);
        return footer;
    }

    @Override
    public void onPulling(int maxValue, int absValue) {
        if (absValue >= maxValue) {
            footer.setText("松开加载更多");
        } else {
            footer.setText("上拉加载更多");
        }
    }

    @Override
    public void onRefreshing() {
        footer.setText("正在加载...");
    }

    @Override
    public void onComplete() {
        footer.setText("上拉加载更多");
    }

    @Override
    public void onCancel() {
        footer.setText("上拉加载更多");
    }
}
