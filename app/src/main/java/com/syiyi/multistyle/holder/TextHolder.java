package com.syiyi.multistyle.holder;

import android.view.View;

import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyle;
import com.syiyi.multistyle.MainActivity;
import com.syiyi.multistyle.R;

/**
 * text
 * Created by songlintao on 2017/1/19.
 */
@Holder
public class TextHolder extends MultiStyle.ViewHolder {

    public TextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.holder_text;
    }

    @Override
    public void clearView() {

    }

    @Override
    public void renderView(MultiStyle.IProxy proxy, int pos, MultiStyle.OnClickListener listener) {
        MainActivity.Content item = (MainActivity.Content) proxy.getItem(pos);
        mHelper.setText(R.id.text, item.getContent());
    }
}
