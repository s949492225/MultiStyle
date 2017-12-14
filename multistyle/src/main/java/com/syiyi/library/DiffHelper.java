package com.syiyi.library;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据比对工具类
 * Created by songlintao on 2017/4/11.
 */

@SuppressWarnings("ALL")
public class DiffHelper<T extends MultiViewModel> {
    private MultiStyleAdapter<T> mAdapter;
    private List<T> mDatas;
    private List<T> mNewData;
    private ExecutorService mWorker = Executors.newSingleThreadExecutor();
    private final int DATA_SIZE_CHANGE = 0X0001;
    private final int DATA_UPDATE_ONE = 0X0002;
    private final int DATA_UPDATE_LIST = 0X0003;
    private boolean isRuning = false;
    private boolean mEableMultiThread = false;
    private DiffUtilCallBack mDiffCallBack;


    public void setEableMultiThread(boolean eableMultiThread) {
        mEableMultiThread = eableMultiThread;
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
                    diffResult.dispatchUpdatesTo(mAdapter);
                    setDataSource(mNewData);
                    break;
                case DATA_UPDATE_ONE:
                    temp = (Object[]) msg.obj;

                    diffResult = (DiffUtil.DiffResult) temp[0];
                    oldData = (T) temp[1];
                    newData = (T) temp[2];

                    diffResult.dispatchUpdatesTo(mAdapter);
                    Collections.replaceAll(mDatas, oldData, newData);
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
                        Collections.replaceAll(mDatas, oldData, newData);
                    }
                    break;
            }
            isRuning = false;
        }
    };

    private void setDataSource(List<T> datas) {
        if (datas == null)
            throw new RuntimeException("数据异常");
        mAdapter.getDataSource().clear();
        mAdapter.getDataSource().addAll(datas);
    }


    public DiffHelper(MultiStyleAdapter<T> adapter, DiffUtilCallBack diffCallBack) {
        this.mAdapter = adapter;
        mDatas = mAdapter.getDataSource();
        mDiffCallBack = diffCallBack;
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

    @NonNull
    public List<T> getDataSource() {
        return mAdapter.getDataSource();
    }

    public void setList(@NonNull List<T> datas) {
        if (datas == null) throw new RuntimeException("数据异常");
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }


    public void insertList(@NonNull List<T> datas) {
        if (datas == null) throw new RuntimeException("数据异常");
        int start = mDatas.size();
        insertList(start, datas);
    }

    public void insertList(int index, @NonNull List<T> datas) {
        if (index < 0 || (index > 0 & index > mDatas.size())) throw new RuntimeException("数据异常");
        mDatas.addAll(index, datas);
        mAdapter.notifyItemRangeInserted(index, datas.size());
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
        mAdapter.notifyItemRangeRemoved(index, count);
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
        if (oldDatas.size() != newDatas.size() || oldDatas.size() == 0 || newDatas.size() == 0 )
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

    //后台线程处理的部分======end

    public void updateOneStraight(int pos, Object playLoad) {
        if (playLoad == null)
            mAdapter.notifyItemChanged(pos);
        else
            mAdapter.notifyItemChanged(pos, playLoad);
    }

    public boolean isWorking() {
        return isRuning;
    }

    public T getItemById(long id) {

        return mAdapter.getItemById(id);
    }

    public T getItemByPos(int pos) {

        return mAdapter.getItem(pos);
    }

    public int getItemPosById(long id) {
        return mAdapter.getItemPosById(id);
    }

    public MultiStyleAdapter getAdapter() {
        return mAdapter;
    }

    public void notifyAllDataChange() {
        mAdapter.notifyDataSetChanged();
    }

    public void sendMessage(Message msg) {
        if (mEableMultiThread) {
            mHandler.sendMessage(msg);
        } else {
            mHandler.dispatchMessage(msg);
        }
    }

    public void work(Runnable runnable) {
        if (mEableMultiThread) {
            mWorker.execute(runnable);
        } else {
            runnable.run();
        }
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
