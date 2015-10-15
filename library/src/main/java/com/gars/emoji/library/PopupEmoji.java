package com.gars.emoji.library;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.gars.emoji.library.adapter.FragmentPageAdapter;
import com.gars.emoji.library.listeners.EmojiTabListener;

import java.util.List;

/**
 * Created by Владимир on 14.10.2015.
 */
public class PopupEmoji extends PopupWindow implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private FragmentActivity activity;
    private EditText editText;
    private ViewGroup tabsView;
    private ImageView ivback;
    private ViewPager viewPager;
    private List<View> tabs;
    private List<View> pages;
    private int selectedColor;
    private boolean autoChangeColorSelection;
    private View rootView;

    public PopupEmoji(FragmentActivity activity, EditText editText){
        this.activity = activity;
        this.editText = editText;
//        selectedColor = activity.getResources().getColor(getAttributeResourceId(activity, R.attr.colorAccent));
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
        if(show)
            showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        else
            dismiss();
    }

    public void setPages(List<View> tabs, List<View> pages){
        this.tabs = tabs;
        this.pages = pages;

        if(!initCustomTabs())
            initSimpleTabs();

        viewPager.removeAllViews();
        for (View view : pages) {
            viewPager.addView(view);
        }

//        FragmentPageAdapter adapter = new FragmentPageAdapter(activity.getSupportFragmentManager(), pages);
//        viewPager.setAdapter(adapter);
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
        for (View page : pages) {
            if(page instanceof EmojiTabListener){
                int icon = ((EmojiTabListener) page).getIcon();
                ImageView v = new ImageView(activity);
                v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                v.setImageResource(icon);
                v.setOnClickListener(tabLick);
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
                //Point screenRealSize = h.getRealSize(context);

                // Get the width of the screen
                if(Build.VERSION.SDK_INT >= 13){
                    display.getSize(screenSize);
                } else {
                    screenSize.set(display.getWidth(), display.getHeight());
                }



                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
//
//                int screenHeight = rootView.getRootView()
//                        .getHeight();
                int heightDifference = screenSize.y
                        - (r.bottom - r.top);
                int resourceId = activity.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");

                if (resourceId > 0) {
                    heightDifference -= activity.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
//                    keyBoardHeight = heightDifference;
                    setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    setHeight(heightDifference);
//                    if(isOpened == false){
//                        if(onSoftKeyboardOpenCloseListener!=null)
//                            onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
//                    }
//                    isOpened = true;
//                    if(pendingOpen){
//                        showAtBottom();
//                        pendingOpen = false;
//                    }
                }
                else{
                    dismiss();
//                    isOpened = false;
//                    if(onSoftKeyboardOpenCloseListener!=null)
//                        onSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }
}
