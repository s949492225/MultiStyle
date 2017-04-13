package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.MultiStyleHolder;
import com.syiyi.multistyle.MainActivity;
import com.syiyi.multistyle.R;

import java.util.List;

/**
 * 图片
 * Created by songlintao on 2017/1/19.
 */

@Holder("fndsa")
public class ImageHolder extends MultiStyleHolder {

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
    public void renderView(MultiStyleAdapter adapter, int position, List<Object> payloads, OnActionListener mListener) {
        MainActivity.Content item = (MainActivity.Content) adapter.getItem(position);
        ImageView image = (ImageView) itemView.findViewById(R.id.image);
        Glide.with(mContext).load(item.getContent()).into(image);
        TextView tv = (TextView) itemView.findViewById(R.id.text);
        tv.setText(item.text);
    }

}
