package com.triskelapps.views;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

public class TextViewHTML extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewHTML(Context context) {
        super(context);
    }

    public TextViewHTML(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewHTML(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setText(Html.fromHtml(getText().toString()));
        setMovementMethod(LinkMovementMethod.getInstance());
    }



}
