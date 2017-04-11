package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过的adapter
 * Created by Dell on 2017/4/11.
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class MultiStyleAdapter<T extends MultiViewModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "MultiStyleRecycleSaveInstance";
    private static final String TAG = "multiStyle";
    private static final String CLASS = "com.syiyi.holder.H";
    private static final String METHOD = "createViewHolder";
    protected Context mContext;
    private MultiStyleHolder.OnActionListener mListener;
    private int mDefaultHolderId = -100000;
    protected Activity mActivity;
    protected Fragment mFragment;
    private ViewHolderState viewHolderState = new ViewHolderState();
    private final BoundViewHolders boundViewHolders = new BoundViewHolders();
    private static Method mMethodCreate;
    private Map<String, Object> mTags = new HashMap<>();
    private List<T> mDatas = new ArrayList<>();


    static {
        try {
            Class helpClass = Class.forName(CLASS);
            mMethodCreate = helpClass.getMethod(METHOD, ViewGroup.class, int.class);
        } catch (Exception e) {
            throw new RuntimeException("no found H.class ! please rebuild projects");
        }
    }

    public MultiStyleAdapter(@NonNull Context context) {
        this();
        mContext = context;
    }

    public MultiStyleAdapter(@NonNull Activity activity) {
        this();
        mContext = activity;
        mActivity = activity;
    }

    public MultiStyleAdapter(@NonNull Fragment fragment) {
        this();
        mContext = fragment.getContext();
        mFragment = fragment;
    }

    public MultiStyleAdapter() {
        setHasStableIds(true);
    }

    @Override
    public
    @NonNull
    RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            MultiStyleHolder holder = (MultiStyleHolder) mMethodCreate.invoke(null, parent, viewType);
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
        long startTime = 0;
        if (BuildConfig.DEBUG) {
            startTime = System.currentTimeMillis();
        }
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder viewHolder = (MultiStyleHolder) holder;

            if (payloads.isEmpty()) {
                try {
                    viewHolderState.restore(viewHolder);
                    viewHolder.renderView(this, position, null, mListener);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "renderViewError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                    }
                }
            } else {
                try {
                    viewHolder.clearView();
                    viewHolderState.restore(viewHolder);
                    viewHolder.renderView(this, position, payloads, mListener);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "renderViewPlayLoadError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                    }
                }
            }

            boundViewHolders.put(viewHolder);

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onBindViewHolder: " + position + ">>" + (System.currentTimeMillis() - startTime) + ">>" + holder.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder viewHolder = (MultiStyleHolder) holder;

            viewHolderState.save(viewHolder);
            boundViewHolders.remove(viewHolder);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder styleHolder = (MultiStyleHolder) holder;
            styleHolder.onViewAttachedToWindow();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder styleHolder = (MultiStyleHolder) holder;
            styleHolder.onViewDetachedFromWindow();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        for (MultiStyleHolder holder : boundViewHolders) {
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

    public
    @NonNull
    T getItem(int position) {
        return mDatas.get(position);
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

    public void setOnClickListener(@NonNull MultiStyleHolder.OnActionListener listener) {
        mListener = listener;
    }


    @NonNull
    private List<T> createNewDatas() {
        List<T> temp = new ArrayList<>();
        temp.addAll(mDatas);
        return temp;
    }

    public void setList(@NonNull List<T> datas) {
        mDatas.clear();
        insertList(datas);
    }

    public void insertList(@NonNull List<T> datas) {
        datas.addAll(0, mDatas);
        notifyChange(datas);
    }

    public void insertList(int index, @NonNull List<T> datas) {
        if (index < 0 || index > mDatas.size() - 1) return;
        List<T> temp = createNewDatas();
        temp.addAll(index, datas);
        notifyChange(temp);
    }

    public void insertOne(@NonNull T data) {
        List<T> temp = new ArrayList<>();
        temp.add(data);
        insertList(temp);
    }

    public void insertOne(int index, @NonNull T data) {
        List<T> temp = new ArrayList<>();
        temp.add(data);
        insertList(index, temp);
    }

    public void removeList(int index, int count) {
        if (mDatas.size() == 0 || index < 0 || index > mDatas.size() - 1 || index + count > mDatas.size()) {
            return;
        }
        List<T> temp = createNewDatas();
        List<T> del = new ArrayList<>();
        int i = index;
        for (; i < index + count; i++) {
            del.add(temp.get(i));
        }
        temp.removeAll(del);
        notifyChange(temp);
    }

    public void removeFirst() {
        if (mDatas.size() == 0) return;
        removeList(0, 1);
    }

    public void removeLast() {
        if (mDatas.size() == 0)
            return;
        removeList(mDatas.size() - 1, 1);
    }

    public void updateList(@NonNull List<T> oldDatas, @NonNull List<T> newDatas) {
        if (oldDatas.size() != newDatas.size() || oldDatas.size() == 0 || newDatas.size() == 0)
            return;
        List<T> temp = createNewDatas();
        for (int i = 0; i < oldDatas.size(); i++) {
            T oldData = oldDatas.get(i);
            T newData = newDatas.get(i);
            int index = temp.indexOf(oldData);
            if (index == -1)
                throw new RuntimeException("updateOne:" + "oldData not found in oldList");
            temp.set(index, newData);
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, temp), true);
        diffResult.dispatchUpdatesTo(this);
        for (int i = 0; i < oldDatas.size(); i++) {
            T oldData = oldDatas.get(i);
            T newData = newDatas.get(i);
            oldData.resetPlayLoadData(newData);
        }
    }


    public void updateOne(@NonNull T oldData, @NonNull T newData) {
        List<T> temp = createNewDatas();
        int index = temp.indexOf(oldData);
        if (index == -1)
            throw new RuntimeException("updateOne:" + "oldData not found in oldList");
        temp.set(index, newData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, temp), true);
        diffResult.dispatchUpdatesTo(this);
        oldData.resetPlayLoadData(newData);
    }

    private void notifyChange(@NonNull List<T> newDatas) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, newDatas), true);
        diffResult.dispatchUpdatesTo(this);
        mDatas = newDatas;
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
