package com.gars.simpleemoji;

import android.content.Context;
import android.view.View;

import com.gars.emoji.library.listeners.EmojiTabListener;

/**
 * Created by Владимир on 15.10.2015.
 */
public class EmojiView extends View implements EmojiTabListener {
    public EmojiView(Context context) {
        super(context);
    }

    @Override
    public int getIcon() {
        return R.mipmap.ic_launcher;
    }
}
