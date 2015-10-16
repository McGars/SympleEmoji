package com.gars.simpleemoji;

import android.content.Context;

import com.gars.emoji.library.BaseEmojiPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 15.10.2015.
 */
public class EmojiView2 extends BaseEmojiPage {
    private SmileAdapter adapter;

    public EmojiView2(Context context) {
        super(context);
    }

    @Override
    public int getIcon() {
        return R.drawable.emoji_2764;
    }

    @Override
    protected void init() {
        super.init();
        List<SmileItem> list = getSmileItem();
        adapter = new SmileAdapter(getContext(), list);
        gridView.setAdapter(adapter);
    }

    public List<SmileItem> getSmileItem() {
        List<SmileItem> list = new ArrayList<>();
        list.add(new SmileItem(R.drawable.emoji_2714));
        list.add(new SmileItem(R.drawable.emoji_2716));
        list.add(new SmileItem(R.drawable.emoji_2728));
        list.add(new SmileItem(R.drawable.emoji_2733));
        list.add(new SmileItem(R.drawable.emoji_2734));
        list.add(new SmileItem(R.drawable.emoji_2744));
        list.add(new SmileItem(R.drawable.emoji_2747));
        list.add(new SmileItem(R.drawable.emoji_2753));
        list.add(new SmileItem(R.drawable.emoji_2754));
        list.add(new SmileItem(R.drawable.emoji_2755));
        list.add(new SmileItem(R.drawable.emoji_2757));
        list.add(new SmileItem(R.drawable.emoji_2764));
        list.add(new SmileItem(R.drawable.emoji_2795));
        return list;
    }
}
