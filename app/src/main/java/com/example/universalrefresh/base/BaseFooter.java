package com.example.universalrefresh.base;

import android.content.Context;
import android.view.View;

/**
 * @author dwj  2016/7/14 11:21
 */
public abstract class BaseFooter {
    private Context context;

    public BaseFooter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract View get();

    public abstract void onPulling(int maxValue, int absValue);

    public abstract void onRefreshing();

    public abstract void onComplete();

    public abstract  void onCancel();
}
