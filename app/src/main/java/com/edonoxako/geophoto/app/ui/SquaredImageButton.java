package com.edonoxako.geophoto.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;


public class SquaredImageButton extends ImageButton {

    public SquaredImageButton(Context context) {
        super(context);
    }

    public SquaredImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + 6, getMeasuredWidth() + 6);
    }
}
