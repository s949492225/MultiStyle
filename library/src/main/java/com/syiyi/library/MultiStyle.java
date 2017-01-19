package com.syiyi.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        protected ViewHelper mHelper;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContentView = itemView;
            mHelper = new ViewHelper(mContentView, mContext);
        }

        @SuppressWarnings("unchecked")
        public static <T extends ViewHolder> T newInstance(ViewGroup parentView, Class<T> holderClazz) {
            Class[] argType = new Class[]{View.class};
            Object[] argParam = new Object[]{new View(parentView.getContext())};
            try {
                Constructor constructor = holderClazz.getDeclaredConstructor(argType);
                constructor.setAccessible(true);
                ViewHolder temp = (ViewHolder) constructor.newInstance(argParam);
                Object[] realArgParam = new Object[]{LayoutInflater.from(parentView.getContext()).inflate(temp.getLayoutId(), parentView, false)};
                return (T) constructor.newInstance(realArgParam);
            } catch (Exception e) {
                throw new RuntimeException("new holder error :" + e.getMessage());
            }
        }

        protected Context getContext() {
            return mContext;
        }

        public View getContentView() {
            return mHelper.getContentView();
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

    public static class DefaultListPoxy implements IProxy {
        @SuppressWarnings("unchecked")
        private List<Object> mDatas = new ArrayList();
        private Map<String, Object> mTags = new HashMap<>();

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
    }


    public static class RecycleViewAdapter extends RecyclerView.Adapter {

        protected Context mContext;
        private IProxy mProxy;
        private OnClickListener mListener;
        private int mDefaultHolderId = -100000;
        static Method mMethodCreate;

        static {
            try {
                Class helpClass = Class.forName("com.syiyi.holder.ViewHolderHelper");
                mMethodCreate = helpClass.getMethod("createViewHolder", ViewGroup.class, int.class);
            } catch (Exception e) {
                throw new RuntimeException("init ViewHolderHelper fail ! please rebuild projects");
            }
        }

        public RecycleViewAdapter(Context context, IProxy proxy) {
            mContext = context;
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
    }
}
