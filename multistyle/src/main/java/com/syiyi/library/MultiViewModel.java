package com.syiyi.library;

/**
 * viewModel,一般来说需要转换
 * Created by Dell on 2017/4/11.
 */
public interface MultiViewModel {

    int getViewTypeId();

    String getViewTypeName();

    boolean areContentsTheSame(MultiViewModel newModel);

    Object getChangePayload(MultiViewModel newModel);

    boolean areItemsTheSame(MultiViewModel newModel);

    void resetPlayLoadData(MultiViewModel newModel);
}
