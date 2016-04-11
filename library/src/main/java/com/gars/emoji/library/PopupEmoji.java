package com.gars.emoji.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.gars.emoji.library.listeners.OnBackspaceListener;
import com.gars.emoji.library.listeners.OnEmojiKeyboardListener;
import com.gars.emoji.library.listeners.OnEmojiTabListener;

import java.util.List;

/**
 * Created by Владимир on 14.10.2015.
 */
public class PopupEmoji extends PopupWindow implements View.OnClickListener, ViewPager.OnPageChangeListener, View.OnTouchListener {

    private final EmojiPagerAdapter adapter;
    private FragmentActivity activity;
    private EditText editText;
    private ViewGroup tabsView;
    private ImageView ivback;
    private ViewPager viewPager;
    private List<View> tabs;
    private List<View> pages;
    private int selectedColor;
    private boolean autoChangeColorSelection = true;
    private View rootView;
    private boolean isShow;
    private boolean keyboardOpen;
    private OnEmojiKeyboardListener onEmojiKeyboardListener;
    private OnBackspaceListener onBackspaceListener;

    public PopupEmoji(FragmentActivity activity, EditText editText){
        this.activity = activity;
        this.editText = editText;
        int color = getAttributeResourceId(activity, R.attr.emojiColorPressed);
        if(color == 0){
            selectedColor = Color.DKGRAY;
        } else {
            selectedColor = activity.getResources().getColor(color);
        }
        adapter = new EmojiPagerAdapter();
        initRootView();
        initView();
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        editText.setOnTouchListener(this);
    }

    private void initRootView() {
        ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
        if(decorView!=null){
            rootView = decorView.getChildAt(0);
            registerScreenResize();
        }
    }

    /**
     * Change background color tab view when page selected
     * @param selectedColor
     */
    public void setSelectedColor(@ColorRes int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Set access change background color tab view
     * @param autoChangeColorSelection
     */
    public void setAutoChangeColorSelectionTab(boolean autoChangeColorSelection){
        this.autoChangeColorSelection = autoChangeColorSelection;
    }

    /**
     * Listener for open or close keyboard
     * @param onEmojiKeyboardListener
     */
    public void setOnEmojiKeyboardListener(OnEmojiKeyboardListener onEmojiKeyboardListener){
        this.onEmojiKeyboardListener = onEmojiKeyboardListener;
    }

    /**
     * Listener for remove text in EditText click on back space
     * @param onBackspaceListener
     */
    public void setOnBackspaceListener(OnBackspaceListener onBackspaceListener){
        this.onBackspaceListener = onBackspaceListener;
    }

    /**
     * Open emoji
     * @param show
     */
    public void show(boolean show){
        isShow = show;
        if(show){
            if(!keyboardOpen)
                showKeyboard();
            else
                show();
        } else if (isShowing()){
            invalidatePopupViews(false);
            dismiss();
        }
    }

    CountDownTimer timerShow = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish() {
            showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
            invalidatePopupViews(true);
            if (Build.VERSION.SDK_INT > 14) {
                getContentView().setAlpha(0f);
                getContentView().animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start();
            }
        }
    };

    private void show(){
        if(!isShowing()){
            timerShow.cancel();
            timerShow.start();
        }
    }

    /**
     * Pages must implements {@link OnEmojiTabListener}
     * Calls when emoji open or hidden
     * @param show
     */
    private void invalidatePopupViews(boolean show){
        for (View view : pages) {
            if(view instanceof OnEmojiTabListener)
                ((OnEmojiTabListener) view).onEmojiShow(show);
        }
    }

    /**
     * @param tabs override default tabs for pages
     * @param pages
     */
    public void setPages(List<View> tabs, List<View> pages){
        this.tabs = tabs;
        this.pages = pages;

        if(!initCustomTabs())
            initSimpleTabs();

        viewPager.setAdapter(null);
        adapter.clear();
        for (View view : pages) {
            adapter.addView(view);
        }
        viewPager.setAdapter(adapter);
    }

    /**
     * Set view pages in viewPager
     * Pages must implements {@link OnEmojiTabListener} for creating tabs
     * @param pages
     */
    public void setPages(List<View> pages){
        setPages(null, pages);
    }

    protected void initView(){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.se_view_popop, null);
        tabsView = (ViewGroup)v.findViewById(R.id.tabsView);
        ivback = (ImageView)v.findViewById(R.id.ivback);
        viewPager = (ViewPager)v.findViewById(R.id.viewPagerEmoji);

