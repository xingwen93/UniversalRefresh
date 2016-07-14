package com.example.universalrefresh.base;

import android.content.Context;
import android.view.View;
/**
 * @author dwj  2016/7/4 18:14
 */

public abstract class BaseRefresher {
    private Context context;

    public BaseRefresher(Context context) {
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
