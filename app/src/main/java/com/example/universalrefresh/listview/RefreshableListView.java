package com.example.universalrefresh.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;

import com.example.universalrefresh.base.RefreshableWrapper;
import com.example.universalrefresh.base.RefreshableWrapper2;
import com.example.universalrefresh.footer.ZCMFooter;
import com.example.universalrefresh.header.ZCMHeader;

public class RefreshableListView extends RefreshableWrapper<ZCMHeader, StickyListView, ZCMFooter> {

	private StickyListView refreshListView;

	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    public ZCMHeader onCreateRefreshHeader() {
        return new ZCMHeader(getContext());
    }

	@Override
	protected StickyListView onCreateRefreshContentView() {
		refreshListView = new StickyListView(getContext());
		refreshListView.setOverScrollMode(AbsListView.OVER_SCROLL_NEVER);
        refreshListView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		return refreshListView;
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
        if (refreshListView.getChildCount() != 0) {
            return refreshListView.getFirstVisiblePosition() == 0
                    && refreshListView.getChildAt(0).getTop() == 0;
		}
        return true;
	}

    @Override
    public boolean enablePullUp() {
        return true;
    }

    @Override
    protected boolean isReadyToPullUp() {
        if (refreshListView.getChildCount() != 0) {
            int adapterChildCount = refreshListView.getAdapter().getCount();
            return refreshListView.getLastVisiblePosition() == adapterChildCount - 1
                    && refreshListView.getChildAt(refreshListView.getChildCount() - 1).getBottom() >= refreshListView.getHeight();
        }
        return true;
    }

}
