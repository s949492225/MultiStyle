package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
@SuppressWarnings("All")
public class MultiStyleAdapter<T extends MultiViewModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "MultiStyleRecycleSaveInstance";
    static final String TAG = "multiStyle";
    private static final String CLASS = "com.syiyi.holder.H";
    private static final String METHOD_CREATE_HOLDER = "createViewHolder";
    private static final String METHOD_GETIDBYNAME = "getIdByName";
    protected Context mContext;
    private MultiStyleHolder.OnActionListener mListener;
    private int mDefaultHolderId = -100000;
    protected Activity mActivity;
    protected Fragment mFragment;
    private ViewHolderState viewHolderState = new ViewHolderState();
    private final BoundViewHolders boundViewHolders = new BoundViewHolders();
    private static Method mMethodCreate;
    private static Method mMethodGetIdByName;
    private Map<String, Object> mTags = new HashMap<>();
    protected List<T> mDatas = new ArrayList<>();
    protected RecyclerView.RecycledViewPool mChildRecycledViewPool = new RecyclerView.RecycledViewPool();
    static boolean enableDebug = false;

    public RecyclerView.RecycledViewPool getChildRecycledViewPool() {
        return mChildRecycledViewPool;
    }

    public void setChildRecycledViewPool(RecyclerView.RecycledViewPool childRecycledViewPool) {
        this.mChildRecycledViewPool = childRecycledViewPool;
    }

    public static void setDebug(boolean enableDebug) {
        MultiStyleAdapter.enableDebug = enableDebug;
    }

    static {
        try {
            Class helpClass = Class.forName(CLASS);
            mMethodCreate = helpClass.getMethod(METHOD_CREATE_HOLDER, ViewGroup.class, int.class);
            mMethodGetIdByName = helpClass.getMethod(METHOD_GETIDBYNAME, String.class);
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
                ErrorHolder errorHolder = new ErrorHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_error, parent, false));
                errorHolder.setErrorId(viewType);
                return errorHolder;
            }
            holder.setActivityOrFragment(mActivity, mFragment);
            return holder;
        } catch (Exception e) {
            ErrorHolder errorHolder = new ErrorHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_error, parent, false));
            errorHolder.setException(e);
            return errorHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, position, Collections.emptyList());
    }

    private int getViewIdByName(String name) {
        try {
            return (int) mMethodGetIdByName.invoke(null, name);
        } catch (Exception e) {
            return -100000;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        long startTime = 0;
        if (enableDebug) {
            startTime = System.currentTimeMillis();
        }
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder viewHolder = (MultiStyleHolder) holder;
            if (payloads.isEmpty()) {
                try {
                    viewHolder.clearView();
                    viewHolder.renderView(this, position, null, mListener);
                    viewHolderState.restore(viewHolder);
                } catch (Exception e) {
                    if (enableDebug) {
                        Log.e(TAG, "renderViewError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                    }
                }
            } else {
                try {
                    viewHolder.renderView(this, position, payloads, mListener);
                    viewHolderState.restore(viewHolder);
                } catch (Exception e) {
                    if (enableDebug) {
                        Log.e(TAG, "renderViewPlayLoadError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                    }
                }
            }

            boundViewHolders.put(viewHolder);

            if (enableDebug) {
                Log.d(TAG, "onBindViewHolder: pos>>" + position + "time>>" + (System.currentTimeMillis() - startTime) + ">>" + holder.getClass().getSimpleName());
            }
        }
    }

    public T getItemById(long id) {
        List<T> dataSource = getDataSource();
        for (T a : dataSource) {
            if (a.hashCode() == id)
                return a;
        }
        return null;
    }

    public int getItemPosById(long id) {
        List<T> dataSource = getDataSource();
        T a = getItemById(id);
        if (a != null)
            return dataSource.indexOf(a);
        else
            return -1;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof MultiStyleHolder) {
            MultiStyleHolder viewHolder = (MultiStyleHolder) holder;
            viewHolderState.save(viewHolder);
            viewHolder.onViewRecycled();

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
        mDefaultHolderId = getViewIdByName(holderName);
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
        T viewModel = getItem(position);
        try {
            String name = viewModel.getViewTypeName();
            if (!TextUtils.isEmpty(name)) {
                type = getViewIdByName(name);
            } else {
                type = viewModel.getViewTypeId();
            }
        } catch (Exception e) {
            throw new RuntimeException("item at position " + position + "->>" + e.getMessage());
        }

        return mDefaultHolderId == -100000 ? type : mDefaultHolderId;
    }

    public void setOnClickListener(@NonNull MultiStyleHolder.OnActionListener listener) {
        mListener = listener;
    }

    public List<T> getDataSource() {
        return mDatas;
    }

    public void setDataSource(List<T> datas) {
        mDatas = datas;
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
