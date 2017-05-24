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
public abstract class MultiStyleHolder extends RecyclerView.ViewHolder {
    protected View mContentView;
    protected Context mContext;
    protected Activity mActivity;
    protected Fragment mFragment;
    protected RecyclerView.Adapter mAdapter;
    protected int mPos;

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

    public abstract void renderView(MultiStyleAdapter adapter, int position, List<Object> payloads, OnActionListener mListener);

    public boolean shouldSaveViewState() {
        return false;
    }

    public void onViewAttachedToWindow() {
    }

    public void onViewDetachedFromWindow() {
    }


    public final long getViewStateKey() {
        return getItemId();
    }

    public interface OnActionListener {
        void onClick(View view, int pos, MultiStyleAdapter adapter, Object... extras);

        void onLongClick(View view, int pos, MultiStyleAdapter adapter, Object... extras);
    }
}
