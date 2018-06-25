package com.syiyi.mylibrary;

import android.view.View;

import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.MultiStyleHolder;

import java.util.List;

@Holder
public class Test extends MultiStyleHolder<String> {
    public Test(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void clearView() {

    }

    @Override
    public void renderView(MultiStyleAdapter adapter, String model, List<Object> payloads, OnActionListener<String> mListener) {
    }
}
