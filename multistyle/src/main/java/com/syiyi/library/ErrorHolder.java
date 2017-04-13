package com.syiyi.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * it will show when no id found in H
 * Created by syiyi on 2017/3/21.
 */

class ErrorHolder extends RecyclerView.ViewHolder {

    ErrorHolder(View itemView) {
        super(itemView);
    }

    void setErrorId(int id) {
        TextView tv = (TextView) itemView.findViewById(R.id.msg);
        tv.setText("no id found :" + id + "in H,please check it");
    }

    void setException(Exception e) {
        TextView tv = (TextView) itemView.findViewById(R.id.msg);
        tv.setText("create holder error \r\n" + e.getMessage() + "");
    }


}
