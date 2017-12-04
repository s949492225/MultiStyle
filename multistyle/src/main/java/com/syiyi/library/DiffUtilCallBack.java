package com.syiyi.library;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * diffUtil callback
 * Created by Dell on 2017/4/11.
 */

@SuppressWarnings("ALL")
public abstract class DiffUtilCallBack<T extends MultiViewModel> extends DiffUtil.Callback {

    protected List<T> mOldDatas;
    protected List<T> mNewDatas;

    public DiffUtilCallBack loadData(List<T> oldDatas, List<T> newDatas) {
        mOldDatas = oldDatas;
        mNewDatas = newDatas;
        return this;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas == null ? 0 : mOldDatas.size();
    }

    @Override
    public int getNewListSize() {
        return mNewDatas == null ? 0 : mNewDatas.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return areItemsTheSame(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return areContentsTheSame(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return getChangePayload(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
    }

    public abstract boolean areContentsTheSame(T oldModel, T newModel);

    public abstract Object getChangePayload(T oldModel, T newModel);

    public boolean areItemsTheSame(T oldModel, T newModel) {
        return oldModel.hashCode() == newModel.hashCode();
    }
}
