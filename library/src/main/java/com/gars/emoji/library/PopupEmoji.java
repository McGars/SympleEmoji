package com.gars.emoji.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.gars.emoji.library.listeners.EmojiTabListener;

import java.util.List;

/**
 * Created by Владимир on 14.10.2015.
 */
public class PopupEmoji extends PopupWindow implements View.OnClickListener, ViewPager.OnPageChangeListener {

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
    }

    private void initRootView() {
        ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
        if(decorView!=null){
            rootView = decorView.getChildAt(0);
            registerScreenResize();
        }
    }

    public void setSelectedColor(@ColorRes int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setAutoChangeColorSelectionTab(boolean autoChangeColorSelection){
        this.autoChangeColorSelection = autoChangeColorSelection;
    }

    public void show(boolean show){
        isShow = show;
        if(show){
            showKeyboard();
            show();
        } else if (isShowing())
            dismiss();
    }

    private void show(){
        if(!isShowing()){
            showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
            invalidatePopupViews(true);
        }
    }

    private void invalidatePopupViews(boolean show){
        for (View view : pages) {
            if(view instanceof EmojiTabListener)
                ((EmojiTabListener) view).onEmojiShow(show);
        }
    }

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

    private void initSimpleTabs() {
        tabsView.removeAllViews();
        int size = pxToDp(32, activity);
        int padding = pxToDp(4, activity);
        for (int i = 0; i < pages.size(); i++) {
            View page = pages.get(i);
            if(page instanceof EmojiTabListener){
                int icon = ((EmojiTabListener) page).getIcon();
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

    private boolean initCustomTabs() {
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

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelectionColor(position);
    }

    private void setSelectionColor(int position){
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

    public void registerScreenResize(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point screenSize = new Point();
                // Get the width of the screen
                if(Build.VERSION.SDK_INT >= 13){
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

                if (heightDifference > 70) {

                    setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    setHeight(heightDifference);

                    if(isShow && !isShowing()){
                        show();
                    }
                } else {
                    if(isShowing()){
                        invalidatePopupViews(false);
                        dismiss();
                    }
                }
            }
        });
    }

//    boolean checkIfKeyBoardIsOpen(){
//        InputMethodManager imm = (InputMethodManager) activity
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        if (imm.isAcceptingText()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static int pxToDp(int px, Context contex) {
        return (int)TypedValue.applyDimension(1, (float)px, contex.getResources().getDisplayMetrics());
    }

    public boolean showKeyboard() {
        if(editText != null && editText.getWindowToken() != null) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            return true;
        }
    }
}
