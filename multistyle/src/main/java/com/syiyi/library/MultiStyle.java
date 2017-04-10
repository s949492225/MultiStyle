package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多种item
 * Created by songlintao on 2016/9/29.
 */
@SuppressWarnings("WeakerAccess")
public class MultiStyle {

    public interface OnActionListener {
        void onClick(View view, int pos, MultiStyleAdapter adapter, Object... extras);

        void onLongClick(View view, int pos, MultiStyleAdapter adapter, Object... extras);
    }

    /**
     * 仅能在RecycleView中使用
     */
    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        protected View mContentView;
        protected Context mContext;
        protected Activity mActivity;
        protected Fragment mFragment;

        public ViewHolder(View itemView) {
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

    }

    public interface MultiViewModel {
        int getViewTypeId();

        String getViewTypeName();
    }

    @SuppressWarnings("unchecked")
    public static final class MultiStyleAdapter<T extends MultiViewModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "MultiStyleRecycleSaveInstance";
        protected Context mContext;
        private OnActionListener mListener;
        private int mDefaultHolderId = -100000;
        private Activity mActivity;
        private Fragment mFragment;
        private ViewHolderState viewHolderState = new ViewHolderState();
        private final ViewHolders boundViewHolders = new ViewHolders();
        static Method mMethodCreate;

        private Map<String, Object> mTags = new HashMap<>();

        protected List<MultiViewModel> mDatas = new ArrayList<>();

        static {
            try {
                Class helpClass = Class.forName("com.syiyi.holder.H");
                mMethodCreate = helpClass.getMethod("createViewHolder", ViewGroup.class, int.class);
            } catch (Exception e) {
                throw new RuntimeException("init ViewHolderHelper fail ! please rebuild projects");
            }
        }

        public MultiStyleAdapter(Context context) {
            this();
            mContext = context;
        }

        public MultiStyleAdapter(Activity activity) {
            this();
            mContext = activity;
            mActivity = activity;
        }

        public MultiStyleAdapter(Fragment fragment) {
            this();
            mContext = fragment.getContext();
            mFragment = fragment;
        }

        public MultiStyleAdapter() {
            setHasStableIds(true);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                ViewHolder holder = (ViewHolder) mMethodCreate.invoke(null, parent, viewType);
                if (holder == null) {
                    ErrorHolder errorHolder = new ErrorHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_error, parent));
                    errorHolder.setErrorId(viewType);
                    return errorHolder;
                }
                holder.setActivityOrFragment(mActivity, mFragment);
                return holder;
            } catch (Exception e) {
                ErrorHolder errorHolder = new ErrorHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_error, parent));
                errorHolder.setException(e);
                return errorHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            onBindViewHolder(holder, position, Collections.emptyList());
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                if (payloads.isEmpty()) {
                    try {
                        viewHolder.clearView();
                        viewHolderState.restore(viewHolder);
                        viewHolder.renderView(this, position, null, mListener);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            Log.e("multiStyle", "renderViewError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                        }
                    }
                } else {
                    try {
                        viewHolderState.restore(viewHolder);
                        viewHolder.renderView(this, position, payloads, mListener);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            Log.e("multiStyle", "renderViewPlayLoadError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                        }
                    }
                }
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) {
                viewHolderState.save((ViewHolder) holder);
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) {
                ViewHolder styleHolder = (ViewHolder) holder;
                styleHolder.onViewAttachedToWindow();
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) {
                ViewHolder styleHolder = (ViewHolder) holder;
                styleHolder.onViewDetachedFromWindow();
            }
        }

        public void onSaveInstanceState(Bundle outState) {
            for (ViewHolder holder : boundViewHolders) {
                viewHolderState.save(holder);
            }

            if (viewHolderState.size() > 0 && !hasStableIds()) {
                throw new IllegalStateException("Must have stable ids when saving view holder state");
            }

            outState.putParcelable(SAVED_STATE_ARG_VIEW_HOLDERS, viewHolderState);
        }

        public void onRestoreInstanceState(@Nullable Bundle inState) {
            if (boundViewHolders.size() > 0) {
                throw new IllegalStateException(
                        "State cannot be restored once views have been bound. It should be done before adding "
                                + "the adapter to the recycler view.");
            }

            if (inState != null) {
                viewHolderState = inState.getParcelable(SAVED_STATE_ARG_VIEW_HOLDERS);
            }
        }


        @Override
        public long getItemId(int position) {
            return mDatas.get(position).hashCode();
        }


        public void setDefaultHolderId(int holderId) {
            mDefaultHolderId = holderId;
        }

        public void setDefaultHolderName(String holderName) {
            mDefaultHolderId = Integer.valueOf(holderName);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public T getItem(int position) {
            return (T) mDatas.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            MultiViewModel viewModel = getItem(position);
            try {
                type = viewModel.getViewTypeId();
                if (type == -1) {
                    type = Integer.valueOf(viewModel.getViewTypeName());
                }
            } catch (Exception e) {
                throw new RuntimeException("item at position " + position + "->>" + e.getMessage());
            }

            return mDefaultHolderId == -100000 ? type : mDefaultHolderId;
        }

        public void setOnClickListener(OnActionListener listener) {
            mListener = listener;
        }

        public void addDatas(List<T> datas) {
            if (datas == null || datas.isEmpty())
                return;
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }

        public void setDatas(List<T> datas) {
            mDatas.clear();
            addDatas(datas);
        }

        public void setTag(String key, Object value) {
            mTags.put(key, value);
        }

        public void setTags(Map<String, Object> tags) {
            mTags.putAll(tags);
        }

        public Object getTag(String key) {
            return mTags.get(key);
        }

        public Map<String, Object> tags() {
            return mTags;
        }

        public void clearTags() {
            mTags.clear();
        }

    }
}
