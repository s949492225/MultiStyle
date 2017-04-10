package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyle;
import com.syiyi.multistyle.MainActivity;
import com.syiyi.multistyle.R;

import java.util.List;

/**
 * 图片
 * Created by songlintao on 2017/1/19.
 */

@Holder("fndsa")
public class ImageHolder extends MultiStyle.ViewHolder {

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
    public void renderView(MultiStyle.MultiStyleAdapter adapter, int position, List<Object> payloads, MultiStyle.OnActionListener mListener) {
        MainActivity.Content item = (MainActivity.Content) adapter.getItem(position);
        ImageView image = (ImageView) itemView.findViewById(R.id.image);
        Glide.with(mContext).load(item.getContent()).into(image);
    }

}
