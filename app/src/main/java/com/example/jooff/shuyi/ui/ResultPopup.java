package com.example.jooff.shuyi.ui;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.jooff.shuyi.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Jooff on 2016/8/2.
 */

public class ResultPopup extends BasePopupWindow implements View.OnClickListener {

    private TextView korea;
    public ResultPopup(Activity context) {
        super(context);
        korea = (TextView) findViewById(R.id.korea);
        setViewClickListener(this,korea);
    }

    @Override
    protected Animation getShowAnimation() {
        return null;
    }

    @Override
    protected View getClickToDismissView() {
        return null;
    }

    @Override
    public View getPopupView() {
        return null;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    @Override
    public void onClick(View view) {

    }
}
