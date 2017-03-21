package com.syiyi.library;

import java.util.HashMap;
import java.util.Map;

/**
 * Tags方法代理
 * Created by mac on 17/3/6.
 */

public class TagsProxy
{
    private Map<String, Object> mTags = new HashMap<>();

    public void setTag(String key, Object value){
        mTags.put(key, value);
    }

    public void setTags(Map<String, Object> tags){
        mTags.putAll(tags);
    }

    public Object getTag(String key){
        return mTags.get(key);
    }

    public Map<String, Object> tags(){
        return mTags;
    }

    public void clearTags(){
        mTags.clear();
    }
}
