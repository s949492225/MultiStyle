package com.syiyi.multistyle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.syiyi.holder.H;
import com.syiyi.library.DiffHelper;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.MultiViewModel;
import com.syiyi.library.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static List<Content> DATA = new ArrayList<>();
    static final int TYPE_TEXT = H.id.TextHolder_com_syiyi_multistyle_holder;
    static final int TYPE_IMAGE = H.id.ImageHolder_com_syiyi_multistyle_holder;

    static {
        DATA.add(new Content(TYPE_TEXT, "hello"));
//        DATA.add(new Content(TYPE_IMAGE, "http://a.hiphotos.baidu.com/image/pic/item/5d6034a85edf8db1ab6b738c0d23dd54574e7494.jpg"));
        DATA.add(new Content(TYPE_TEXT, "I"));
//        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/f7246b600c338744e7162094550fd9f9d62aa002.jpg"));
        DATA.add(new Content(TYPE_TEXT, "am"));
//        DATA.add(new Content(TYPE_IMAGE, "http://e.hiphotos.baidu.com/image/pic/item/8cb1cb1349540923592e4e479758d109b3de4947.jpg"));
        DATA.add(new Content(TYPE_TEXT, "super"));
//        DATA.add(new Content(TYPE_IMAGE, "http://b.hiphotos.baidu.com/image/pic/item/7acb0a46f21fbe09022d2ecb6f600c338644adfa.jpg"));
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

    private MultiStyleAdapter adapter;
    private DiffHelper<Content> mDiffHelper;
    private Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewHelper mHelper = new ViewHelper(this.getWindow().getDecorView().findViewById(android.R.id.content), this);
        RecyclerView list = mHelper.getView(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MultiStyleAdapter() {};
        mDiffHelper=new DiffHelper<>(adapter);
        mDiffHelper.setOperationListener(new DiffHelper.OnOperationListener(){
            @Override
            public void onStart() {
                Log.d("xxxxxxxx", "onStart: ");
            }

            @Override
            public void onComplete() {
                Log.d("xxxxxxxx", "onComplete: ");
            }
        });

        MultiStyleAdapter.setDebug(true);
        list.setAdapter(adapter);
        mDiffHelper.setList(DATA);

    }

    public void onClick(View view) {
        if (random == null) {
            random = new Random(1000);
        }
        Content c1 = new Content(TYPE_TEXT, "今天天气真好" + random.nextInt(1000));

        Content c2 = new Content(TYPE_IMAGE, "http://b.hiphotos.baidu.com/image/pic/item/7acb0a46f21fbe09022d2ecb6f600c338644adfa.jpg");
        c2.text = "我是2";

        Content c3 = new Content(TYPE_TEXT, "今天好" + random.nextInt(1000));

        List<Content> temp = new ArrayList<>();
//        temp.add(c1);
//        temp.add(c2);
        for (int i=0;i<20;i++){
            Content a = new Content(TYPE_TEXT, "今天天气真好"+random.nextInt(1000) + random.nextInt(1000));
            temp.add(a);
        }
//        //insert
//        adapter.insertOne(c1);
//        adapter.insertOne(1,c1);
//        adapter.insertList(temp);
        mDiffHelper.insertList(6,temp);
//
//        //remove
//        adapter.removeList(0, 1);
//        adapter.removeList(0, 2);
//        adapter.removeList(1, 2);
//        adapter.removeFirst();
//        adapter.removeLast();
//
//        //update
//        List<Content> newList = new ArrayList<>();
//        newList.add(c1);
//        newList.add(c3);
//
//        //批量更新
//        List<Content> oldList = new ArrayList<>();
//        Content d1 = adapter.getItem(1);
//        c1.id = d1.id;
//        Content d2 = adapter.getItem(2);
//        c3.id = d2.id;
//
//        oldList.add(d1);
//        oldList.add(d2);
//
//        adapter.updateList(oldList, newList);
    }

    public static class Content implements MultiViewModel {

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
        public boolean areContentsTheSame(MultiViewModel newModel) {
            if (!(newModel instanceof Content))
                return false;
            Content temp = (Content) newModel;
            return content.equals(temp.getContent());
        }

        @Override
        public Object getChangePayload(MultiViewModel newModel) {
            Content temp = (Content) newModel;
            return temp.getContent();
        }

        @Override
        public boolean areItemsTheSame(MultiViewModel newModel) {
            if (!(newModel instanceof Content))
                return false;
            Content temp = (Content) newModel;
            return id.equals(temp.id);
        }

        @Override
        public void resetPlayLoadData(MultiViewModel newModel) {
            Content temp = (Content) newModel;
            content = temp.getContent();
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
