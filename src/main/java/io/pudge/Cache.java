package io.pudge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xq on 23/02/2017.
 */
public class Cache {
    int id;
    int size;
    List<Video> videoList = new ArrayList<>();
    Map<Integer, Integer> latencyToEndpoint = new HashMap<>();

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

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public Map<Integer, Integer> getLatencyToEndpoint() {
        return latencyToEndpoint;
    }

    public void setLatencyToEndpoint(Map<Integer, Integer> latencyToEndpoint) {
        this.latencyToEndpoint = latencyToEndpoint;
    }
}
