package com.syiyi.multistyle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.syiyi.holder.ViewHolderHelper;
import com.syiyi.library.MultiStyle;
import com.syiyi.library.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static List<Content> DATA = new ArrayList<>();
    static final int TYPE_TEXT = ViewHolderHelper.id_TextHolder_com_syiyi_multistyle_holder;
    static final int TYPE_IMAGE = ViewHolderHelper.id_ImageHolder_com_syiyi_multistyle_holder;

    static {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewHelper mHelper = new ViewHelper(this.getWindow().getDecorView().findViewById(android.R.id.content), this);
        RecyclerView list = mHelper.getView(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setAdapter(new MultiStyle.RecycleViewAdapter(this, new MultiStyle.IProxy() {
            @Override
            public int getCount() {
                return DATA.size();
            }

            @Override
            public Object getItem(int pos) {
                return DATA.get(pos);
            }

            @Override
            public int getItemViewType(int position) {
                return DATA.get(position).getViewType();
            }

            @Override
            public void addData(Object o) {
                //ignore
            }

            @Override
            public void clear() {
                //ignore
            }
        }));

    }

    public static class Content {

        Content(int viewType, String content) {
            this.viewType = viewType;
            this.content = content;
        }

        private int viewType;
        private String content;

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
