package com.syiyi.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * 未知id提示的holder
 * Created by Dell on 2017/3/21.
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
