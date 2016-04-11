package com.gars.simpleemoji;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.gars.emoji.library.PopupEmoji;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PopupEmoji emoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emoji = new PopupEmoji(this, (EditText) findViewById(R.id.editText));
        findViewById(R.id.button).setOnClickListener(this);

        emoji.setPages(getPages());
    }

    @Override
    public void onClick(View v) {
        emoji.show(!emoji.isShowing());
    }

    public List<View> getPages() {
        List<View> pages = new ArrayList<>();
        pages.add(new EmojiView(this));
        pages.add(new EmojiView2(this));
        return pages;
    }

}
