package com.gars.simpleemoji;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.gars.emoji.library.BaseEmojiPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 15.10.2015.
 */
public class EmojiView extends BaseEmojiPage {
    private SmileAdapter adapter;

    public EmojiView(Context context) {
        super(context);
    }

    @Override
    public int getIcon() {
        return R.drawable.emoji_1f3a0;
    }

    @Override
    public void onEmojiShow(boolean show) {

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
        list.add(new SmileItem(R.drawable.emoji_1f3a0));
        list.add(new SmileItem(R.drawable.emoji_1f3a1));
        list.add(new SmileItem(R.drawable.emoji_1f3a2));
        list.add(new SmileItem(R.drawable.emoji_1f3a3));
        list.add(new SmileItem(R.drawable.emoji_1f3a4));
        list.add(new SmileItem(R.drawable.emoji_1f3a5));
        list.add(new SmileItem(R.drawable.emoji_1f3a6));
        list.add(new SmileItem(R.drawable.emoji_1f3a7));
        list.add(new SmileItem(R.drawable.emoji_1f3a8));
        list.add(new SmileItem(R.drawable.emoji_1f3a9));
        list.add(new SmileItem(R.drawable.emoji_1f3aa));
        list.add(new SmileItem(R.drawable.emoji_1f3ab));
        return list;
    }
}