        ivback.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);

        setContentView(v);
    }

    protected void initSimpleTabs() {
        tabsView.removeAllViews();
        int size = pxToDp(32, activity);
        int padding = pxToDp(4, activity);
        for (int i = 0; i < pages.size(); i++) {
            View page = pages.get(i);
            if(page instanceof OnEmojiTabListener){
                int icon = ((OnEmojiTabListener) page).getIcon();
                ImageView v = new ImageView(activity);
                v.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.leftMargin = padding;
                params.rightMargin = padding;
                v.setLayoutParams(params);
                v.setImageResource(icon);
                v.setOnClickListener(tabLick);
                if(i == 0 && autoChangeColorSelection)
                    v.setBackgroundColor(selectedColor);
                tabsView.addView(v);
            }
        }
    }

    protected boolean initCustomTabs() {
        if(tabs!=null){
            tabsView.removeAllViews();
            for (View tab : tabs) {
                tabsView.addView(tab);
                if(!tab.isClickable())
                    tab.setOnClickListener(tabLick);
            }
            return true;
        }
        return false;
    }

    View.OnClickListener tabLick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = tabsView.indexOfChild(v);
            viewPager.setCurrentItem(position, true);
            setSelectionColor(position);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivback){
            if(onBackspaceListener!=null && onBackspaceListener.onBackSpace())
                return;
            editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelectionColor(position);
    }

    /**
     * if {@link #autoChangeColorSelection} = true, setted background color to selectedColor of tab view
     * Set background color
     * @param position
     */
    protected void setSelectionColor(int position){
        if(!autoChangeColorSelection)
            return;
        for (int i = 0; i < tabsView.getChildCount(); i++) {
            View child = tabsView.getChildAt(i);
            if(child.getBackground()!=null){
                child.setBackgroundColor(0);
            }
        }

        View tab = tabsView.getChildAt(position);
        if(tab!=null){
            tab.setBackgroundColor(selectedColor);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void registerScreenResize(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    /**
     * Remove layout listener from root view
     */
    public void onDestroy(){
        if(Build.VERSION.SDK_INT>=16)
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        else
            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
        dismiss();
    }

    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            // Get the width of the screen
            if (Build.VERSION.SDK_INT >= 13) {
                display.getSize(screenSize);
            } else {
                screenSize.set(display.getWidth(), display.getHeight());
            }

            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
//
            int heightDifference = screenSize.y
                    - (r.bottom - r.top);
            int resourceId = activity.getResources()
                    .getIdentifier("status_bar_height",
                            "dimen", "android");

            if (resourceId > 0) {
                heightDifference -= activity.getResources()
                        .getDimensionPixelSize(resourceId);
            }

            if (heightDifference > 30) {
                keyboardTrigger(!keyboardOpen);
                keyboardOpen = true;
                setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                setHeight(heightDifference);

                if (isShow) {

                    show();
                }
            } else {
                keyboardTrigger(keyboardOpen);
                keyboardOpen = false;
                if (isShowing()) {
                    invalidatePopupViews(false);
                    dismiss();
                }
            }
        }
    };

    /**
     * Trigger call when keyboard opened or closed
     * @param check
     */
    protected void keyboardTrigger(boolean check){
        if(check)
            if(onEmojiKeyboardListener!=null)
                onEmojiKeyboardListener.onOpenKeyboard(keyboardOpen);
    }

    /**
     * Convert px to dp
     * @param px
     * @param contex
     * @return
     */
    public static int pxToDp(int px, Context contex) {
        return (int)TypedValue.applyDimension(1, (float)px, contex.getResources().getDisplayMetrics());
    }

    /**
     * Open keyboard in she not open
     * @return
     */
    public boolean showKeyboard() {
        if(editText != null && editText.getWindowToken() != null) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            return true;
        }
    }

    /**
     * Get resource id for pressed color in tab bar
     * @param context
     * @param attr
     * @return
     */
    public static int getAttributeResourceId(Context context, int attr){
        try {
            TypedValue typedValue = new TypedValue();
            int[] resIdAttr = new int[] { attr };
            TypedArray a = context.obtainStyledAttributes(typedValue.data, resIdAttr);
            int resId = a.getResourceId(0, 0);
            a.recycle();
            return resId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                isShow = false;
        }

        return false;
    }
}
