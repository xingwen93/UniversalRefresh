package com.example.universalrefresh.footer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universalrefresh.R;
import com.example.universalrefresh.base.BaseRefresher;

/**
 * @author dwj  2016/7/4 18:17
 */
public class ZCMFooter extends BaseRefresher {

    TextView footer;

    public ZCMFooter(Context context) {
        super(context);
    }

    @Override
    public View get() {
//        View footer = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_listview_header, null);
//        mTextTip = (TextView) footer.findViewById(R.id.tv_header_tips);
//        mTextTip.setText("招财猫理财");
//        mImageView = (ImageView) footer.findViewById(R.id.img_header_icon);
//        if (mAnimationDrawable == null) {
//            mAnimationDrawable = (AnimationDrawable) mImageView.getBackground();
//        }
        footer = new TextView(getContext());
        int padding = (int) (8 * getContext().getResources().getDisplayMetrics().density);
        footer.setPadding(padding, padding, padding, padding);
        footer.setText("上拉加载更多");
        footer.setTextSize(20f);
        footer.setGravity(Gravity.CENTER);
        return footer;
    }

    @Override
    public void onPulling(int max, int offset) {
        if (offset >= max) {
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
