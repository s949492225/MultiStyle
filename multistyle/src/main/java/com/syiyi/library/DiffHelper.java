package com.syiyi.library;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据比对工具类
 * Created by songlintao on 2017/4/11.
 */

@SuppressWarnings("ALL")
public class DiffHelper<T extends MultiViewModel> {
    private MultiStyleAdapter mAdapter;
    private List<T> mDatas;
    private List<T> mNewData;
    private ExecutorService mWorker = Executors.newSingleThreadExecutor();
    private final int DATA_SIZE_CHANGE = 0X0001;
    private final int DATA_UPDATE_ONE = 0X0002;
    private final int DATA_UPDATE_LIST = 0X0003;
    private Lock mLock = new ReentrantLock();

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
                    diffResult.dispatchUpdatesTo(mAdapter);
                    mAdapter.setDataSource(mNewData);
                    break;
                case DATA_UPDATE_ONE:
                    temp = (Object[]) msg.obj;

                    diffResult = (DiffUtil.DiffResult) temp[0];
                    oldData = (T) temp[1];
                    newData = (T) temp[2];

                    diffResult.dispatchUpdatesTo(mAdapter);
                    oldData.resetPlayLoadData(newData);
                    break;
                case DATA_UPDATE_LIST:
                    temp = (Object[]) msg.obj;

                    diffResult = (DiffUtil.DiffResult) temp[0];
                    diffResult.dispatchUpdatesTo(mAdapter);

                    List<T> oldDatas = (List<T>) temp[1];
                    List<T> newDatas = (List<T>) temp[2];

                    for (int i = 0; i < oldDatas.size(); i++) {
                        oldData = oldDatas.get(i);
                        newData = newDatas.get(i);
                        oldData.resetPlayLoadData(newData);
                    }
                    break;
            }
            mLock.unlock();
        }
    };

    public DiffHelper(MultiStyleAdapter adapter) {
        this.mAdapter = adapter;
        mDatas = mAdapter.getDataSource();
    }

    public void onDestory() {
        mHandler.removeCallbacksAndMessages(null);
        mWorker.shutdownNow();
    }

    @NonNull
    public List<T> createNewDatas() {
        List<T> temp = new ArrayList<>();
        temp.addAll(mAdapter.getDataSource());
        return temp;
    }


    public void setList(@NonNull List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }


    public void insertList(@NonNull List<T> datas) {
        int start = mDatas.size();
        mDatas.addAll(datas);
        mAdapter.notifyItemRangeInserted(start, datas.size());
    }

    public void insertList(int index, @NonNull List<T> datas) {
        if (index < 0 || index > mDatas.size() - 1) return;
        List<T> temp = createNewDatas();
        temp.addAll(index, datas);
        mNewData = temp;
        executeChange("insertList2");
    }

    public void insertLast(@NonNull T data) {
        List<T> temp = new ArrayList<>();
        temp.add(data);
        insertList(temp);
    }

    public void insertFirst(@NonNull T data) {
        List<T> temp = new ArrayList<>();
        temp.add(0, data);
        insertList(0, temp);
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
        mNewData = temp;
        executeChange("removeList2");
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

    public void updateList(@NonNull final List<T> oldDatas, @NonNull final List<T> newDatas) {
        if (oldDatas.size() != newDatas.size() || oldDatas.size() == 0 || newDatas.size() == 0)
            return;
        mWorker.execute(new Runnable() {
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

                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, temp), true);

                        Message message = mHandler.obtainMessage();
                        message.what = DATA_UPDATE_LIST;
                        message.obj = new Object[]{diffResult, oldDatas, newDatas};
                        mHandler.sendMessage(message);
                    }
                }.run();
            }
        });
    }

    public void updateOne(@NonNull final T oldData, @NonNull final T newData) {
        mWorker.execute(new Runnable() {
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

                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, temp), true);


                        Message message = mHandler.obtainMessage();
                        message.what = DATA_UPDATE_ONE;
                        message.obj = new Object[]{diffResult, oldData, newData};
                        mHandler.sendMessage(message);
                    }
                }.run();

            }
        });

    }

    public void batchOperate(@NonNull final List<T> newData) {
        this.mDatas = newData;
        executeChange("batchOperate");
    }

    private void executeChange(final String action) {
        mWorker.execute(new Runnable() {
            @Override
            public void run() {
                new WorkInvoker(action) {
                    @Override
                    void invoke() {
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallBack(mDatas, mNewData), true);
                        Message message = mHandler.obtainMessage();
                        message.what = DATA_SIZE_CHANGE;
                        message.obj = diffResult;
                        mHandler.sendMessage(message);
                    }
                }.run();

            }
        });

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
            mLock.lock();
            invoke();
            if (MultiStyleAdapter.enableDebug) {
                Log.d(MultiStyleAdapter.TAG, "WorkInvoker-" + name + ":" + +(System.currentTimeMillis() - startTime));
            }
        }
    }


}
