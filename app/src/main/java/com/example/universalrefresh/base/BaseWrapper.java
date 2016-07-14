package com.example.universalrefresh.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public abstract class BaseWrapper<Header extends BaseHeader, Content extends View, Footer extends BaseFooter> extends LinearLayout {

	public static final String TAG = BaseWrapper.class.getSimpleName();

	private Content mContentView;

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
        PULL_TO_EXECUTE,
        RELEASE_TO_EXECUTE,
		REFRESHING,
        ORIGIN
	}
	private State mState;

	private static final float RATIO_DEFAULT = 3f;
	private float ratio = RATIO_DEFAULT;

    public static final int REFRESH_TYPE_NONE = 0;
    public static final int OPERATION_PULL_DOWN = 1;
    public static final int OPERATION_PULL_UP = 2;

    private int mOperationType = REFRESH_TYPE_NONE;

	public interface OnOperatingListener {
		void onPulling(int operationType, int maxvalue, int absValue);
		void onExecuting(int operationType);
		void onComplete(int operationType);
	}

	private OnOperatingListener mOnRefreshListener;

	public void setOnOperatingListener(OnOperatingListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	public BaseWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

	private void init() {
		setOrientation(VERTICAL);
        setClickable(true);
		mTouchSlop = dpToPx(4);

		mContentView = onCreateRefreshContentView();
		if (mContentView == null) {
			throw new RuntimeException("No refresh content view");
		}

		mIsEnablePullDown = enablePullDown();
		mIsEnablePullUp = enablePullUp();

		getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (mIsEnablePullDown) {
                    mHeaderViewHeight = mHeaderView.getHeight();
                    if (mHeaderViewHeight != 0) {
                        hideHeader();
                    }
                }

                if (mIsEnablePullUp) {
                    mFooterViewHeight = mFooterView.getHeight();
                }

                if (mHeaderViewHeight != 0 || mFooterViewHeight != 0) {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                }

                return true;
            }
        });

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

		addView(mContentView);

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

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0, height = 0;

        if (mIsEnablePullDown && mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mContentView != null) {
            measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mIsEnablePullUp && mFooterView != null) {
            measureChild(mFooterView, widthMeasureSpec, heightMeasureSpec);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.max(width, mHeaderView.getMeasuredWidth());
            width = Math.max(width, mContentView.getMeasuredWidth());
            width = Math.max(width, mFooterView.getMeasuredWidth());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mContentView.getMeasuredHeight();
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mFooterView != null && mFooterViewHeight != 0) {
            mFooterView.layout(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + mFooterViewHeight);
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean hasTarget = super.dispatchTouchEvent(ev);
//        if (!hasTarget) {
//            return onInterceptTouchEvent(ev);
//        }
//        return true;
//    }

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
                                mOperationType = OPERATION_PULL_DOWN;
                            }
                        }
                    } else {
                        if (mIsEnablePullUp && !mIsNeedInterceptTouchEvent) {
                            if (isReadyToPullUp()) {
                                mIsNeedInterceptTouchEvent = true;
                                mOperationType = OPERATION_PULL_UP;
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
                            mState = mState.PULL_TO_EXECUTE;
                        }

                        if (mOperationType == OPERATION_PULL_DOWN) {
                            int moveY = (int) ev.getY();
                            int offset = (int) ((moveY - mLastMotionY) / ratio);
                            offset = offset < 0 ? 0 : offset;
                            pullDown(offset);

                            if (mState == State.PULL_TO_EXECUTE) {
                                if (offset >= mHeaderViewHeight) {
                                    setRefreshState(State.RELEASE_TO_EXECUTE);
                                }
                            }

                            if (mState == State.RELEASE_TO_EXECUTE) {
                                if (offset < mHeaderViewHeight) {
                                    setRefreshState(State.PULL_TO_EXECUTE);
                                }
                            }
                        } else if (mOperationType == OPERATION_PULL_UP) {
                            int moveY = (int) ev.getY();
                            int offset = (int) ((moveY - mLastMotionY) / ratio);
                            offset = offset > 0 ? 0 : offset;
                            pullUp(-offset);

                            if (mState == State.PULL_TO_EXECUTE) {
                                if (getScrollY() >= mFooterViewHeight) {
                                    setRefreshState(State.RELEASE_TO_EXECUTE);
                                }
                            }

                            if (mState == State.RELEASE_TO_EXECUTE) {
                                if (getScrollY() < mFooterViewHeight) {
                                    setRefreshState(State.PULL_TO_EXECUTE);
                                }
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mHasDownEvent) {
                            if (mState == State.RELEASE_TO_EXECUTE) {
                                startOperating();
                            } else {
                                cancelExecuting();
                            }
                        } else {
                            cancelExecuting();
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
        MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
        lp.topMargin = -mHeaderViewHeight + offset;
        mHeaderView.setLayoutParams(lp);

        mHeader.onPulling(mHeaderViewHeight, offset);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onPulling(mOperationType, mHeaderViewHeight, offset);
        }
    }

    protected void pullUp(int offset) {
        scrollTo(0, offset);

        mFooter.onPulling(mFooterViewHeight, offset);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onPulling(mOperationType, mFooterViewHeight, offset);
        }
    }

    public void startOperating() {
        if (mOperationType == OPERATION_PULL_DOWN) {
            pullDown(mHeaderViewHeight);
            mHeader.onRefreshing();
        } else if (mOperationType == OPERATION_PULL_UP) {
            pullUp(mFooterViewHeight);
            mFooter.onRefreshing();
        }

        setRefreshState(State.REFRESHING);

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onExecuting(mOperationType);
        }
	}

	public void completeExecuting() {

        if (mOperationType == OPERATION_PULL_DOWN) {
            hideHeader();
            mHeader.onComplete();
        } else if (mOperationType == OPERATION_PULL_UP) {
            hideFooter();
            mFooter.onComplete();
        }

		setRefreshState(State.ORIGIN);

		if (mOnRefreshListener != null) {
			mOnRefreshListener.onComplete(mOperationType);
		}
	}

    public void cancelExecuting() {
        if (mOperationType == OPERATION_PULL_DOWN) {
            hideHeader();
            mHeader.onCancel();
        } else if (mOperationType == OPERATION_PULL_UP) {
            hideFooter();
            mFooter.onCancel();
        }
        setRefreshState(State.ORIGIN);
    }

    private void hideHeader() {
        MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
        lp.topMargin = -mHeaderViewHeight;
        mHeaderView.setLayoutParams(lp);
    }

    private void hideFooter() {
        scrollBy(0, -getScrollY());
    }

	private void setRefreshState(State state) {
		mState = state;
	}

    protected abstract Content onCreateRefreshContentView();

    public Content getContentView() {
        return mContentView;
    }

    public abstract boolean enablePullDown();

    public void tooglePullDown(boolean toggle) {
        mIsEnablePullDown = toggle;
    }

	public Header onCreateRefreshHeader() {
		return null;
	}

    public abstract boolean enablePullUp();

    public void tooglePullUp(boolean toggle) {
        mIsEnablePullUp = toggle;
    }

    public Footer onCreateRefreshFooter() {
        return null;
    }

	protected abstract boolean isReadyToPullDown();

    protected abstract boolean isReadyToPullUp();

	protected int dpToPx(float dp) {
		return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
	}

}
