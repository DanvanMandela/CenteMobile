package com.elmacentemobile.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.elmacentemobile.R;

public class CustomerCategory extends RelativeLayout {

    private Context context;


    public CustomerCategory(Context context) {
        super(context);

    }

    public CustomerCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.block_customer_type_layout, null);

    }

    public CustomerCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CustomerCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
}
