package com.example.bang.caughtu.customviewset;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by BANG on 2015-01-10.
 */
public class noTouchViewPager extends ViewPager {

    private boolean enabled;

    public noTouchViewPager(Context context) {
        super(context);
        this.enabled = true;
    }

    public noTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(this.enabled){
            return super.onTouchEvent(ev);
        }
        return false;
    }

    //터치 불가 하도록 만들기 위해
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.enabled){
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    //터치 조절
    public void setPagingEnabled(){
        this.enabled = true;
    }

    public void setPagingDisabled(){
        this.enabled = false;
    }
}
