package com.syiyi.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private MultiStyleHolder.OnActionListener<T> mListener;
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


    private List<T> mNewData;
    private ExecutorService mWorker;
    private final int DATA_SIZE_CHANGE = 0X0001;
    private final int DATA_UPDATE_ONE = 0X0002;
    private final int DATA_UPDATE_LIST = 0X0003;
    private boolean isRuning = false;
    private boolean mEnableMultiThread = false;
    private DiffUtilCallBack mDiffCallBack;
    static boolean enableDebug = false;

    public void setEableMultiThread(boolean enableMultiThread) {
        mEnableMultiThread = enableMultiThread;
        mWorker=enableMultiThread?Executors.newSingleThreadExecutor():null;
    }

    @NonNull
    public List<T> createNewDatas() {
        List<T> temp = new ArrayList<>();
        temp.addAll(getDataSource());
        return temp;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void dispatchMessage(Message msg) {
            DiffUtil.DiffResult diffResult;
            Object[] temp;
            T oldData;
            T newData;
            switch (msg.what) {
                case DATA_SIZE_CHANGE:
                    diffResult = (DiffUtil.DiffResult) msg.obj;
                    diffResult.dispatchUpdatesTo(MultiStyleAdapter.this);
                    fixDataSource(mNewData);
                    break;
                case DATA_UPDATE_ONE:
                    temp = (Object[]) msg.obj;

                    diffResult = (DiffUtil.DiffResult) temp[0];
                    oldData = (T) temp[1];
                    newData = (T) temp[2];

                    diffResult.dispatchUpdatesTo(MultiStyleAdapter.this);
                    Collections.replaceAll(mDatas, oldData, newData);
                    break;
                case DATA_UPDATE_LIST:
                    temp = (Object[]) msg.obj;

                    diffResult = (DiffUtil.DiffResult) temp[0];
                    diffResult.dispatchUpdatesTo(MultiStyleAdapter.this);

                    List<T> oldDatas = (List<T>) temp[1];
                    List<T> newDatas = (List<T>) temp[2];

                    for (int i = 0; i < oldDatas.size(); i++) {
                        oldData = oldDatas.get(i);
                        newData = newDatas.get(i);
                        Collections.replaceAll(mDatas, oldData, newData);
                    }
                    break;
            }
            isRuning = false;
        }
    };

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

    public void setDiffCallBack(DiffUtilCallBack diffCallBack) {
        mDiffCallBack = diffCallBack;
    }

    public void onDestory() {
        mHandler.removeCallbacksAndMessages(null);
        mWorker.shutdownNow();
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
            T model = getItem(position);
            MultiStyleHolder viewHolder = (MultiStyleHolder) holder;
            if (payloads.isEmpty()) {
                try {
                    viewHolder.clearView();
                    viewHolder.renderView(this, model, null, mListener);
                    viewHolderState.restore(viewHolder);
                } catch (Exception e) {
                    if (enableDebug) {
                        Log.e(TAG, "renderViewError:" + viewHolder.getClass().getSimpleName() + ":" + e.getMessage());
                    }
                }
            } else {
                try {
                    viewHolder.renderView(this, model, payloads, mListener);
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

    public void setOnClickListener(@NonNull MultiStyleHolder.OnActionListener<T> listener) {
        mListener = listener;
    }

    public List<T> getDataSource() {
        return mDatas;
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


    private void fixDataSource(@NonNull List<T> datas) {
        if (datas == null) throw new RuntimeException("数据异常");
        mDatas.clear();
        mDatas.addAll(datas);
    }

    public void setList(@NonNull List<T> datas) {
        if (datas == null) throw new RuntimeException("数据异常");
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void insertList(@NonNull List<T> datas) {
        if (datas == null) throw new RuntimeException("数据异常");
        int start = mDatas.size();
        insertList(start, datas);
    }

    public void insertList(int index, @NonNull List<T> datas) {
        if (index < 0 || (index > 0 & index > mDatas.size())) throw new RuntimeException("数据异常");
        mDatas.addAll(index, datas);
        notifyItemRangeInserted(index, datas.size());
    }

    public void insertLast(@NonNull T data) {
        if (data == null)
            throw new RuntimeException("数据异常");
        List<T> temp = new ArrayList<>();
        temp.add(data);
        insertList(temp);
    }

    public void insertFirst(@NonNull T data) {
        if (data == null)
            throw new RuntimeException("数据异常");
        List<T> temp = new ArrayList<>();
        temp.add(0, data);
        insertList(0, temp);
    }

    public void insertOne(int index, @NonNull T data) {
        if (index < 0 || data == null)
            throw new RuntimeException("数据异常");
        List<T> temp = new ArrayList<>();
        temp.add(data);
        insertList(index, temp);
    }


    public void removeList(int index, int count) {
        if (mDatas.size() == 0 || index < 0 || index > mDatas.size() - 1 || index + count > mDatas.size()) {
            throw new RuntimeException("数据异常");
        }
        mDatas.subList(index, index + count).clear();
        notifyItemRangeRemoved(index, count);
    }

    public void removeFirst() {
        if (mDatas.size() == 0) throw new RuntimeException("数据异常");
        removeList(0, 1);
    }

    public void removeLast() {
        if (mDatas.size() == 0)
            throw new RuntimeException("数据异常");
        removeList(mDatas.size() - 1, 1);
    }


    //后台线程处理的部分======begin
    public void updateList(@NonNull final List<T> oldDatas, @NonNull final List<T> newDatas) {
        if (oldDatas.size() != newDatas.size() || oldDatas.size() == 0 || newDatas.size() == 0)
            throw new RuntimeException("数据异常或者有任务正在执行");
        if (isRuning)
            return;
        isRuning = true;
        work(new Runnable() {
            @Override
            public void run() {
                new WorkInvoker("updateList") {
                    @Override
                    void invoke() {
                        List<T> temp = createNewDatas();
                        for (int i = 0; i < oldDatas.size(); i++) {
                            T oldData = oldDatas.get(i);
                            T newData = newDatas.get(i);
                            int index = temp.indexOf(oldData);
                            if (index == -1)
                                throw new RuntimeException("updateOne:" + "oldData not found in oldList");
                            temp.set(index, newData);
                        }
                        checkDiffCallBack();
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffCallBack.loadData(mDatas, temp), true);

                        Message message = mHandler.obtainMessage();
                        message.what = DATA_UPDATE_LIST;
                        message.obj = new Object[]{diffResult, oldDatas, newDatas};
                        sendMessage(message);
                    }
                }.run();
            }
        });
    }

    public void updateOne(@NonNull final T oldData, @NonNull final T newData) {
        if (oldData == null || newData == null)
            throw new RuntimeException("数据异常或者有任务正在执行");
        if (isRuning)
            return;
        isRuning = true;
        work(new Runnable() {
            @Override
            public void run() {
                new WorkInvoker("updateOne") {
                    @Override
                    void invoke() {
                        List<T> temp = createNewDatas();
                        int index = temp.indexOf(oldData);
                        if (index == -1)
                            throw new RuntimeException("updateOne:" + "oldData not found in oldList");
                        temp.set(index, newData);

                        checkDiffCallBack();
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffCallBack.loadData(mDatas, temp), true);

                        Message message = mHandler.obtainMessage();
                        message.what = DATA_UPDATE_ONE;
                        message.obj = new Object[]{diffResult, oldData, newData};
                        sendMessage(message);
                    }
                }.run();

            }
        });

    }

    public void batchOperate(@NonNull final List<T> newData) {
        if (newData == null)
            throw new RuntimeException("数据异常或者有任务正在执行");
        if (isRuning)
            return;
        isRuning = true;
        this.mNewData = newData;
        work(new Runnable() {
            @Override
            public void run() {
                new WorkInvoker("batchOperate") {
                    @Override
                    void invoke() {
                        checkDiffCallBack();
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffCallBack.loadData(mDatas, mNewData), true);
                        Message message = mHandler.obtainMessage();
                        message.what = DATA_SIZE_CHANGE;
                        message.obj = diffResult;
                        sendMessage(message);
                    }
                }.run();

            }
        });
    }

    public void updateOneStraight(int pos, Object playLoad) {
        if (playLoad == null)
            notifyItemChanged(pos);
        else
            notifyItemChanged(pos, playLoad);
    }


    //后台线程处理的部分======end
    public boolean isWorking() {
        return isRuning;
    }

    public T getItemByPos(int pos) {

        return getItem(pos);
    }

    public void sendMessage(Message msg) {
        if (mEnableMultiThread) {
            mHandler.sendMessage(msg);
        } else {
            mHandler.dispatchMessage(msg);
        }
    }

    public void work(Runnable runnable) {
        if (mEnableMultiThread) {
            mWorker.execute(runnable);
        } else {
            runnable.run();
        }
    }

    private void checkDiffCallBack() {
        if (mDiffCallBack == null)
            throw new RuntimeException("此方法需要设置 diffCallBack");
    }

    abstract class WorkInvoker {
        private String name;

        abstract void invoke();

        WorkInvoker(String name) {
            this.name = name;
        }

        void run() {
            long startTime = 0;
            if (MultiStyleAdapter.enableDebug) {
                startTime = System.currentTimeMillis();

            }
            invoke();
            if (MultiStyleAdapter.enableDebug) {
                Log.d(MultiStyleAdapter.TAG, "WorkInvoker-" + name + ":" + +(System.currentTimeMillis() - startTime));
            }
        }
    }


}
