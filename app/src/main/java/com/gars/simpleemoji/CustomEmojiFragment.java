package com.gars.simpleemoji;

import com.gars.emoji.library.FragmentPage;

/**
 * Created by Владимир on 15.10.2015.
 */
public class CustomEmojiFragment extends FragmentPage {
    @Override
    public int getIcon() {
        return R.mipmap.ic_launcher;
    }

    public static CustomEmojiFragment newInstance() {
        return new CustomEmojiFragment();
    }
}
