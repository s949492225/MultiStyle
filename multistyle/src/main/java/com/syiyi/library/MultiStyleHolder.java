package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * 仅能在RecycleView中使用
 */
@SuppressWarnings("All")
public abstract class MultiStyleHolder<T> extends RecyclerView.ViewHolder {
    protected View mContentView;
    protected Context mContext;
    protected Activity mActivity;
    protected Fragment mFragment;

    public MultiStyleHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mContentView = itemView;
    }

    protected Context getContext() {
        return mContext;
    }

    public View getContentView() {
        return mContentView;
    }

    public void setActivityOrFragment(Activity activity, Fragment fragment) {
        mActivity = activity;
        mFragment = fragment;
    }

    /**
     * @return holder的view的layoutId
     */
    public abstract int getLayoutId();

    public abstract void clearView();

    public abstract void renderView(MultiStyleAdapter adapter, T model, List<Object> payloads, OnActionListener<T> mListener);

    public boolean shouldSaveViewState() {
        return false;
    }

    public void onViewAttachedToWindow() {
    }

    public void onViewDetachedFromWindow() {
    }

    public void onViewRecycled() {

    }


    public interface OnActionListener<T> {
        void onClick(View view, T model, MultiStyleAdapter adapter, Object... extras);

        void onLongClick(View view, T model, MultiStyleAdapter adapter, Object... extras);
    }
}
