package com.example.universalrefresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.universalrefresh.R;
import com.example.universalrefresh.base.BaseRefresher;

/**
 * @author dwj  2016/7/4 18:17
 *
 */
public class ZCMHeader extends BaseRefresher {

    private TextView mTextTip;
    private ImageView mImageView;
    private AnimationDrawable mAnimationDrawable;

    public ZCMHeader(Context context) {
        super(context);
    }

    @Override
    public View get() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_listview_header, null);
        mTextTip = (TextView) header.findViewById(R.id.tv_header_tips);
        mTextTip.setText("招财猫理财");
        mImageView = (ImageView) header.findViewById(R.id.img_header_icon);
        if (mAnimationDrawable == null) {
            mAnimationDrawable = (AnimationDrawable) mImageView.getBackground();
        }
        return header;
    }

    @Override
    public void onPulling(int mRefreshHeaderHeight, int offset) {
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onComplete() {
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public void onCancel() {
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }
}
