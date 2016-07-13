package com.example.universalrefresh.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public abstract class RefreshableWrapper2<Header extends BaseRefresher, Content extends View, Footer extends BaseRefresher> extends ViewGroup {

    public static final String TAG = RefreshableWrapper.class.getSimpleName();

    private Content mRefreshableView;

    private Header mHeader;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private boolean mIsEnablePullDown = true;

    private Footer mFooter;
    private View mFooterView;
    private int mFooterViewHeight;
    private boolean mIsEnablePullUp = true;

    private float mLastMotionY;
    private int mTouchSlop;
    private boolean mHasDownEvent = false;

    private boolean mIsNeedInterceptTouchEvent = false;

    public enum State {
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
        ORIGIN
    }
    private State mState;

    private static final float RATIO_DEFAULT = 3f;
    private float ratio = RATIO_DEFAULT;

    public static final int REFRESH_TYPE_NONE = 0;
    public static final int REFRESH_TYPE_PULL_DOWN = 1;
    public static final int REFRESH_TYPE_PULL_UP = 2;

    private int mRefreshType = REFRESH_TYPE_NONE;

    public interface OnRefreshListener {
        void onPull(int refreshType, int maxvalue, int value);
        void onRefreshing(int refreshType);
        void onRefreshComplete(int refreshType);
    }
    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public RefreshableWrapper2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTouchSlop = dpToPx(4);

        mRefreshableView = onCreateRefreshContentView();
        if (mRefreshableView == null) {
            throw new RuntimeException("No refresh content view");
        }

        mIsEnablePullDown = enablePullDown();
        mIsEnablePullUp = enablePullUp();

        if (mIsEnablePullDown) {
            mHeader = onCreateRefreshHeader();
            if (mHeader != null) {
                mHeaderView = mHeader.get();
                if (mHeaderView != null) {
                    addView(mHeaderView);
                } else {
                    mIsEnablePullDown = false;
                }
            } else {
                mIsEnablePullDown = false;
            }
        }

        addView(mRefreshableView);

        if (mIsEnablePullUp) {
            mFooter = onCreateRefreshFooter();
            if (mFooter != null) {
                mFooterView = mFooter.get();
                if (mFooterView != null) {
                    addView(mFooterView);
                } else {
                    mIsEnablePullUp = false;
                }
            } else {
                mIsEnablePullUp = false;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;

        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mRefreshableView != null) {
            measureChild(mRefreshableView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mFooterView != null) {
            measureChild(mFooterView, widthMeasureSpec, heightMeasureSpec);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (mHeaderView != null) {
                width = Math.max(width, mHeaderView.getMeasuredWidth());
            }
            if (mRefreshableView != null) {
                width = Math.max(width, mRefreshableView.getMeasuredWidth());
            }
            if (mFooterView != null) {
                width = Math.max(width, mRefreshableView.getMeasuredWidth());
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (mHeaderView != null) {
                height += mHeaderView.getMeasuredHeight();
            }
            if (mRefreshableView != null) {
                height += mRefreshableView.getMeasuredHeight();
            }
            if (mFooterView != null) {
                height += mFooterView.getMeasuredHeight();
            }
        }

        setMeasuredDimension(width, height);
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        if (mHeaderView != null) {
//            scrollBy(0, mHeaderView.getHeight());
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int top = 0;
            if (mHeaderView != null) {
                int childWidth = mHeaderView.getMeasuredWidth();
                int childHeight = mHeaderView.getMeasuredHeight();
                int cl = 0;
                int ct = top;
                int cr = childWidth;
                int cb = ct + childHeight;
                mHeaderView.layout(cl, ct, cr, cb);
                top = cb;
            }

            if (mRefreshableView != null) {
                int childWidth = mRefreshableView.getMeasuredWidth();
                int childHeight = mRefreshableView.getMeasuredHeight();
                int cl = 0;
                int ct = top;
                int cr = getMeasuredWidth();
                int cb = ct + getMeasuredWidth();
                mRefreshableView.layout(cl, ct, cr, cb);
                top = cb;
            }

            if (mFooterView != null) {
                int childWidth = mFooterView.getMeasuredWidth();
                int childHeight = mFooterView.getMeasuredHeight();
                int cl = 0;
                int ct = top;
                int cr = childWidth;
                int cb = childHeight;
                mFooterView.layout(cl, ct, cr, cb);
            }
        }
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsEnablePullDown && !mIsEnablePullUp) {
            return false;
        }

        if (mState == State.REFRESHING) {
            mHasDownEvent = false;
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHasDownEvent = true;
                mIsNeedInterceptTouchEvent = false;
                setRefreshState(State.ORIGIN);
                mLastMotionY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int offsetY = (int) (moveY - mLastMotionY);
                if (Math.abs(offsetY) > mTouchSlop) {
                    if (offsetY > 0) {
                        if (mIsEnablePullDown && !mIsNeedInterceptTouchEvent) {
                            if (isReadyToPullDown()) {
                                mIsNeedInterceptTouchEvent = true;
                                mRefreshType = REFRESH_TYPE_PULL_DOWN;
                            }
                        }
                    } else {
                        if (mIsEnablePullUp && !mIsNeedInterceptTouchEvent) {
                            if (isReadyToPullUp()) {
                                mIsNeedInterceptTouchEvent = true;
                                mRefreshType = REFRESH_TYPE_PULL_UP;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mHasDownEvent = false;
                break;
            default:
                break;
        }

        return mIsNeedInterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mState != State.REFRESHING) {
            if (mIsNeedInterceptTouchEvent) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_MOVE: {

                        if (mState == State.ORIGIN) {
                            mState = mState.PULL_TO_REFRESH;
                        }

                        if (mRefreshType == REFRESH_TYPE_PULL_DOWN) {
                            int moveY = (int) ev.getY();
                            int offset = (int) ((moveY - mLastMotionY) / ratio);
                            offset = offset < 0 ? 0 : offset;
                            pullDown(offset);

                            if (mState == State.PULL_TO_REFRESH) {
                                if (offset >= mHeaderViewHeight + mTouchSlop) {
                                    setRefreshState(State.RELEASE_TO_REFRESH);
                                }
                            }

                            if (mState == State.RELEASE_TO_REFRESH) {
                                if (offset < mHeaderViewHeight + mTouchSlop) {
                                    setRefreshState(State.PULL_TO_REFRESH);
                                }
                            }
                        } else if (mRefreshType == REFRESH_TYPE_PULL_UP) {
                            int moveY = (int) ev.getY();
                            int offset = (int) ((moveY - mLastMotionY) / ratio);
                            offset = offset > 0 ? 0 : offset;
                            pullUp(-offset);
                            if (mState == State.PULL_TO_REFRESH) {
                                if (getScrollY() >= mFooterViewHeight + mTouchSlop) {
                                    setRefreshState(State.RELEASE_TO_REFRESH);
                                }
                            }

                            if (mState == State.RELEASE_TO_REFRESH) {
                                if (getScrollY() < mFooterViewHeight + mTouchSlop) {
                                    setRefreshState(State.PULL_TO_REFRESH);
                                }
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mHasDownEvent) {
                            if (mState == State.RELEASE_TO_REFRESH) {
                                postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        completeRefresh();
                                    }
                                }, 1500);
                                startRefresh();
                            } else {
                                cancelRefresh();
                            }
                        } else {
                            cancelRefresh();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return true;
    }
    private void pullDown(int offset) {

        int left = mHeaderView.getPaddingLeft();
        int right = mHeaderView.getPaddingRight();
        int bottom = mHeaderView.getPaddingBottom();
        mHeaderView.setPadding(left, -mHeaderViewHeight + offset, right, bottom);

//        MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
//        lp.topMargin = -mHeaderViewHeight + offset;
//        mHeaderView.setLayoutParams(lp);

        mHeader.onPulling(mHeaderViewHeight, offset);

        if (mOnRefreshListener != null) {
            offset = offset > mHeaderViewHeight ? mHeaderViewHeight : offset;
            mOnRefreshListener.onPull(REFRESH_TYPE_PULL_DOWN, mHeaderViewHeight, offset);
        }
    }

    protected void pullUp(int offset) {
        scrollTo(0, offset);

        mFooter.onPulling(mFooterViewHeight, offset);

        if (mOnRefreshListener != null) {
            offset = offset > mFooterViewHeight ? mFooterViewHeight : offset;
            mOnRefreshListener.onPull(REFRESH_TYPE_PULL_UP, mFooterViewHeight, offset);
        }
    }

    public void startRefresh() {
        startRefresh(0);
    }

    public void startRefresh(long timeDelay) {
        if (mRefreshType == REFRESH_TYPE_PULL_DOWN) {
            pullDown(mHeaderViewHeight);
            mHeader.onRefreshing();
        } else if (mRefreshType == REFRESH_TYPE_PULL_UP) {
            pullUp(mFooterViewHeight);
            mFooter.onRefreshing();
        }

        setRefreshState(State.REFRESHING);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefreshing(mRefreshType);
        }
    }

    public void completeRefresh() {

        if (mRefreshType == REFRESH_TYPE_PULL_DOWN) {
            hideHeader();
            mHeader.onComplete();
        } else if (mRefreshType == REFRESH_TYPE_PULL_UP) {
            hideFooter();
            mFooter.onComplete();
        }

        setRefreshState(State.ORIGIN);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefreshComplete(REFRESH_TYPE_PULL_DOWN);
        }
    }

    public void cancelRefresh() {
        if (mRefreshType == REFRESH_TYPE_PULL_DOWN) {
            hideHeader();
            mHeader.onCancel();
        } else if (mRefreshType == REFRESH_TYPE_PULL_UP) {
            hideFooter();
            mFooter.onCancel();
        }
        setRefreshState(State.ORIGIN);
    }

    private void hideHeader() {
        int left = mHeaderView.getPaddingLeft();
        int right = mHeaderView.getPaddingRight();
        int bottom = mHeaderView.getPaddingBottom();
        mHeaderView.setPadding(left, -mHeaderViewHeight, right, bottom);
    }

    private void hideFooter() {
        scrollBy(0, -getScrollY());
    }

    private void setRefreshState(State state) {
        mState = state;
    }

    protected abstract Content onCreateRefreshContentView();

    public Content getRefreshContentView() {
        return mRefreshableView;
    }

    public Header onCreateRefreshHeader() {
        return null;
    }

    public abstract boolean enablePullDown();

    public Footer onCreateRefreshFooter() {
        return null;
    }

    public abstract boolean enablePullUp();

    protected abstract boolean isReadyToPullDown();

    protected abstract boolean isReadyToPullUp();

    protected int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}

