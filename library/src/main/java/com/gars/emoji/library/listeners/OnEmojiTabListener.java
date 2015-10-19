package com.gars.emoji.library.listeners;

import android.support.annotation.DrawableRes;

/**
 * Created by Владимир on 14.10.2015.
 */
public interface OnEmojiTabListener {
    @DrawableRes
    public int getIcon();

    public void onEmojiShow(boolean show);
}
