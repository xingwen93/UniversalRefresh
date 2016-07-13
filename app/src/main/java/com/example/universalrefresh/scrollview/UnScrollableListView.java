package com.example.universalrefresh.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author dwj  2016/7/4 17:10
 */
public class UnScrollableListView extends ListView {

    public UnScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
