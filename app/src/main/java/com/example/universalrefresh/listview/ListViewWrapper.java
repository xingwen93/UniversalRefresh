package com.example.universalrefresh.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.example.universalrefresh.base.BaseWrapper;
import com.example.universalrefresh.footer.ZCMFooter;
import com.example.universalrefresh.header.ZCMHeader;

public class ListViewWrapper extends BaseWrapper<ZCMHeader, StickyListView, ZCMFooter> {

	private StickyListView refreshListView;

	public ListViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (refreshListView.getChildCount() == refreshListView.getHeaderViewsCount() + refreshListView.getFooterViewsCount()) {
            View emptyView = refreshListView.getEmptyView();
            if (emptyView != null) {
                measureChild(emptyView, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(emptyView.getMeasuredWidth(), emptyView.getMeasuredHeight());
            }
        }
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
