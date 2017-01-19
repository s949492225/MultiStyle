package com.syiyi.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * View属性设置的帮助类
 * Created by songlintao on 16/10/13.
 */

public class ViewHelper {
    private View mContentView;
    private Context mContext;
    private ExecutorService workServices = Executors.newFixedThreadPool(2);
    private Handler mUIHandler;
    private SparseArray<View> mSubViews = new SparseArray<>();

    public ViewHelper(View mContent, Context mContext) {
        this.mContentView = mContent;
        this.mContext = mContext;
        mUIHandler = new Handler(this.mContext.getMainLooper());
    }

    public Context getContext() {
        return mContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mSubViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mSubViews.put(viewId, view);
        }
        return (T) view;
    }

    public void clearClick(int... viewIds) {
        for (int i : viewIds) {
            setClick(i, null);
        }
    }

    public void setContentClick(View.OnClickListener clickListener) {
        mContentView.setOnClickListener(clickListener);
    }

    public void clearContentClick() {
        setContentClick(null);
    }

    public void clearColor(int... viewIds) {
        for (int i : viewIds) {
            getView(i).setSelected(false);
        }
    }

    @SuppressWarnings("deprecation")
    public String setEditText(int viewId, String text) {
        EditText editText = getView(viewId);
        editText.setText(text == null ? "" : Html.fromHtml(text));
        return editText.getText().toString();
    }

    public String getEditText(int viewId) {
        EditText editText = getView(viewId);
        return editText.getText().toString();
    }

    public void clearEditFocus(int viewId) {
        EditText editText = getView(viewId);
        editText.clearFocus();
    }

    public void setClick(int viewId, View.OnClickListener clickListener) {
        getView(viewId).setOnClickListener(clickListener);
    }

    public void setClick(View.OnClickListener clickListener, int... viewIds) {
        for (int id : viewIds) {
            getView(id).setOnClickListener(clickListener);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    @SuppressWarnings("deprecation")
    public void setText(int viewId, String text) {
        View view = getView(viewId);
        TextView textView = (TextView) view;
        textView.setText(Html.fromHtml(text == null ? "" : text));
        textView.setVisibility(View.VISIBLE);
    }

    public void setSelect(boolean select, int viewId) {
        getView(viewId).setSelected(select);
    }

    public void clearText(int viewId) {
        View view = getView(viewId);
        TextView textView = (TextView) view;
        textView.setText(null);
    }

    public void setEnable(boolean enable, int... viewIds) {
        for (int i : viewIds) {
            getView(i).setEnabled(enable);
        }
    }

    public boolean enable(int viewId) {
        return getView(viewId).isEnabled();
    }

    public void setVisible(boolean isShow, int... viewIds) {
        for (int i : viewIds) {
            getView(i).setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public void setInVisible(boolean isShow, int... viewIds) {
        for (int i : viewIds) {
            getView(i).setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void clearText(int... viewIds) {
        for (int i : viewIds) {
            clearText(i);
        }
    }

    public void setTextColor(int color, int... viewIds) {
        for (int id : viewIds) {
            TextView tv = getView(id);
            tv.setTextColor(color);
        }
    }

    public void setProgress(int viewId, int progress) {
        ProgressBar bar = getView(viewId);
        if (progress > 0) {
            bar.setProgress(progress);
        } else {
            bar.setProgress(0);
        }
    }

    public void clearProgress(int viewId) {
        ProgressBar bar = getView(viewId);
        bar.setProgress(0);
    }

    @SuppressWarnings("deprecation")
    public void setResourceColor(int colorId, int... viewIds) {
        for (int id : viewIds) {
            TextView tv = getView(id);
            tv.setTextColor(mContext.getResources().getColor(colorId));
        }
    }

    @SuppressWarnings("deprecation")
    public void setResourceColorStateList(int colorId, int... viewIds) {
        for (int id : viewIds) {
            TextView tv = getView(id);
            tv.setTextColor(mContext.getResources().getColorStateList(colorId));
        }
    }


    public void setBackGround(int backGround, int... viewIds) {
        for (int id : viewIds) {
            TextView tv = getView(id);
            tv.setBackgroundResource(backGround);
        }
    }


    @SuppressWarnings("unused")
    public ImageView setImage(int viewId, @DrawableRes int resource) {
        if (resource == -1) return null;
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resource);
        return imageView;
    }


    @SuppressWarnings("unused")
    public ImageView setImage(int viewId, Drawable drawable) {
        ImageView imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
        return imageView;
    }

    public void setImage(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
    }

    void setSelect(int viewId, boolean isSelected) {
        getView(viewId).setSelected(isSelected);
    }

    public String getTextViewText(int viewId) {
        return ((TextView) getView(viewId)).getText().toString();
    }

    public void setBackGroundRes(int[] backRes, int[] viewIds) {
        if (backRes.length != viewIds.length)
            throw new RuntimeException("backRes 和 viewIds 数量不一致");
        for (int i = 0; i < backRes.length; i++) {
            getView(viewIds[i]).setBackgroundResource(backRes[i]);
        }
    }

    @SuppressWarnings("deprecation")
    public void setTextColor(int[] colorsId, int[] viewIds) {
        if (colorsId.length != viewIds.length)
            throw new RuntimeException("colorsId 和 viewIds 数量不一致");
        for (int i = 0; i < colorsId.length; i++) {
            ((TextView) getView(viewIds[i])).setTextColor(mContext.getResources().getColor
                    (colorsId[i]));
        }
    }

    @NonNull
    public String getString(int stringId) {
        return mContext.getResources().getString(stringId);
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T inflateView(ViewGroup container, int layoutId, boolean isAttach) {
        return (T) LayoutInflater.from(mContext).inflate(layoutId, container, isAttach);
    }



    public void goBack(WebView webView){
        if (webView != null && webView.canGoBack()){
            webView.goBack();
        }
    }



    public void postDelay(Runnable runnable, int delay) {
        getContentView().postDelayed(runnable, delay);
    }



    public Uri openFileToUri(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public Intent setFlags(Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent;
    }

    public void clearTextChangeListener(int viewId) {
        EditText editText = getView(viewId);

        try {
            Field mListeners = editText.getClass().getSuperclass().getSuperclass().getDeclaredField("mListeners");
            mListeners.setAccessible(true);
            Object obj = mListeners.get(editText);
            if (obj != null) {
                @SuppressWarnings("unchecked")
                ArrayList<TextWatcher> listeners = (ArrayList<TextWatcher>) obj;
                listeners.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Rect getGlobalRect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }

    public ViewHelper work(Runnable runOnWork) {
        workServices.submit(runOnWork);
        return this;
    }

    public ViewHelper ui(Runnable runOnUI) {
        mUIHandler.post(runOnUI);
        return this;
    }

    public void destroy() {
        workServices.shutdownNow();
        mUIHandler.removeCallbacksAndMessages(null);
    }

}
