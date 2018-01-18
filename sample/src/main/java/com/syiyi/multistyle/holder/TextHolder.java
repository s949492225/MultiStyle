package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.EditText;

import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.MultiStyleHolder;
import com.syiyi.multistyle.Content;
import com.syiyi.multistyle.R;

import java.util.List;

/**
 * text
 * Created by songlintao on 2017/1/19.
 */
@Holder("text")
public class TextHolder extends MultiStyleHolder<Content> {
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
    public void renderView(MultiStyleAdapter adapter, Content content, List<Object> payloads, OnActionListener mListener) {

        text = (EditText) itemView.findViewById(R.id.et);
        if (payloads != null) {
            text.setHint((String) payloads.get(0));
        } else {
            text.setHint(content.getContent());
        }
    }

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }

}