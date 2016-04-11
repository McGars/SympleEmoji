# SympleEmoji
Show custom emoji above keyboard

The base classes for your own popup emoji

```java

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PopupEmoji emoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emoji = new PopupEmoji(this, (EditText) findViewById(R.id.editText));
        emoji.setPages(getPages());

        findViewById(R.id.button).setOnClickListener(this);
    }

    /**
    * Pages of emoji (unlimited)
    */
    public List<View> getPages() {
        List<View> pages = new ArrayList<>();
        pages.add(new EmojiView(this));
        pages.add(new EmojiView2(this));
        return pages;
    }

    @Override
    public void onClick(View v) {
        emoji.show(!emoji.isShowing());
    }

}

```
