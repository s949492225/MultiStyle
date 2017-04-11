package com.syiyi.library;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * diffUtil callback
 * Created by Dell on 2017/4/11.
 */

public class DiffUtilCallBack<T extends MultiViewModel> extends DiffUtil.Callback {

    private List<T> mOldDatas;
    private List<T> mNewDatas;

    public DiffUtilCallBack(List<T> oldDatas, List<T> newDatas) {
        mOldDatas = oldDatas;
        mNewDatas = newDatas;
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
        T oldModel = mOldDatas.get(oldItemPosition);
        T newModel = mNewDatas.get(newItemPosition);
        return oldModel.areItemsTheSame(newModel) ;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldModel = mOldDatas.get(oldItemPosition);
        T newModel = mNewDatas.get(newItemPosition);
        return oldModel.areContentsTheSame(newModel);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        T oldModel = mOldDatas.get(oldItemPosition);
        T newModel = mNewDatas.get(newItemPosition);
        return oldModel.getChangePayload(newModel);
    }
}
