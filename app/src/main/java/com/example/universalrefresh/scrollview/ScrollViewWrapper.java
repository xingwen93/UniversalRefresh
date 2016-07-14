package com.example.universalrefresh.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.example.universalrefresh.base.BaseWrapper;
import com.example.universalrefresh.footer.ZCMFooter;
import com.example.universalrefresh.header.ZCMHeader;

/**
 * @author dwj  2016/7/4 16:28
 */
public class ScrollViewWrapper extends BaseWrapper<ZCMHeader, ScrollView, ZCMFooter> {

    public ScrollViewWrapper(Context context) {
        this(context, null);
    }

    public ScrollViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ZCMHeader onCreateRefreshHeader() {
        return new ZCMHeader(getContext());
    }

    @Override
    protected ScrollView onCreateRefreshContentView() {
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        scrollView.setFillViewport(true);
        scrollView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        return scrollView;
    }

    @Override
    public ZCMFooter onCreateRefreshFooter() {
        return new ZCMFooter(getContext());
    }

    @Override
    public boolean enablePullDown() {
        return true;
    }

    @Override
    protected boolean isReadyToPullDown() {
       return getContentView().getScrollY() == 0;
    }

    @Override
    public boolean enablePullUp() {
        return true;
    }

    @Override
    protected boolean isReadyToPullUp() {
        ScrollView contentView = getContentView();
        int childHeight = contentView.getChildAt(0).getHeight();
        return childHeight == contentView.getScrollY() + contentView.getHeight();
    }

}
