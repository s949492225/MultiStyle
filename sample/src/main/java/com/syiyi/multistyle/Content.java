package com.syiyi.multistyle;

import com.syiyi.holder.H;
import com.syiyi.library.MultiViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * model
 * Created by songlintao on 2017/12/4.
 */
public class Content implements MultiViewModel {

    public String id;

    Content(int viewType, String content) {
        this.viewType = viewType;
        this.content = content;
        id = UUID.randomUUID().toString();
    }

    private int viewType;
    private String content;
    public String text = "default";


    public String getContent() {
        return content;
    }


    @Override
    public int getViewTypeId() {
        return viewType;
    }

    @Override
    public String getViewTypeName() {
        return null;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    static final int TYPE_TEXT = H.id.TextHolder_com_syiyi_multistyle_holder;
    private static final int TYPE_IMAGE = H.id.ImageHolder_com_syiyi_multistyle_holder;

    static List<Content> DATA = new ArrayList<>();


    static {
        DATA.add(new Content(TYPE_TEXT, "hello"));
        DATA.add(new Content(TYPE_TEXT, "I"));
        DATA.add(new Content(TYPE_TEXT, "am"));
        DATA.add(new Content(TYPE_TEXT, "super"));
        DATA.add(new Content(TYPE_TEXT, "man"));
        DATA.add(new Content(TYPE_TEXT, "hello"));
        DATA.add(new Content(TYPE_IMAGE, "http://a.hiphotos.baidu.com/image/pic/item/5d6034a85edf8db1ab6b738c0d23dd54574e7494.jpg"));
        DATA.add(new Content(TYPE_TEXT, "I"));
        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/f7246b600c338744e7162094550fd9f9d62aa002.jpg"));
        DATA.add(new Content(TYPE_TEXT, "am"));
        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/8cb1cb1349540923592e4e479758d109b3de4947.jpg"));
        DATA.add(new Content(TYPE_TEXT, "super"));
        DATA.add(new Content(TYPE_IMAGE, "http://b.hiphotos.baidu.com/image/pic/item/7acb0a46f21fbe09022d2ecb6f600c338644adfa.jpg"));
        DATA.add(new Content(TYPE_TEXT, "man"));
        DATA.add(new Content(TYPE_TEXT, "hello"));
        DATA.add(new Content(TYPE_IMAGE, "http://a.hiphotos.baidu.com/image/pic/item/5d6034a85edf8db1ab6b738c0d23dd54574e7494.jpg"));
        DATA.add(new Content(TYPE_TEXT, "I"));
        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/f7246b600c338744e7162094550fd9f9d62aa002.jpg"));
        DATA.add(new Content(TYPE_TEXT, "am"));
        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/8cb1cb1349540923592e4e479758d109b3de4947.jpg"));
        DATA.add(new Content(TYPE_TEXT, "super"));
        DATA.add(new Content(TYPE_IMAGE, "http://b.hiphotos.baidu.com/image/pic/item/7acb0a46f21fbe09022d2ecb6f600c338644adfa.jpg"));
        DATA.add(new Content(TYPE_TEXT, "man"));
    }
}
