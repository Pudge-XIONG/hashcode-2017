package io.pudge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xq on 23/02/2017.
 */
public class Video {
    int id;
    int size;

    List<Cache> cacheList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Cache> getCacheList() {
        return cacheList;
    }

    public void setCacheList(List<Cache> cacheList) {
        this.cacheList = cacheList;
    }
}
