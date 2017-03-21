package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 多种item
 * Created by songlintao on 2016/9/29.
 */
public class MultiStyle {

    public interface OnClickListener {
        void onClick(View view, int pos, IProxy proxy, Object... extras);
    }

    /**
     * 仅能在RecycleView中使用
     */
    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        private View mContentView;
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

        public abstract void renderView(IProxy proxy, int pos, OnClickListener listener);

    }

    public interface IProxy<T> {
        int getCount();

        Object getItem(int pos);

        int getItemViewType(int position);

        void addData(T t);

        void clear();

    }

    public static class DefaultListPoxy extends TagsProxy implements IProxy {
        @SuppressWarnings("unchecked")
        private List<Object> mDatas = new ArrayList();

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int pos) {
            return mDatas.get(pos);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }


        @SuppressWarnings("unchecked")
        @Override
        public void addData(Object o) {
            mDatas.addAll((Collection) o);
        }

        @Override
        public void clear() {
            mDatas.clear();
        }
    }


    public static class RecycleViewAdapter extends RecyclerView.Adapter {
        protected Context mContext;
        private IProxy mProxy;
        private OnClickListener mListener;
        private int mDefaultHolderId = -100000;
        private Activity mActivity;
        private Fragment mFragment;

        static Method mMethodCreate;

        static {
            try {
                Class helpClass = Class.forName("com.syiyi.holder.H");
                mMethodCreate = helpClass.getMethod("createViewHolder", ViewGroup.class, int.class);
            } catch (Exception e) {
                throw new RuntimeException("init ViewHolderHelper fail ! please rebuild projects");
            }
        }

        public RecycleViewAdapter(Context context, IProxy proxy) {
            mContext = context;
            mProxy = proxy;
        }

        public RecycleViewAdapter(Activity activity, IProxy proxy) {
            mContext = activity;
            mActivity = activity;
            mProxy = proxy;
        }

        public RecycleViewAdapter(Fragment fragment, IProxy proxy) {
            mContext = fragment.getContext();
            mFragment = fragment;
            mProxy = proxy;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                return (RecyclerView.ViewHolder) mMethodCreate.invoke(null, parent, viewType);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setActivityOrFragment(mActivity, mFragment);
            try {
                viewHolder.clearView();
                viewHolder.renderView(mProxy, position, mListener);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e("renderHolderError", viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                }
            }
        }

        public void setDefaultHolderId(int holderId) {
            mDefaultHolderId = holderId;
        }

        @Override
        public int getItemCount() {
            return mProxy.getCount();
        }


        @Override
        public int getItemViewType(int position) {
            return mDefaultHolderId == -100000 ? mProxy.getItemViewType(position) : mDefaultHolderId;
        }

        public void setOnClickListener(OnClickListener listener) {
            mListener = listener;
        }

        public IProxy getProxy() {
            return mProxy;
        }

        @SuppressWarnings("unchecked")
        public void addData(Object t) {
            if (t == null) return;
            mProxy.addData(t);
            notifyDataSetChanged();
        }


        public void clear() {
            mProxy.clear();
        }

        public void setData(Object data) {
            clear();
            addData(data);
        }

    }
}
