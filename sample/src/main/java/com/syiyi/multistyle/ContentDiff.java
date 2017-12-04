package com.syiyi.multistyle;

import com.syiyi.library.DiffUtilCallBack;

/**
 * diff
 * Created by songlintao on 2017/12/4.
 */
public class ContentDiff extends DiffUtilCallBack<Content> {
    @Override
    public boolean areContentsTheSame(Content oldModel, Content newModel) {
        return oldModel.getContent().equals(newModel.getContent());
    }

    @Override
    public Object getChangePayload(Content oldModel, Content newModel) {
        return newModel.getContent();
    }
}
