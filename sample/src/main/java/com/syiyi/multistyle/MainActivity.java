package com.syiyi.multistyle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.syiyi.library.DiffHelper;
import com.syiyi.library.MultiStyleAdapter;
import com.syiyi.library.ViewHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.syiyi.multistyle.Content.*;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private MultiStyleAdapter<Content> adapter;
    private DiffHelper<Content> mDiffHelper;
    private Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewHelper mHelper = new ViewHelper(this.getWindow().getDecorView().findViewById(android.R.id.content), this);
        RecyclerView list = mHelper.getView(R.id.list);
        list.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MultiStyleAdapter<Content>();
        list.setAdapter(adapter);
        mDiffHelper = new DiffHelper<Content>(adapter, new ContentDiff());
        mDiffHelper.setList(DATA);

    }

    public void onClick(View view) {
        add();
    }

    private void add() {
        if (random == null) {
            random = new Random(1000);
        }

        List<Content> temp = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Content a = new Content(TYPE_TEXT, i + "气真好" + random.nextInt(1000) + random.nextInt(1000));
            temp.add(a);
        }

        List<Content> newDatas = mDiffHelper.createNewDatas();
        newDatas.addAll(temp);
        mDiffHelper.batchOperate(newDatas);
    }

    private void remove(){
        mDiffHelper.removeList(0, 1);
    }

    private void update()
    {
        Content old = mDiffHelper.getItemByPos(0);
        Content newData = new Content(old.getViewTypeId(), "dddddd");
        newData.id = old.id;

        mDiffHelper.updateOne(old, newData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
