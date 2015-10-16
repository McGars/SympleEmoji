package com.gars.emoji.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.gars.emoji.library.listeners.EmojiTabListener;

import java.util.zip.Inflater;

/**
 * Created by Владимир on 16.10.2015.
 */
public abstract class BaseEmojiPage extends FrameLayout implements EmojiTabListener {
    protected GridView gridView;

    public BaseEmojiPage(Context context) {
        this(context, null);
    }

    public BaseEmojiPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseEmojiPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseEmojiPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @LayoutRes
    protected int getLayoutId(){
        return R.layout.emoji_grid_view;
    }

    protected void init(){
        LayoutInflater infrater = LayoutInflater.from(getContext());
        gridView = (GridView) infrater.inflate(getLayoutId(), null);
        addView(gridView);
    }

}
