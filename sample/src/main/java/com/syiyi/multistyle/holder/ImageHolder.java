package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.MultiStyleHolder;
import com.syiyi.multistyle.Content;
import com.syiyi.multistyle.R;

import java.util.List;

/**
 * 图片
 * Created by songlintao on 2017/1/19.
 */

@Holder("fndsa")
public class ImageHolder extends MultiStyleHolder<Content> {

    public ImageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.holder_image;
    }

    @Override
    public void clearView() {

    }

    @Override
    public void renderView(MultiStyleAdapter adapter, Content item, List<Object> payloads, OnActionListener<Content> mListener) {
        ImageView image = (ImageView) itemView.findViewById(R.id.image);
        Glide.with(mContext).load(item.getContent()).into(image);
        TextView tv = (TextView) itemView.findViewById(R.id.text);
        tv.setText(item.text);
    }

}
