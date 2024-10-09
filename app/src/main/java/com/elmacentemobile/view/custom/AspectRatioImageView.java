package com.elmacentemobile.view.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

public class AspectRatioImageView extends AppCompatImageView {

    public AspectRatioImageView(Context context)
    {
        super(context);
        this.setScaleType(ScaleType.FIT_XY);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleType(ScaleType.FIT_XY);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Drawable d = getDrawable();

        if (d != null && d.getIntrinsicHeight() > 0)
        {
            int width = getLayoutParams().width;
            int height = getLayoutParams().height;


            if (height == ViewGroup.LayoutParams.WRAP_CONTENT || (width > 0 &&  width < 10000))
            {
                if (width <= 0);
                width = MeasureSpec.getSize(widthMeasureSpec);

                if (width > 0)
                    height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();


                if (height > getMaxHeight()) {
                    height = getMaxHeight();
                    width = height * d.getIntrinsicWidth() / d.getIntrinsicHeight();
                }

            }
            else if (width == ViewGroup.LayoutParams.WRAP_CONTENT  || (height > 0 &&  height < 10000))
            {

                if (height <= 0)
                    height = MeasureSpec.getSize(heightMeasureSpec);


                if (height > 0)
                    width = height * d.getIntrinsicWidth() / d.getIntrinsicHeight();


                if (width > getMaxWidth()) {
                    width = getMaxWidth();
                    height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
                }
            }


            if (width > 0 && height > 0)
                setMeasuredDimension(width, height);

            else
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}