package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.EditText;

import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyle;
import com.syiyi.multistyle.R;

import java.util.List;

/**
 * text
 * Created by songlintao on 2017/1/19.
 */
@Holder
public class TextHolder extends MultiStyle.ViewHolder {
    private EditText text;

    public TextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.holder_text;
    }

    @Override
    public void clearView() {
        text = (EditText) itemView.findViewById(R.id.et);
        text.setText(null);
    }

    @Override
    public void renderView(MultiStyle.MultiStyleAdapter adapter, int position, List<Object> payloads, MultiStyle.OnActionListener mListener) {

    }

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }
}