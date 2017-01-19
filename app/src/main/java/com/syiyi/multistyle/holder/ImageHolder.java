package com.syiyi.multistyle.holder;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.syiyi.annotation.Holder;
import com.syiyi.library.MultiStyle;
import com.syiyi.multistyle.MainActivity;
import com.syiyi.multistyle.R;

/**
 * 图片
 * Created by songlintao on 2017/1/19.
 */

@Holder
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
    public void renderView(MultiStyle.IProxy proxy, int pos, MultiStyle.OnClickListener listener) {
        MainActivity.Content item = (MainActivity.Content) proxy.getItem(pos);
        ImageView image = mHelper.getView(R.id.image);
        Glide.with(mHelper.getContext()).load(item.getContent()).into(image);

    }
}
